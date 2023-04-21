package player
//import credentials._

object Player {
  var charName: String = "Datwardaguy"
  var charProffesion: String = "Druid"
  var charExperience: Int = 9999
  var charLevel: Int = 9999
  var healthPoints: Int = 9999
  var manaPoints: Int = 9999
  var soulPoints: Int = 9999
  var capacityValue = 9999
  var magicLevel: Int = 9999
  var helmetLocation: Option[(Int, Int)] = None
  var armorLocation: Option[(Int, Int)] = None

  def setExperienceValue(value: Int): Unit = {
    charExperience = value
  }

  def setLevelValue(value: Int): Unit = {
    charLevel = value
  }

  def setHealthPoints(value: Int): Unit = {
    healthPoints = value
  }

  def setMana(value: Int): Unit = {
    manaPoints = value
  }

  def setSoulPoints(value: Int): Unit = {
    soulPoints = value
  }

  def setCapacityValue(value: Int): Unit = {
    capacityValue = value
  }

  def setMagicLevel(value: Int): Unit = {
    magicLevel = value
  }




  def updateState(skill: String, value: Int): Unit = {
    skill match {
      case "charExperience" => setExperienceValue(value)
      case "charLevel" => setLevelValue(value)
      case "healthPoints" => setHealthPoints(value)
      case "manaPoints" => setMana(value)
      case "soulPoints" => setSoulPoints(value)
      case "capacityValue" => setCapacityValue(value)
      case "magicLevel" => setMagicLevel(value)
      case _ => println(s"Invalid skill: $skill")
    }
  }

}
