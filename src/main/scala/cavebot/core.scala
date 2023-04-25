package cavebot

import utils.image.{getCenterLoc, getCenterPoint, getLocationFromImage, getLocationFromImageMid, getSideFromFilename, loadImage}
import org.opencv.core.Mat
import utils.mouse.{calcLocOffset, mouseDrag, mouseMoveSmooth, rightClick, shiftClick}
import scala.io.StdIn
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object core {

  def createLootbag(): Unit = {

  }

  def locateLootbag(mainImage: Mat): Unit = {
    var bagWindowImage = loadImage("images/containers/bagWindow.png")
    var bagImage = loadImage("images/containers/brownBag.png")
    var center = loadImage("images/screenInfo/center.png")
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
