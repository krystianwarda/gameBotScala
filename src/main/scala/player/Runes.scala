package player
import player.Player
import utils.image.{getLocationFromImageMidMatchTemp, loadImage}
import utils.mouse.dragUseOnChar
object Runes {
  def runeOnChar(characterClass: Player, runeName: String): Unit = {
      if (runeName == "uh") {
        var uhImage = loadImage("images/runes/uh.png")
        var uhLoc = getLocationFromImageMidMatchTemp(uhImage, characterClass.getCharWindow)
        dragUseOnChar(uhLoc, characterClass.getCenterLoc)
      }
    if (runeName == "ih") {
      var ihImage = loadImage("images/runes/ih.png")
      var ihLoc = getLocationFromImageMidMatchTemp(ihImage, characterClass.getCharWindow)
      dragUseOnChar(ihLoc, characterClass.getCenterLoc)
    }
  }

  // runeOnEnemy

}
