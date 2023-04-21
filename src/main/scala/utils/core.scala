package utils

import java.awt.{Desktop, Robot, Toolkit}
import java.awt.event.KeyEvent
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import java.awt.KeyboardFocusManager
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source
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

object core {

    def getWindow(windowName: String): String = {
        // Open the window
        val processBuilder = new ProcessBuilder(Seq("xdotool", "search", "--name", windowName).toList.asJava)
        Source.fromInputStream(processBuilder.start().getInputStream()).mkString.trim()
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
}


