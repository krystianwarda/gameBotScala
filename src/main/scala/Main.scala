
import cavebot.{CaveBot, core}
import cavebot.core.{followWaypoints, locateLoot, moveToNextWaypoint, recordWaypoints}

import radar.core.findCharLocation
import userUI.SwingApp
import utils.image.getLocationFromImageMidMatchTemp
import utils.mouse.{leftClick, rightClick}

import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.Source
//import scala.jdk.CollectionConverters._
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import org.opencv.core.CvType
import utils.image.{loadImage, loadImage3, loadOpenCVSettings, makeScreenshot, makeScreenshotID, makeScreenshotMat, refreshWindow}
import player.Player

import scala.concurrent.ExecutionContext.Implicits.global
import credentials._
//import radar.core.locatePosition
import utils.mouse.mouseMoveSmooth
//import player.Player.{eatFood, foodStatus}
//import player.core.checkSkills
import utils.core.{detectPlayerWindows, getCurrentTimestamp, getWindowId, maximizeWindow}
import utils.keyboard.sendText
import utils.gameScreen
import cavebot.CaveBot
//import player.Spells
//import scala.collection.immutable.Function1
//import userUI.swingApp
import userUI.ExampleApp
import player.Example
object Main {


  def main(args: Array[String]): Unit = {
    val examplesList = List(
      new player.Example("Alice", 1),
      new player.Example("Bob", 2),
      new player.Example("Charlie", 3)
    )

    val exampleApp = ExampleApp(examplesList)
    exampleApp.visible = true

//    val app = new userUI.swingApp

//    loadOpenCVSettings()

//    gameScreen.setWindowName(windowName)
//    gameScreen.setWindowId(getWindowId(windowName))


//    val captureInterval = 1000 // capture interval in milliseconds
//    val cascadeClassifier = new CascadeClassifier("path/to/your/cascade/classifier.xml") // path to your cascade classifier file
//    val scaleFactor = 1.2 // scale factor for object detection
//    val minNeighbors = 3 // minimum number of neighbors for object detection
//    makeScreenshot(windowName)
//

//    val playersList = detectPlayerWindows(windowName)
//    val app = new SwingApp(playersList)
//    app.main(Array())
//
//    while (true) {
//      for (singlePlayer <- playersList) {
//        maximizeWindow(singlePlayer.windowID)
//        singlePlayer.updateGeneral()
//        println(singlePlayer.characterName)
//        println(singlePlayer.charLevel)
//        println(singlePlayer.botLightHealSpell)
//        println(singlePlayer.botLightHealHealth)
//        println(singlePlayer.botLightHealMana)
//        Thread.sleep(10000)
//      }
//    }

//      println("Start...")
//      for (singlePlayer <- playersList) {
//        println(singlePlayer.characterName)
//        maximizeWindow(singlePlayer.windowID)
//        singlePlayer.updateGeneral()
//      }
//    }
//




//        // training
//        singlePlayer.checkSkills()
//        singlePlayer.rotationStatus()
//        if (singlePlayer.characterName == "Datwardaguy") {
//          singlePlayer.foodStatus(loadImage(s"window_${singlePlayer.characterName}.png"))
//          if (singlePlayer.getManaPoints > 25) {Spells.castSpellMultiple("utevo lux", 1)}
//        }



  }
}


// cavebot

//        var caveBotTestClass = new CaveBot("testPath")
////         create a cavebot path
//        for (i <- 1 to 4) {
//          singlePlayer.updateGeneral()
//          recordWaypoints(singlePlayer, caveBotTestClass)
//          Thread.sleep(5000)
//        }
//        caveBotTestClass.saveStateToFile(caveBotTestClass.getCaveBotName())
//        println("Saved")
//        Thread.sleep(20000)

//var caveBotTestClass = new CaveBot("testPath")
//caveBotTestClass.loadStateFromFile("classes/cavebot/testPath.ser")
//println("In 3 seconds cavebot initialization.")
//Thread.sleep(4000)
//for (i <- 1 to 4) {
//  singlePlayer.updateGeneral()
//  followWaypoints(singlePlayer, caveBotTestClass)
//  Thread.sleep(4000)
//}
//print("Finished the route")



//autoheal
//        singlePlayer.autoheal(exura =Some(230), IH=Some(170))

//Create lootbag
//        locateLoot(singlePlayer)