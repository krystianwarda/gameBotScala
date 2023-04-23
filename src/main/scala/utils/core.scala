package utils

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
import java.io.File
import java.awt.{Dimension, Rectangle, Robot, Toolkit}
import java.io.File
import javax.imageio.ImageIO
import scala.collection.JavaConverters._
import org.opencv.imgproc.Imgproc
import org.opencv.imgcodecs.Imgcodecs
import scala.util.Random
import scala.concurrent.duration._
import scala.sys.process.stringSeqToProcess
import utils.gameScreen._
import utils.gameScreen

object core {

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
        baseNumber + Random.between(randomNumber1, randomNumber2 + 1)
    }

    def randomSleep(baseTime: Int, randomTime1: Int, randomTime2: Int): Unit = {
        val randomPause = (baseTime + Random.nextInt(randomTime2 - randomTime1 + 1) + randomTime1).seconds
        Thread.sleep(randomPause.toMillis)
    }

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
}


