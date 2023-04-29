package player

import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import utils.image.loadImage
import player.Player
import utils.keyboard.sendText
import utils.core.randomSleep
import java.io.File

object Spells {

  def castSpell(spell: String): Unit = {
    randomSleep(0.2,0.3,0.5)
    sendText(spell)
  }

  def castSpellSlow(spell: String): Unit = {
    randomSleep(1, 0.5, 1)
    sendText(spell)
  }

  def castSpellFast(spell: String): Unit = {
    randomSleep(0.2, 0.3, 0.5)
    sendText(spell)
  }

  def castSpellMultiple(spell: String, castTimes: Int): Unit = {
    for (i <- 1 to castTimes) {
      castSpell(spell)
      randomSleep(2,1,3)
    }

  }
}




