//package player

import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import utils.image.loadImage
import player.Player
import utils.keyboard.sendText
import utils.core.randomSleep
import java.io.File

object Spells {
  val UH: String = "adura vita"
  val IH: String = "adura gran"
  val HMM: String = "adori gran"

  def castSpell(spell: String): Unit = {
    sendText(spell)
    randomSleep(3,1,3)
  }

  def castSpellMultiple(spell: String, castTimes: Int): Unit = {
    for (i <- 1 to castTimes) {
      castSpell(spell)
      randomSleep(2,1,3)
    }

  }
}




