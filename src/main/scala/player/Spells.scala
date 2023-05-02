package player

import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import utils.image.loadImage
import player.Player
import utils.keyboard.{sendText, sendTextFast}
import utils.core.randomSleep

import java.awt.Robot
import java.io.File

object Spells {

  def castSpell(robotInstance: Robot, spell: String): Unit = {
    sendTextFast(robotInstance, spell)
  }

  def castSpellSlow(robotInstance: Robot, spell: String): Unit = {
    sendTextFast(robotInstance, spell)
  }

  def castSpellFast(robotInstance: Robot, spell: String): Unit = {
    sendTextFast(robotInstance, spell)
  }

  def castSpellMultiple(robotInstance: Robot, spell: String, castTimes: Int): Unit = {
    for (i <- 1 to castTimes) {
      castSpell(robotInstance, spell)
      randomSleep(2,1,3)
    }

  }
}




