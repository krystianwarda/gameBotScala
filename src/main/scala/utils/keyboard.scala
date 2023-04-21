package utils

import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent

object keyboard {

  val robot = new Robot()

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
