package utils

import io.circe.generic.auto.exportEncoder

import java.awt.event.{InputEvent, KeyEvent, MouseEvent}
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener

import java.awt.{Desktop, Robot, Toolkit}
import java.awt.event.KeyEvent
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import java.awt.KeyboardFocusManager
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source
import java.time.Instant
import java.awt.{Rectangle, Robot}
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.{File, FileOutputStream, ObjectOutputStream}
import javax.imageio.ImageIO
import scala.collection.JavaConverters._
import org.opencv.imgproc.Imgproc
import org.opencv.imgcodecs.Imgcodecs
import io.circe.syntax._

import scala.util.Random
import scala.concurrent.duration._
import scala.sys.process.stringSeqToProcess
import utils.gameScreen._
import utils.gameScreen

import java.awt.Robot
import player.Player
import radar.core.{cutRadarImage, getRadarCenterLoc}
//import radar.core.locatePosition
import utils.image.{getCenterLoc, loadImage, makeScreenshot, makeScreenshotID}
import javax.swing.SwingWorker

import java.nio.charset.StandardCharsets
import io.circe._
import io.circe.generic.semiauto._

object core {

    def runBot(playersList: List[player.Player], runningBot: Boolean): Unit = {

        while (runningBot) {
            for (singlePlayer <- playersList) {
                if (singlePlayer.threadActivation) {
                    singlePlayer.saveClass()
                    singlePlayer.startInputHandling()
                    singlePlayer.setThreaad(false)
                }
                maximizeWindow(singlePlayer.windowID)
                singlePlayer.updateGeneral()
                println(singlePlayer.getHealthPoints())
                println(singlePlayer.getManaPoints())
            }
        }
    }

    def detectPlayerWindows(windowNameSubstring: String): List[Player] = {
        // Search for windows that match the window name substring
        val searchCommand = Seq("xdotool", "search", "--name", windowNameSubstring)
        val searchProcess = new ProcessBuilder(searchCommand.toList.asJava).start()
        val windowIDs = Source.fromInputStream(searchProcess.getInputStream()).getLines().toList

        // Extract character names and create PlayerWindow objects
        windowIDs.map { windowID =>
            maximizeWindow(windowID)
            Thread.sleep(100)
            val robot = new Robot()
            val windowTitle = Seq("xdotool", "getwindowname", windowID).!!.trim
            val characterName = windowTitle.split(" - ")(1).replaceAll("\\s", "_")
            var screenshotPath = makeScreenshotID(windowID, characterName)
            var characterWindow = loadImage(screenshotPath)
            var centerLoc = getCenterLoc(characterWindow)
            var radarImage = cutRadarImage(characterWindow)
            var radarCenterLoc = getRadarCenterLoc(characterWindow)
            new Player(windowID, characterName,characterWindow, centerLoc, radarImage, radarCenterLoc, robot)
        }
    }
    def detectWindows(windowName: String): List[String] = {
        // Window name
        val windowSubstring = windowName

        // Search for windows that match the window name substring
        val searchCommand = Seq("xdotool", "search", "--name", windowSubstring)
        val searchProcess = new ProcessBuilder(searchCommand.toList.asJava).start()
        val windowIDs = Source.fromInputStream(searchProcess.getInputStream()).getLines().toList

        windowIDs
    }


    def getCurrentTimestamp: Long = System.currentTimeMillis / 1000
        // Instant.now().getEpochSecond


    def getWindowId(windowSubstring: String): String = {
        val processBuilder = new ProcessBuilder(Seq("xdotool", "search", "--name", windowSubstring).toList.asJava)
        val windowID = Source.fromInputStream(processBuilder.start().getInputStream()).mkString.trim()
        windowID
    }


    def maximizeWindow(windowID: String): Unit = {
        // Check if the window is minimized
        val stateCommand = Seq("xdotool", "getwindowgeometry", windowID)
        val stateOutput = stateCommand.!!.trim
        val isMinimized = stateOutput.contains("minimized")

        if (isMinimized) {
            // Restore the window
            Seq("xdotool", "windowmap", windowID).!!
            Thread.sleep(500)
        }

        // Toggle to the desired window
        Seq("xdotool", "windowactivate", windowID).!!

        // Maximize the window
        val screenSize = Toolkit.getDefaultToolkit.getScreenSize
        val maxCommand = Seq("xdotool", "windowsize", windowID, screenSize.getWidth.toInt.toString, screenSize.getHeight.toInt.toString)
        val maxProcess = new ProcessBuilder(maxCommand.toList.asJava).start()
        Await.result(Future(maxProcess.waitFor()), 10.seconds)
    }

//    def randomSleep(): Unit = {
//        val randomTime = (2 + Random.nextInt(3)).seconds
//        Thread.sleep(randomTime.toMillis)
//    }

    def randomNumber(baseNumber: Int, randomNumber1: Int, randomNumber2: Int): Int = {
        baseNumber + Random.nextInt(randomNumber2 - randomNumber1 + 1) + randomNumber1
    }
//    def randomNumber(baseNumber: Int, randomNumber1: Int, randomNumber2: Int): Int = {
//        baseNumber + Random.between(randomNumber1, randomNumber2 + 1)
//    }

