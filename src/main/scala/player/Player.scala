package player
//import org.opencv.core.Mats
import cavebot.{CaveBot, Monsters}
import cavebot.core.{detectMonsters, followWaypoints, getBattlePositions, monsterMarkedStatus}
import com.sun.jna.Union
import org.opencv.core.{Mat, Point, Rect, Size}
import org.opencv.imgcodecs.Imgcodecs
import player.RunesPotions.runePotionOnChar
import player.skillsWindow.{createRectangle, numberDetection}
import radar.core.cutRadarImage
import utils.InputHandler
import utils.core.randomDirection
import utils.image.{areImagesIdentical, extractRectangle, getLocationFromImageMidLeft, loadImage, makeScreenshotMat, mouseOverRectangle, saveMatAsPng}
import utils.keyboard.{pressCtrlDirection, pressFKey}
import utils.mouse.leftClick

import java.awt.Robot
import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.util.control.Breaks.break
//import player.Player2.{capacityValue, charExperience, charLevel, healthPoints, lastMealTimestamp, magicLevel, manaPoints, soulPoints}
import utils.core.{getCurrentTimestamp, randomNumber}
import utils.image.foodDetection
import utils.mouse.{mouseMoveSmooth, rightClick}
import player.Spells
import java.time.{Instant, Duration}

