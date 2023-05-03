package cavebot

import utils.image.{arrayToMat, cropImageRect, fromBytes, getCenterLoc, getCenterPoint, getLocationFromCroppedImage, getLocationFromImage, getLocationFromImageHashMidMatchLowConf, getLocationFromImageMid, getLocationFromImageMidEdgeDetect, getLocationFromImageMidLeft, getLocationFromImageMidMatchTemp, getLocationFromImageMidMatchTempLowConf, getLocationFromImagePoint, getLocationFromImageRight, getSideFromFilename, hashMat, hashit, hashitHex, loadImage, makeScreenshotMat, matToArray, saveMatToFile, singleCutWindow}
import org.opencv.core.Mat
import utils.mouse.{calcLocOffset, leftClick, mouseDrag, mouseDragSmooth, mouseMoveSmooth, rightClick, shiftClick}

import scala.io.StdIn
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import org.opencv.core.Rect
import player.Player

import java.io.{File, FileInputStream, ObjectInputStream}
//import cavebot.CaveBot

object core {


  def recordMove(playerClass: Player, caveBotClass: CaveBot): Unit = {
    var radarImage = playerClass.getRadarImage()
    var radarArray = matToArray(radarImage)
    caveBotClass.addWaypointArray(radarArray)
    println("recorded a step")
  }

  def recordRope(playerClass: Player, caveBotClass: CaveBot): Unit = {
    var radarImage = playerClass.getRadarImage()
    var radarArray = matToArray(radarImage)
    caveBotClass.addWaypointArray(radarArray)
    println("recorded a step")
  }

  def followWaypoints(playerClass: Player, caveBotClass: CaveBot): Unit = {
    val nextWaypoint = caveBotClass.getNextWaypoint().getOrElse(Array.empty[Array[Int]])
    if (nextWaypoint.nonEmpty) {
      moveToNextWaypoint(playerClass, nextWaypoint)
      println("load a step")
    } else {
      println("No more waypoints.")
    }
  }
  def moveToNextWaypoint(playerClass: Player, tempWaypoints: Array[Array[Int]]): Unit = {
    var charWindow = playerClass.getCharWindow()
    var matFile =arrayToMat(tempWaypoints)
    saveMatToFile(matFile, "loadedWaypoint")
    var nextMoveLoc = getLocationFromImageMidMatchTempLowConf(matFile, charWindow)
    leftClick(playerClass.getRobot(), nextMoveLoc)
  }

  def locateLoot(playerClass: Player): Unit = {

    var mainImage = playerClass.getCharWindow()
    val centerLoc = Some((767,500))
    var chatSettingImage = loadImage("images/screenInfo/chatSettings.png")
    var equipmentAreaPoints = getLocationFromImageRight(chatSettingImage, mainImage)
    mouseMoveSmooth(playerClass.getRobot(), equipmentAreaPoints)
    //    var equipmentArea = singleCutWindow(mainImage, equipmentAreaPoints, "right")
//    var croppedImage = cropImageRect(mainImage, equipmentArea)
    dropLoot(playerClass, equipmentAreaPoints, centerLoc)
    openBag(playerClass, equipmentAreaPoints)
    dropLoot(playerClass, equipmentAreaPoints, centerLoc)
  }

  def openBag(playerClass: Player, edgePoint: Option[(Int, Int)]): Unit = {
    var bagImage = loadImage("images/containers/brownBag.png")
//    var tempLoc = getLocationFromImagePoint(bagImage, mainImage, edgePoint, "right")
    var tempLoc = getLocationFromImageMidMatchTemp(bagImage, playerClass.getCharWindow)
    mouseMoveSmooth(playerClass.getRobot(), tempLoc)
    println("Bag found")
    rightClick(playerClass.getRobot(), tempLoc)
    playerClass.updateCharWindow()
  }

  def dropLoot(playerClass: Player, edgePoint: Option[(Int, Int)], centerLoc: Option[(Int, Int)]): Unit = {

    val lootList = List("mace", "sword", "plate_shield", "chain_armor", "brass_helmet")
    for (str <- lootList) {
      var tempImage = loadImage(s"images/loot/$str.png")
//      var tempLoc = getLocationFromImagePoint(tempImage, mainImage, edgePoint, "right")
      var tempLoc = getLocationFromImageMidMatchTemp(tempImage, playerClass.getCharWindow)

      if (tempLoc.isEmpty) {
        None
      } else {
        println(str)
        mouseDragSmooth(playerClass.getRobot(), tempLoc, centerLoc)
        playerClass.updateCharWindow()
      }
    }
  }

  def locateLootbag(playerClass: Player, mainImage: Mat): Unit = {
    var bagWindowImage = loadImage("images/containers/bagWindow.png")
    var bagImage = loadImage("images/containers/brownBag.png")
//    var center = loadImage("images/screenInfo/center.png")
    var bagWindowLoc = getLocationFromImageMid(bagWindowImage, mainImage)
    var firstPlace = getLocationFromImageMid(bagImage, mainImage)
    var secondPlace = calcLocOffset(bagWindowLoc, 45, 40)


//    val centerLoc = getLocationFromImageMid(center, mainImage)

    val centerLoc = Some((767,500))
//    val centerLoc = getCenterLoc(mainImage)
//    println(centerLoc)
//    mouseMoveSmooth(centerLoc)

    while (true) {
      for (i <- 1 to 7) {
        Thread.sleep(600)
        if (i == 1) {
          mouseDrag(playerClass.getRobot(), centerLoc, secondPlace)
        } else {
          mouseDrag(playerClass.getRobot(), centerLoc, firstPlace)
        }
      }

      var lastPlace = calcLocOffset(firstPlace, 110, 45)
      mouseMoveSmooth(playerClass.getRobot(), lastPlace)
      Thread.sleep(600)
      rightClick(playerClass.getRobot(), lastPlace)

    }
  }

//  def loadCaveBots(): List[CaveBot] = {
//        val caveBotList = List(
//          CaveBot("Cave Bot A", List(Mat("Mat 1"), Mat("Mat 2"), Mat("Mat 3"))),
//          CaveBot("Cave Bot B", List(Mat("Mat 4"), Mat("Mat 5"), Mat("Mat 6")))
//        )
//  }

  def loadCaveBots(): List[CaveBot] = {
    val directoryPath = "classes/cavebot"
    val directory = new File(directoryPath)
    val files = directory.listFiles.filter(_.isFile)
    println(s"Found ${files.length} files in directory $directoryPath")

    val caveBots = files.flatMap { file =>
      println(file)
      val objectInputStream = new ObjectInputStream(new FileInputStream(file))
      try {
        objectInputStream.readObject() match {
          case caveBot: CaveBot => Some(caveBot)
          case _ => None
        }
      } catch {
        case ex: Exception =>
          println(s"Error while loading CaveBot from file ${file.getName}: ${ex.getMessage}")
          None
      } finally {
        objectInputStream.close()
      }
    }.toList
    println(s"Loaded ${caveBots.length} CaveBots")
    caveBots
  }




}
