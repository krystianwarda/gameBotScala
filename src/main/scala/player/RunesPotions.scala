package player
import player.Player
import utils.image.{getLocationFromImageMidMatchTemp, loadImage}
import utils.mouse.dragUseOnChar

import java.awt.Robot
object RunesPotions {
  def runePotionOnChar(characterClass: Player, runeName: String): Unit = {
      if (runeName == "uh") {
        var uhImage = loadImage("images/runesPotions/uh.png")
        var uhLoc = getLocationFromImageMidMatchTemp(uhImage, characterClass.getCharWindow)
        dragUseOnChar(characterClass.getRobot(), uhLoc, characterClass.getCenterLoc)
        characterClass.setLastSpellTimestamp(System.currentTimeMillis)
      }
    if (runeName == "ih") {
      var ihImage = loadImage("images/runesPotions/ih.png")
      var ihLoc = getLocationFromImageMidMatchTemp(ihImage, characterClass.getCharWindow)
      dragUseOnChar(characterClass.getRobot(), ihLoc, characterClass.getCenterLoc)
      characterClass.setLastSpellTimestamp(System.currentTimeMillis)
    }
    if (runeName == "hp") {
      var hpImage = loadImage("images/runesPotions/hp.png")
      var hpLoc = getLocationFromImageMidMatchTemp(hpImage, characterClass.getCharWindow)
      dragUseOnChar(characterClass.getRobot(), hpLoc, characterClass.getCenterLoc)
      characterClass.setLastSpellTimestamp(System.currentTimeMillis)
    }
    if (runeName == "mp") {
      var mpImage = loadImage("images/runesPotions/mp.png")
      var mpLoc = getLocationFromImageMidMatchTemp(mpImage, characterClass.getCharWindow)
      dragUseOnChar(characterClass.getRobot(), mpLoc, characterClass.getCenterLoc)
      characterClass.setLastSpellTimestamp(System.currentTimeMillis)
    }
  }

  // runeOnEnemy

}
