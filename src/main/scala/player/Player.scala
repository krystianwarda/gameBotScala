package player

import utils.core.{getCurrentTimestamp, randomNumber}
import utils.image.{foodDetection, getLocationFromImageMid}
import utils.mouse.{mouseMoveSmooth, rightClick}
//import credentials._
import org.opencv.core.{Mat}
import java.time.Instant
import java.time.Duration

object Player {
  var charName: String = "Datwardaguy"
  var charProffesion: String = "Druid"
  var charExperience: Int = 9999
  var charLevel: Int = 9999
  var healthPoints: Int = 9999
  var manaPoints: Int = 9
  var soulPoints: Int = 9999
  var capacityValue = 9999
  var magicLevel: Int = 9999
  var lastMealTimestamp: Long = System.currentTimeMillis / 1000
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

  def foodStatus(mainImage: Mat): Unit = {
    var currentTimestamp = getCurrentTimestamp
    val previousMealTimestamp = Player.lastMealTimestamp
    // Calculate the difference between the timestamps

//    val difference = Duration.between(Instant.ofEpochSecond(previousMealTimestamp), Instant.ofEpochSecond(currentTimestamp)).getSeconds
    val difference = ((currentTimestamp - previousMealTimestamp)).toInt
    if (difference > randomNumber(30, 5, 20)) {
      eatFood(mainImage)
    }
  }

  def eatFood(mainImage: Mat): Unit = {
    var foodLoc = foodDetection(mainImage)
    mouseMoveSmooth(foodLoc)
    Thread.sleep(300)
    rightClick(foodLoc)
    lastMealTimestamp = System.currentTimeMillis / 1000
  }
}
