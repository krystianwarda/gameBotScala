package player

import player.battle.{createRectangle, numberDetection}
import utils.image.{extractRectangle, mouseOverRectangle, getLocationFromImage3, getLocationFromImageMidLeft, loadImage, saveMatAsPng}
import utils.mouse.mouseMoveSmooth
import player.Player

import java.awt.Robot
import java.awt.event.KeyEvent
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.io.Source

object core {

  def checkSkills(): Unit = {
    // check skills sections
    var mainImage = loadImage("window.png")
    val stringList = List("charExperience", "charLevel", "healthPoints", "manaPoints", "soulPoints", "capacityValue", "magicLevel")

    for (str <- stringList) {
      var tempImage = loadImage(s"images/battle/sections/$str.png")
      println(str)
      var tempLoc = getLocationFromImageMidLeft(tempImage, mainImage)
      try {
        mouseMoveSmooth(tempLoc)
      } catch {
        case e: Exception =>
          println("Not found: " + e.getMessage)
      }
    }
  }

  def checkMana(): Unit = {
    // check skills sections
    var mainImage = loadImage("window.png")
    val stringList = List("manaPoints")

    for (str <- stringList) {
      var tempImage = loadImage(s"images/battle/sections/$str.png")
      println(str)
      var tempLoc = getLocationFromImageMidLeft(tempImage, mainImage)
      try {
        mouseMoveSmooth(tempLoc)
      } catch {
        case e: Exception =>
          println("Not found: " + e.getMessage)
      }

      // call createRectangle to get the rectangle coordinates
      val rectangleCoords: Option[(Option[(Int, Int)], Option[(Int, Int)], Option[(Int, Int)], Option[(Int, Int)])] = createRectangle(tempLoc)
      mouseOverRectangle(rectangleCoords)
      var rectangleImage = extractRectangle(mainImage, rectangleCoords)
      saveMatAsPng(mainImage, str)
      Player.updateState(str, numberDetection(rectangleImage))

    }
  }

}


