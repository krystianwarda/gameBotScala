package player
//import org.opencv.core.Mats
import com.sun.jna.Union
import org.opencv.core.Mat
import player.Runes.runeOnChar
import player.skillsWindow.{createRectangle, numberDetection}
import radar.core.{cutRadarImage}
import utils.core.randomDirection
import utils.image.{extractRectangle, getLocationFromImageMidLeft, loadImage, makeScreenshotMat, mouseOverRectangle, saveMatAsPng}
import utils.keyboard.pressCtrlDirection
//import player.Player2.{capacityValue, charExperience, charLevel, healthPoints, lastMealTimestamp, magicLevel, manaPoints, soulPoints}
import utils.core.{getCurrentTimestamp, randomNumber}
import utils.image.foodDetection
import utils.mouse.{mouseMoveSmooth, rightClick}
import player.Spells

class Player(val windowID: String,
             val characterName: String,
             var charWindow: Mat,
             var centerLoc: Option[(Int, Int)],
             var radarImage: Mat,
             var radarCenterLoc: Option[(Int, Int)]) {
  // bot settings values
  var botStrongHealSpell: String = ""
  var botStrongHealHealth: Int = 0
  var botStrongHealMana: Int = 0
  var botLightHealSpell: String = ""
  var botLightHealValue: Int = 0
  var botLightHealMana: Int = 0



  // window values
  var charExperience: Int = 0
  var charLevel: Int = 0
  var healthPoints: Int = 0
  var manaPoints: Int = 0
  var soulPoints: Int = 0
  var capacityValue = 0
  var magicLevel: Int = 0
  var lastMealTimestamp: Long = System.currentTimeMillis / 1000
  var lastCharacterRotation: Long = System.currentTimeMillis() / 1000
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

  def setLastMealTimestamp(value: Long): Unit = {
    lastMealTimestamp = value
  }
  def setCenterLoc(loc: Option[(Int, Int)]): Unit = {
    centerLoc = loc
  }
  def setLastCharacterRotation(value: Long): Unit = {
    lastCharacterRotation = value
  }
  def setRadarImage(image: Mat): Unit = {
    radarImage = image
  }

  def setRadarCenterLoc(loc: Option[(Int, Int)]): Unit = {
    radarCenterLoc = loc
  }
  def getlastMealTimestamp(): Long = {
    return this.lastMealTimestamp
  }

  def getLastCharacterRotation(): Long = {
    return this.lastCharacterRotation
  }

  def getManaPoints(): Int = {
    return this.manaPoints
  }

  def getHealthPoints(): Int = {
    return this.healthPoints
  }

  def getCharWindow(): Mat = {
    return charWindow
  }
  def updateCharWindow(): Unit = {
    charWindow = makeScreenshotMat(this.windowID, this.characterName)
  }

  def updateRadarImage(): Unit = {
    setRadarImage(cutRadarImage(this.getCharWindow()))
  }

  def getCenterLoc(): Option[(Int, Int)] = {
    return centerLoc
  }

  def getRadarImage(): Mat = {
    return radarImage
  }

  def getRadarCenterLoc(): Option[(Int, Int)] = {
    return radarCenterLoc
  }

  def autoheal(exura: Option[Int] = None,
               exura_gran: Option[Int] = None,
               exura_vita: Option[Int] = None,
               exura_sio: Option[Int] = None,
               IH: Option[Int] = None,
               UH: Option[Int] = None): Unit = {

    if (UH.exists(_ > this.getHealthPoints)) {
      Runes.runeOnChar(this, "uh")
    }
    if (IH.exists(_ > this.getHealthPoints)) {
      println("I must heal!")
      Runes.runeOnChar(this, "ih")
    }
    if (exura_sio.exists(_ > this.getHealthPoints)) {
      Spells.castSpellSlow(s"""exura sio \"${this.characterName}""")
    }
    if (exura_vita.exists(_ > this.getHealthPoints)) {
      Spells.castSpellSlow("exura vita")
    }
    if (exura_gran.exists(_ > this.getHealthPoints)) {
      Spells.castSpellSlow("exura gran")
    }
    if (exura.exists(_ > this.getHealthPoints)) {
        Spells.castSpellSlow("exura")
    }
  }

  def findCoordinate(croppedImage: Mat, previousCoordinate: Option[(Int, Int, Int)] = None): Unit = {
    val stringList = List("charExperience", "charLevel", "healthPoints", "manaPoints", "soulPoints", "capacityValue", "magicLevel")
  }
  def checkSkills(): Unit = {
    // check skills sections
//    println(this.characterName)
    var mainImage = loadImage(s"window_${this.characterName}.png")
    val stringList = List("charExperience", "charLevel", "healthPoints", "manaPoints", "soulPoints", "capacityValue", "magicLevel")

    for (str <- stringList) {
      var tempImage = loadImage(s"images/skillsWindow/sections/$str.png")
      var tempLoc = getLocationFromImageMidLeft(tempImage, mainImage)

      // call createRectangle to get the rectangle coordinates
      val rectangleCoords: Option[(Option[(Int, Int)], Option[(Int, Int)], Option[(Int, Int)], Option[(Int, Int)])] = createRectangle(tempLoc)
      var skillValue = numberDetection(extractRectangle(mainImage, rectangleCoords))

      // assign skillValue to specific skill using Player.updateState
      str match {
        case "charExperience" => this.updateState(str, skillValue)
        case "charLevel" => this.updateState(str, skillValue)
        case "healthPoints" => this.updateState(str, skillValue)
        case "manaPoints" => this.updateState(str, skillValue)
        case "soulPoints" => this.updateState(str, skillValue)
        case "capacityValue" => this.updateState(str, skillValue)
        case "magicLevel" => this.updateState(str, skillValue)
        case _ => println(s"Invalid skill: $str")
      }
    }
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

  def foodStatus(): Unit = {
    var currentTimestamp = getCurrentTimestamp
    var previousMealTimestamp = this.getlastMealTimestamp
    // Calculate the difference between the timestamps
    var difference = ((currentTimestamp - previousMealTimestamp)).toInt
    println(s"Food, ${this.characterName}, diff: ${difference}")
    if (difference > randomNumber(30, 5, 20)) {
      eatFood(this.getCharWindow())
    } else if (foodDetection(this.getCharWindow()) == null) {
      println("Food image not found!")
    }
  }

  def eatFood(mainImage: Mat): Unit = {
    var foodLoc = foodDetection(mainImage)
    mouseMoveSmooth(foodLoc)
    Thread.sleep(300)
    rightClick(foodLoc)
    this.setLastMealTimestamp(System.currentTimeMillis / 1000)
  }

  def rotationStatus(): Unit = {
    var currentTimestamp = getCurrentTimestamp
    var previousCharacterRotation = this.getLastCharacterRotation
    var difference = ((currentTimestamp - previousCharacterRotation)).toInt
    println(s"Rotation, ${this.characterName}, diff: ${difference}")
    if (difference > randomNumber(60, 50, 150)) {
      for (i <- 2 to 3 + randomNumber(0, 1, 3)) {
        pressCtrlDirection(randomDirection())
      }
      pressCtrlDirection("top")
    }
  }

  def checkMana(): Unit = {
    println(this.characterName)
    // check skills sections
    var mainImage = loadImage(s"window_${this.characterName}.png")
    val stringList = List("manaPoints")

    for (str <- stringList) {
      var tempImage = loadImage(s"images/skillsWindow/sections/$str.png")
      println(str)
      var tempLoc = getLocationFromImageMidLeft(tempImage, mainImage)
      try {
        mouseMoveSmooth(tempLoc)
      } catch {
        case e: Exception =>
          println("Not found: " + e.getMessage)
      }

      // call createRectangle to get the rectangle coordinates
      val rectangleCoords: Option[(Option[(Int, Int)], Option[(Int, Int)], Option[(Int, Int)], Option[(Int, Int)])] = createRectangle(tempLoc)
      mouseOverRectangle(rectangleCoords)
      var rectangleImage = extractRectangle(mainImage, rectangleCoords)
      saveMatAsPng(mainImage, str)
      this.updateState(str, numberDetection(rectangleImage))

    }
  }

  def updateGeneral(): Unit = {
    print("Screen update.")
    updateCharWindow()
    updateRadarImage()
    checkSkills()
  }

}
