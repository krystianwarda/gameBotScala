package cavebot

import utils.image.{cropImageRect, getCenterLoc, getCenterPoint, getLocationFromCroppedImage, getLocationFromImage, getLocationFromImageMid, getLocationFromImageMidEdgeDetect, getLocationFromImageMidLeft, getLocationFromImageMidMatchTemp, getLocationFromImagePoint, getLocationFromImageRight, getSideFromFilename, loadImage, makeScreenshotMat, singleCutWindow}
import org.opencv.core.Mat
import utils.mouse.{calcLocOffset, mouseDrag, mouseDragSmooth, mouseMoveSmooth, rightClick, shiftClick}

import scala.io.StdIn
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import org.opencv.core.Rect
import player.Player
object core {

  def locateLoot(playerClass: Player): Unit = {

    var mainImage = playerClass.getCharWindow()
    val centerLoc = Some((767,500))
    var chatSettingImage = loadImage("images/screenInfo/chatSettings.png")
    var equipmentAreaPoints = getLocationFromImageRight(chatSettingImage, mainImage)
    mouseMoveSmooth(equipmentAreaPoints)
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
    mouseMoveSmooth(tempLoc)
    println("Bag found")
    rightClick(tempLoc)
    playerClass.updateCharWindow(makeScreenshotMat(playerClass.windowID, playerClass.characterName))
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
        mouseDragSmooth(tempLoc, centerLoc)
        playerClass.updateCharWindow(makeScreenshotMat(playerClass.windowID, playerClass.characterName))
      }
    }
  }

  def locateLootbag(mainImage: Mat): Unit = {
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
          mouseDrag(centerLoc, secondPlace)
        } else {
          mouseDrag(centerLoc, firstPlace)
        }
      }

      var lastPlace = calcLocOffset(firstPlace, 110, 45)
      mouseMoveSmooth(lastPlace)
      Thread.sleep(600)
      rightClick(lastPlace)

    }
  }
}
