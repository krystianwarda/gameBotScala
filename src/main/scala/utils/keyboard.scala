package utils

import com.sun.jna.Library
import player.Player
import utils.core.randomNumber

import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.{Robot, event => e}
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
//import org.freedesktop.xtest._
import com.sun.jna.platform.unix.X11._
import com.sun.jna.Native
import com.sun.jna.platform.unix.X11._
import com.sun.jna.platform.unix.X11.Display
import com.sun.jna.{Native, Platform}



object keyboard {

//  private val robot = new Robot()

  def pressCtrlDirection(script: String): Unit = script match {
    case "left" | "right" | "top" | "bottom" =>
      val robot = new Robot()
      robot.keyPress(e.KeyEvent.VK_CONTROL)
      Thread.sleep(200)
      robot.keyPress(script match {
        case "left"   => e.KeyEvent.VK_LEFT
        case "right"  => e.KeyEvent.VK_RIGHT
        case "top"    => e.KeyEvent.VK_UP
        case "bottom" => e.KeyEvent.VK_DOWN
      })
      Thread.sleep(randomNumber(1,2,5)*100)
      robot.keyRelease(script match {
        case "left"   => e.KeyEvent.VK_LEFT
        case "right"  => e.KeyEvent.VK_RIGHT
        case "top"    => e.KeyEvent.VK_UP
        case "bottom" => e.KeyEvent.VK_DOWN
      })
      Thread.sleep(200)
      robot.keyRelease(e.KeyEvent.VK_CONTROL)
    case _ => throw new IllegalArgumentException(s"Invalid script: $script")
  }

  def sendText(robotInstance: Robot, text: String): Unit = {
    text.foreach { c =>
      val keyCode = KeyEvent.getExtendedKeyCodeForChar(c.toInt)
      robotInstance.keyPress(keyCode)
      robotInstance.keyRelease(keyCode)
    }
    robotInstance.keyPress(KeyEvent.VK_ENTER)
    robotInstance.keyRelease(KeyEvent.VK_ENTER)
  }

  def sendTextFast(robotInstance: Robot, text: String): Unit = {
    val clipboard = Toolkit.getDefaultToolkit.getSystemClipboard
    val stringSelection = new StringSelection(text)
    clipboard.setContents(stringSelection, null)
    robotInstance.keyPress(KeyEvent.VK_CONTROL)
    robotInstance.keyPress(KeyEvent.VK_V)
    robotInstance.keyRelease(KeyEvent.VK_V)
    robotInstance.keyRelease(KeyEvent.VK_CONTROL)
    robotInstance.keyPress(KeyEvent.VK_ENTER)
    robotInstance.keyRelease(KeyEvent.VK_ENTER)
  }

  def pressFKey(robotInstance: Robot, n: Int): Unit = {
    println(s"I want to press F${n}")
    if (n >= 1 && n <= 12) { // check if n is a valid F-key number
      robotInstance.keyPress(KeyEvent.VK_F1 + (n - 1)) // simulate key press for F1 to F12
      Thread.sleep(300)
      robotInstance.keyRelease(KeyEvent.VK_F1 + (n - 1)) // simulate key release for F1 to F1
    } else {
      println("Invalid input. Please enter a number between 1 and 12.") // handle invalid input
    }
  }

}