class Player(val windowID: String,
             val characterName: String,
             var charWindow: Mat,
             var centerLoc: Option[(Int, Int)],
             var radarImage: Mat,
             var radarCenterLoc: Option[(Int, Int)],
             var robotInstance: Robot) {


  var autoHealEnabled: Boolean = false
  var runeMakerEnabled: Boolean = false
  var caveBotEnabled: Boolean = false
  var radarImagePrev: Mat = radarImage
  var checkInterval: Int = 0
  var lastCheckTimestamp: Long = System.currentTimeMillis
  var characterStaticStatus: Boolean = true
  var staticStatusCounter: Int = 0
  var lastWaypointTimestamp: Long = System.currentTimeMillis
  var caveBotStatus: String = ""
  var assignedCaveBotClass: CaveBot = _
  var battlePosition1: Option[(Int, Int)] = _
  var battlePosition2: Option[(Int, Int)] = _
  var battlePosition3: Option[(Int, Int)] = _
  var battlePosition4: Option[(Int, Int)] = _
  var battlePosition5: Option[(Int, Int)] = _


  def updateWaypointStatus(value: Long): Unit = {
    lastWaypointTimestamp = value
  }



  // character values
  var charExperience: Int = 0
  var charLevel: Int = 0
  var healthPoints: Int = 0
  var manaPoints: Int = 0
  var soulPoints: Int = 0
  var capacityValue = 0
  var magicLevel: Int = 0
  var lastMealTimestamp: Long = System.currentTimeMillis / 1000
  var lastSpellTimestamp: Long = System.currentTimeMillis
  var lastCharacterRotation: Long = System.currentTimeMillis() / 1000

  // bot settings values
  var threadActivation: Boolean = true
  private var inputHandler: Option[InputHandler] = None
  var autoHealPanelClass: String = ""
  var botLightHealSpell: String = ""
  var botLightHealHealth: Int = 0
  var botLightHealMana: Int = 0
  var botStrongHealSpell: String = ""
  var botStrongHealHealth: Int = 0
  var botStrongHealMana: Int = 0
  var botIhHealHealth: Int = 0
  var botIhHealMana: Int = 0
  var botUhHealHealth: Int = 0
  var botUhHealMana: Int = 0
  var botHPotionHealHealth: Int = 0
  var botHPotionHealMana: Int = 0
  var botMPotionHealManaMin: Int = 0

  def updateAutoHeal(lightSpell: String, lightHealth: Int, lightMana: Int,
                     strongSpell: String, strongHealth: Int, strongMana: Int,
                     ihHealth: Int, ihMana: Int,
                     uhHealth: Int, uhMana: Int,
                     hPotionHealth: Int, hPotionMana: Int,
                     mPotionManaMin: Int): Unit = {
    botLightHealSpell = lightSpell
    botLightHealHealth = lightHealth
    botLightHealMana = lightMana
    botStrongHealSpell = strongSpell
    botStrongHealHealth = strongHealth
    botStrongHealMana = strongMana
    botIhHealHealth = ihHealth
    botIhHealMana = ihMana
    botUhHealHealth = uhHealth
    botUhHealMana = uhMana
    botHPotionHealHealth = hPotionHealth
    botHPotionHealMana = hPotionMana
    botMPotionHealManaMin = mPotionManaMin
  }

  // window values
  var helmetLocation: Option[(Int, Int)] = None
  var armorLocation: Option[(Int, Int)] = None

  def getCharLevel(): Int = {
    return charLevel
  }

  def updateLastCheck(): Unit = {
    lastCheckTimestamp = System.currentTimeMillis()
  }
  def isCheckNeeded(): Boolean = {
    val currentTime = System.currentTimeMillis()
    val timeDiff = currentTime - this.lastCheckTimestamp
    val checkIntervalMillis = this.checkInterval * 1000

    timeDiff >= checkIntervalMillis
  }
  def setCaveBot(caveBot: CaveBot): Unit = {
    this.assignedCaveBotClass = caveBot
  }

  def caveBotFunction(monsterClass: Monsters): Unit = {
    if (caveBotEnabled) {
      this.checkBattle(monsterClass)
      if (this.characterStaticStatus && this.checkWaypointStatus()) {
        followWaypoints(this, this.assignedCaveBotClass)
      }
    }
  }

  def setThreaad(value: Boolean): Unit = {
    threadActivation = value
  }
  def setBotLightHealSpell (text: String): Unit = {
    botLightHealSpell = text
  }
  def setBotLightHealHealth(value: Int): Unit = {
    botLightHealHealth = value
  }
  def setBotLightHealMana(value: Int): Unit = {
    botLightHealMana = value
  }
  def getBotLightHealSpell(value: Int): Unit = {
    return botLightHealSpell
  }
  def getBotLightHealth(value: Int): Unit = {
    return botLightHealHealth
  }
  def getBotLightHealMana(value: Int): Unit = {
    return botLightHealMana
  }

  def getRobot(): Robot = {
    return this.robotInstance
  }
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

  def setLastSpellTimestamp(value: Long): Unit = {
    lastSpellTimestamp = value
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

  def setRadarImagePrev(image: Mat): Unit = {
    radarImagePrev = image
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
    setRadarImagePrev(this.radarImage)
    setRadarImage(cutRadarImage(this.getCharWindow()))
  }
  def checkWaypointStatus(): Boolean = {
    val currentTime = System.currentTimeMillis()
    val differenceInSeconds = (currentTime - this.lastWaypointTimestamp) / 1000
    differenceInSeconds > 3
  }

  def checkStaticStatus(): Boolean = {
    val currentStatus = areImagesIdentical(this.radarImagePrev, this.radarImage)
    if (currentStatus) {
      staticStatusCounter += 1
    } else {
      staticStatusCounter = 0
    }
    characterStaticStatus = currentStatus
    staticStatusCounter >= 3
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


  def startInputHandling(): Unit = {
    inputHandler = Some(new InputHandler(this.getRobot()))
    inputHandler.foreach(_.start())
  }

  def stopInputHandling(): Unit = {
    inputHandler.foreach(_.interrupt())
    inputHandler = None
  }



  def autoHealFunction(): Unit = {
    if (autoHealEnabled) {
      println("Gate1")
      if (checkExhaust(this.lastSpellTimestamp)) {
        if (this.botUhHealHealth != 0
          && this.botUhHealHealth > this.getHealthPoints
          && this.botUhHealMana < this.getManaPoints) {
          println("Gate3")
          RunesPotions.runePotionOnChar(this, "uh")
          this.setLastSpellTimestamp(System.currentTimeMillis())
        }

        if (this.botIhHealHealth != 0
          && this.botIhHealHealth > this.getHealthPoints
          && this.botIhHealMana < this.getManaPoints) {
          RunesPotions.runePotionOnChar(this, "ih")
          this.setLastSpellTimestamp(System.currentTimeMillis())
        }

        if (this.botHPotionHealHealth != 0
          && this.botHPotionHealHealth > this.getHealthPoints
          && this.botHPotionHealMana < this.getManaPoints) {
          RunesPotions.runePotionOnChar(this, "hp")
          this.setLastSpellTimestamp(System.currentTimeMillis())
        }

        if (this.botMPotionHealManaMin != 0
          && this.botMPotionHealManaMin > this.getManaPoints) {
          RunesPotions.runePotionOnChar(this, "mp")
          this.setLastSpellTimestamp(System.currentTimeMillis())
        }

        if (this.botStrongHealHealth != 0
          && this.botStrongHealHealth > this.getHealthPoints
          && this.botStrongHealMana < this.getManaPoints) {
          Spells.castSpellSlow(this.getRobot(), botStrongHealSpell)
          this.setLastSpellTimestamp(System.currentTimeMillis)
        }

        if (this.botLightHealHealth != 0
          && this.botLightHealHealth > this.getHealthPoints
          && this.botLightHealMana < this.getManaPoints) {
          println("I need to heal")
          pressFKey(this.getRobot(), 3)
          //        Spells.castSpellSlow(botLightHealSpell)
          this.setLastSpellTimestamp(System.currentTimeMillis())
        }
      }
    }
  }


  def findCoordinate(croppedImage: Mat, previousCoordinate: Option[(Int, Int, Int)] = None): Unit = {
    val stringList = List("charExperience", "charLevel", "healthPoints", "manaPoints", "soulPoints", "capacityValue", "magicLevel")
  }

  def checkBattle(monsterClass: Monsters): Unit = {
    var mainImage = this.charWindow
    var battleImage = loadImage("images/battle/battleWindow.png")
    var battleLoc = getLocationFromImageMidLeft(battleImage, mainImage)
    var monsterList = detectMonsters(getBattlePositions(this, battleLoc, 71, 22, 21, 160))

    if (!monsterMarkedStatus(monsterList)) {
      var foundMonster = false
      for (((monsterName, _), index) <- monsterList.zipWithIndex if !foundMonster) {
        if (monsterClass.getMonsterNames().contains(monsterName)) {
          foundMonster = true
          index match {
            case 0 =>
              this.battlePosition1 match {
                case Some((x, y)) =>
                  leftClick(this.robotInstance, Some((x, y)))
                case None =>
                  println("Battle position 1 not found.")
              }
            case 1 =>
              this.battlePosition2 match {
                case Some((x, y)) =>
                  mouseMoveSmooth(this.robotInstance, Some((x, y)))
                case None =>
                  println("Battle position 2 not found.")
              }
            // Add more cases for additional battle positions if needed
            case _ => println("No suitable battle position found.")
          }
        }
      }
    }
  }


  //  def checkBattle(monsterClass: Monsters): Unit = {
//    var mainImage = this.charWindow
//    var battleImage = loadImage("images/battle/battleWindow.png")
//    var battleLoc = getLocationFromImageMidLeft(battleImage, mainImage)
////    mouseMoveSmooth(this.robotInstance, tempLoc)
//    var monsterList = detectMonsters(getBattlePositions(this, battleLoc, 71, 22, 21, 160))
//    if (!monsterMarkedStatus(monsterList)) {
//      for ((monsterName, _) <- monsterList) {
//        if (monsterClass.getMonsterNames().contains(monsterName)) {
//          this.battlePosition1 match {
//            case Some((x, y)) =>
//              mouseMoveSmooth(this.robotInstance, Some((x, y)))
//            // Add any additional actions you want to perform after moving the mouse
//            case None =>
//              println("Battle position 1 not found.")
//          }
//          // Exit the loop if the monster is found and the mouseMoveSmooth function is called
//          break
//        }
//      }
//    }
//  }



//  def saveBattlePositions(matList: List[Mat]): Unit = {
//    for ((mat, index) <- matList.zipWithIndex) {
//      val filename = s"battle${index + 1}.png"
//      Imgcodecs.imwrite(filename, mat)
//    }
//  }




  def checkSkills(): Unit = {
    // check skills sections
//    println(this.characterName)
    var mainImage = this.charWindow
//    var mainImage = loadImage(s"window_${this.characterName}.png")
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
      eatFood(this)
    } else if (foodDetection(this.getCharWindow()) == null) {
      println("Food image not found!")
    }
  }

  def eatFood(characterClass: Player): Unit = {
    var foodLoc = foodDetection(characterClass.getCharWindow())
    mouseMoveSmooth(characterClass.getRobot(), foodLoc)
    Thread.sleep(300)
    rightClick(characterClass.getRobot(), foodLoc)
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
        mouseMoveSmooth(this.getRobot(), tempLoc)
      } catch {
        case e: Exception =>
          println("Not found: " + e.getMessage)
      }

      // call createRectangle to get the rectangle coordinates
      val rectangleCoords: Option[(Option[(Int, Int)], Option[(Int, Int)], Option[(Int, Int)], Option[(Int, Int)])] = createRectangle(tempLoc)
      mouseOverRectangle(this, rectangleCoords)
      var rectangleImage = extractRectangle(mainImage, rectangleCoords)
      saveMatAsPng(mainImage, str)
      this.updateState(str, numberDetection(rectangleImage))

    }
  }
  def checkExhaust(timestamp: Long): Boolean = {
    Math.abs(System.currentTimeMillis() - timestamp) > 2000
  }
  def updateGeneral(): Unit = {
    print("Screen update.\n")
    updateRadarImage()
    updateCharWindow()
    updateRadarImage()
    checkSkills()
//    autoHeal()
    Thread.sleep(300)
  }

  def updateScreen(): Unit = {
    updateCharWindow()
    updateRadarImage()
  }


  def saveClass(): Unit = {
    val filename = s"classes/playerClasses/${this.characterName}.ser"
    val outputStream = new ObjectOutputStream(new FileOutputStream(filename))

    // Create a new object with only the variables you want to export
    val exportObject = new MyClassExport(
      botLightHealSpell,
      botLightHealHealth,
      botLightHealMana,
      botStrongHealSpell,
      botStrongHealHealth,
      botStrongHealMana,
      botIhHealHealth,
      botIhHealMana,
      botUhHealHealth,
      botUhHealMana,
      botHPotionHealHealth,
      botHPotionHealMana,
      botMPotionHealManaMin
    )

    outputStream.writeObject(exportObject)
    outputStream.close()
  }

  def loadClass(): Unit = {
    val filename = s"classes/playerClasses/${this.characterName}.ser"
    val inputStream = new ObjectInputStream(new FileInputStream(filename))
    val importObject = inputStream.readObject().asInstanceOf[MyClassExport]
    inputStream.close()
    // Overwrite the class variables with the loaded values
    botLightHealSpell = importObject.botLightHealSpell
    botLightHealHealth = importObject.botLightHealHealth
    botLightHealMana = importObject.botLightHealMana
    botStrongHealSpell = importObject.botStrongHealSpell
    botStrongHealHealth = importObject.botStrongHealHealth
    botStrongHealMana = importObject.botStrongHealMana
    botIhHealHealth = importObject.botIhHealHealth
    botIhHealMana = importObject.botIhHealMana
    botUhHealHealth = importObject.botUhHealHealth
    botUhHealMana = importObject.botUhHealMana
    botHPotionHealHealth = importObject.botHPotionHealHealth
    botHPotionHealMana = importObject.botHPotionHealMana
    botMPotionHealManaMin = importObject.botMPotionHealManaMin
  }

}

// Define a new class to hold the variables you want to export
class MyClassExport(
                     var botLightHealSpell: String,
                     var botLightHealHealth: Int,
                     var botLightHealMana: Int,
                     var botStrongHealSpell: String,
                     var botStrongHealHealth: Int,
                     var botStrongHealMana: Int,
                     var botIhHealHealth: Int,
                     var botIhHealMana: Int,
                     var botUhHealHealth: Int,
                     var botUhHealMana: Int,
                     var botHPotionHealHealth: Int,
                     var botHPotionHealMana: Int,
                     var botMPotionHealManaMin: Int
                   ) extends Serializable