    def randomDirection(): String = {
        val directions = Array("left", "right", "top", "bottom")
        val randomIndex = Random.nextInt(directions.length)
        directions(randomIndex)
    }

    def randomSleep(baseTime: Double, randomTime1: Double, randomTime2: Double): Unit = {
        val randomPause = (baseTime + Random.nextDouble() * (randomTime2 - randomTime1) + randomTime1).seconds
        Thread.sleep(randomPause.toMillis)
    }

//    def randomSleep(baseTime: Double, randomTime1: Double, randomTime2: Double): Unit = {
//        val randomPause = (baseTime + Random.nextInt(randomTime2 - randomTime1 + 1) + randomTime1).seconds
//        Thread.sleep(randomPause.toMillis)
//    }

//    def maximizeWindow(windowID: String): Unit = {
//        // Check if the window is minimized
//        val stateCommand = Seq("xdotool", "getwindowgeometry", windowID)
//        val stateOutput = stateCommand.!!.trim
//        val isMinimized = stateOutput.contains("minimized")
//
//        if (isMinimized) {
//            // Restore the window
//            Seq("xdotool", "windowmap", windowID).!!
//            Thread.sleep(500)
//        }
//
//        // Maximize the window
//        val screenSize = Toolkit.getDefaultToolkit.getScreenSize
//        val maxCommand = Seq("xdotool", "windowsize", windowID, screenSize.getWidth.toInt.toString, screenSize.getHeight.toInt.toString)
//        val maxProcess = new ProcessBuilder(maxCommand.toList.asJava).start()
//        Await.result(Future(maxProcess.waitFor()), 10.seconds)
//    }

//    def maximizeWindow(windowID: String): Unit = {
//        // Check if the window is minimized
//        val stateCommand = Seq("xdotool", "getwindowgeometry", windowID)
//        val stateOutput = stateCommand.!!.trim
//        val isMinimized = stateOutput.contains("minimized")
//
//        if (isMinimized) {
//            // Bring the window up
//            val bringCommand = Seq("xdotool", "windowactivate", windowID)
//            bringCommand.!!
//
//            // Wait for the window to become active
//            Thread.sleep(500)
//        }
//
//        // Maximize the window
//        val screenSize = Toolkit.getDefaultToolkit.getScreenSize
//        val maxCommand = Seq("xdotool", "windowsize", windowID, screenSize.getWidth.toInt.toString, screenSize.getHeight.toInt.toString)
//        val maxProcess = new ProcessBuilder(maxCommand.toList.asJava).start()
//        Await.result(Future(maxProcess.waitFor()), 10.seconds)
//    }


    def savePlayerClasses(players: List[player.Player]): Unit = {
        val path = "classes/playerClasses/"
        players.foreach { player =>
            val filename = s"${path}${player.characterName}.ser"
            val outputStream = new ObjectOutputStream(new FileOutputStream(filename))
            outputStream.writeObject(player)
            outputStream.close()
        }
    }


    def testMove(windowName: String): Unit = {
        val windowSubstring = windowName
        // Open the window with the specified substring in the background
        val processBuilder = new ProcessBuilder(Seq("xdotool", "search", "--name", windowSubstring).toList.asJava)
        val windowID = Source.fromInputStream(processBuilder.start().getInputStream()).mkString.trim()

        println(s"Moving left, down, and right with a 0.5 second delay")

        // Activate the window with the specified windowID
        val activateCommand = Seq("xdotool", "windowactivate", windowID)
        val activateProcess = new ProcessBuilder(activateCommand.toList.asJava).start()
        Await.result(Future(activateProcess.waitFor()), 10.seconds)

        // Simulate key presses for moving left, down, and right with a 0.5 second delay between each key press after the key up event
        val robot = new Robot()

        robot.keyPress(KeyEvent.VK_UP)
        robot.keyRelease(KeyEvent.VK_UP)
        Thread.sleep(500)
        robot.keyPress(KeyEvent.VK_LEFT)
        robot.keyRelease(KeyEvent.VK_LEFT)
        Thread.sleep(500)
        robot.keyPress(KeyEvent.VK_DOWN)
        robot.keyRelease(KeyEvent.VK_DOWN)
        Thread.sleep(500)
        robot.keyPress(KeyEvent.VK_RIGHT)
        robot.keyRelease(KeyEvent.VK_RIGHT)
    }

//    def saveStateToFile(fileName: String, filePath: String): Unit = {
//        val file = new File(filePath + fileName + ".dat")
//        val outputStream = new ObjectOutputStream(new FileOutputStream(file))
//        outputStream.writeObject(this)
//        outputStream.close()
//    }

//    def saveStateToFile(fileName: String, filePath: String): Unit = {
//        val file = new File(filePath + fileName + ".json")
//        val json = this.asJson.noSpaces
//        val outputStream = new java.io.BufferedWriter(
//            new java.io.OutputStreamWriter(
//                new java.io.FileOutputStream(file),
//                StandardCharsets.UTF_8))
//        outputStream.write(json)
//        outputStream.close()
//    }
}

class InputHandler(robotBotThread: Robot) extends Thread {
    override def run(): Unit = {
        while (true) {
            // Wait for a short time before repeating the loop
            Thread.sleep(100)
        }
    }
}


