package cavebot

import utils.image.{arrayToMat, cropCenter, cropImageRect, fromBytes, getCenterLoc, getCenterPoint, getLocationFromCroppedImage, getLocationFromImage, getLocationFromImageHashMidMatchLowConf, getLocationFromImageMid, getLocationFromImageMidEdgeDetect, getLocationFromImageMidLeft, getLocationFromImageMidMatchTemp, getLocationFromImageMidMatchTempLowConf, getLocationFromImagePoint, getLocationFromImageRight, getSideFromFilename, hashMat, hashit, hashitHex, loadImage, makeScreenshotMat, matToArray, saveMatToFile, singleCutWindow}
import org.opencv.core.{Mat, Point, Rect, Size}
import utils.mouse.{calcLocOffset, leftClick, mouseDrag, mouseDragSmooth, mouseMoveSmooth, rightClick, shiftClick}

import scala.io.StdIn
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import org.opencv.imgproc.Imgproc
import player.Player

import java.io.{File, FileInputStream, ObjectInputStream}
//import cavebot.CaveBot

object core {

  // recordMove method
//  def recordMove(playerClass: Player, caveBotClass: CaveBot): Array[Array[Int]] = {
//    val radarImage = playerClass.getRadarImage()
//    val radarArray = matToArray(radarImage)
//    caveBotClass.addWaypointArray(radarArray)
//    println("recorded a step")
//    radarArray
//  }

  def recordMove(selectedPlayer: Player, selectedCaveBot: CaveBot): Array[Array[Int]] = {
    val radarImage = cropCenter(selectedPlayer.getRadarImage(), 60,60)
    val radarArray = matToArray(radarImage)
    radarArray
  }

//  def recordMove(playerClass: Player, caveBotClass: CaveBot): Unit = {
//    var radarImage = playerClass.getRadarImage()
//    var radarArray = matToArray(radarImage)
//    caveBotClass.addWaypointArray(radarArray)
//    println("recorded a step")
//  }

  def recordRope(playerClass: Player, caveBotClass: CaveBot): Unit = {
    var radarImage = playerClass.getRadarImage()
    var radarArray = matToArray(radarImage)
    caveBotClass.addWaypointArray(radarArray)
    println("recorded a step")
  }

  def followWaypoints(playerClass: Player, caveBotClass: CaveBot): Unit = {
    println("inside function followWaypoints")
    val nextWaypoint = caveBotClass.getNextWaypoint().getOrElse(Array.empty[Array[Int]])
    println(s"nextWaypoint: $nextWaypoint") // Debugging line
    if (nextWaypoint.nonEmpty) {
      println("load a step")
      moveToNextWaypoint(playerClass, nextWaypoint)
      playerClass.updateWaypointStatus(System.currentTimeMillis)
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

  def getBattlePositions(charWindow: Mat, battleLoc: Option[(Int, Int)], yOffset: Int, yDiff: Int, a: Int, b: Int): List[Mat] = {
    battleLoc match {
      case Some((x, y)) =>
        val rectangles = new ListBuffer[Mat]()
        val startPoint = new Point(x, y + yOffset)

        for (i <- 0 until 4) {
          val newLocY = startPoint.y + i * yDiff
          val rect = new Rect(new Point(startPoint.x, newLocY), new Size(b, a))
          val croppedImage = new Mat(charWindow, rect)
          rectangles += croppedImage
        }

        rectangles.toList

      case None =>
        println("Battle location not found.")
        List.empty[Mat]
    }
  }
  def detectMonsters(battlePositions: List[Mat]): List[(String, Boolean)] = {
    battlePositions.zipWithIndex.map { case (battlePosition, index) =>
      val (wordDetected: String, monsterMarked: Boolean) = battleLetterDetection(battlePosition)
//      if (wordDetected.isEmpty) {
//        println(s"In battle ${index + 1} nothing was found")
//      } else {
//        println(s"In battle ${index + 1}: $wordDetected, Marked: $monsterMarked")
//      }
      (wordDetected, monsterMarked)
    }
  }

  def battleLetterDetection(mainImg: Mat): (String, Boolean) = {
    val letterPath = "images/battle/letters"
    val letterFiles = new File(letterPath).listFiles.filter(f => f.getName.matches("^[a-zA-Z][rR]*\\.png$"))
    val letters: Map[String, Mat] = letterFiles.map(f => (f.getName.dropRight(4), loadImage(f.getAbsolutePath))).toMap

    // Remove any null Mat objects
    val nonNullLetters = letters.filter(_._2 != null)

    // Define confidence threshold for matching
    val confidence: Double = 0.99

    def findMatches(lettersToSearch: Map[String, Mat]): Seq[(Int, String, Int, Double, Int)] = {
      lettersToSearch.flatMap { case (letter, letterImg) =>
        val matchMat: Mat = new Mat()
        Imgproc.matchTemplate(mainImg, letterImg, matchMat, Imgproc.TM_CCOEFF_NORMED)

        val letterLocations = for {
          row <- 0 until matchMat.rows
          col <- 0 until matchMat.cols
          if matchMat.get(row, col)(0) >= confidence
        } yield (col, letter, row, matchMat.get(row, col)(0), letterImg.width())

        letterLocations
      }.toSeq
    }

    def groupNearbyLetters(matches: Seq[(Int, String, Int, Double)], distanceThreshold: Int): Seq[(Int, String, Int, Double)] = {
      val sortedMatches = matches.sortBy(_._1)
      val filteredMatches = new ArrayBuffer[(Int, String, Int, Double)]()

      sortedMatches.foreach { currentMatch =>
        val (x1, letter1, y1, score1) = currentMatch
        var isOverlapping = false

        filteredMatches.indices.foreach { i =>
          val (x2, letter2, y2, score2) = filteredMatches(i)

          if (math.abs(x1 - x2) <= distanceThreshold) {
            isOverlapping = true

            if (score1 > score2) {
              filteredMatches(i) = currentMatch
            }
          }
        }

        if (!isOverlapping) {
          filteredMatches.append(currentMatch)
        }
      }

      filteredMatches.sortBy(_._1)
    }

    // Determine the color of the first letter
    val firstLetterColor = nonNullLetters.keys.find(key => key.length > 1 && findMatches(Map(key -> nonNullLetters(key))).nonEmpty)

    val isRed = firstLetterColor.isDefined
    val searchLetters = if (isRed) {
      nonNullLetters.filter(_._1.length > 1)
    } else {
      nonNullLetters.filter(_._1.length == 1)
    }

    val matches = findMatches(searchLetters)

    // Calculate the average width of the detected letters
    val avgWidth = matches.map(_._5).sum / matches.size.toDouble

    // Group nearby letters and select the one with the highest confidence score
    val groupedMatches = groupNearbyLetters(matches.map(t => (t._1, t._2, t._3, t._4)), (avgWidth * 0.4).toInt)
//0.4 mino

    // Sort matches by x-coordinate
    val sortedMatches = groupedMatches.sortBy(_._1)

    // Combine sorted matches into a single string
    val wordDetected = new StringBuilder
    sortedMatches.foreach(m => wordDetected.append(m._2.charAt(0)))

    (wordDetected.toString(), isRed)
  }

}
