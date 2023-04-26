package utils

import utils.core.randomNumber

import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.{Robot, event => e}
object keyboard {

  val robot = new Robot()

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

  def sendText(text: String): Unit = {
    text.foreach { c =>
      val keyCode = KeyEvent.getExtendedKeyCodeForChar(c.toInt)
      robot.keyPress(keyCode)
      robot.keyRelease(keyCode)
    }
    robot.keyPress(KeyEvent.VK_ENTER)
    robot.keyRelease(KeyEvent.VK_ENTER)
  }


  
}
