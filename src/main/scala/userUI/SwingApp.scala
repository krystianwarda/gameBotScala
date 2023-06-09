package userUI

//import monix.reactive.subjects.Var
//import monix.reactive.subjects.Var
import cavebot.Monsters
import cavebot.core.recordMove
import monix.reactive.subjects.Var
import org.opencv.core.{CvType, Mat, MatOfByte}
import org.opencv.imgcodecs.Imgcodecs
import player.Player
import scalafx.beans.property.ObjectProperty

import scala.swing.TabbedPane._
import scala.swing._
import scala.collection.mutable.ArrayBuffer
import java.awt.{Font, GraphicsEnvironment, GridBagLayout, Image}
import javax.swing.{BoxLayout, DefaultDesktopManager, DefaultListModel, JList, JScrollPane, UIManager}
import userUI.UserSettings
import utils.core.runBot
import utils.image.{arrayToMat, matToArray, matToBufferedImage, matToImage}

import java.awt.event.ActionListener
import java.awt.image.DataBufferByte
import javax.swing.SpringLayout.Constraints
import javax.swing.event.ListSelectionListener
import scala.swing.GridBagPanel.Anchor
import scala.swing.event.ListSelectionEvent
//import scala.swing.event.Key.Escape
//import monix.reactive.{Observable, Observer}
//import monix.execution.Scheduler.Implicits.global
//import monix.execution.rstreams.Subscription
import scala.swing.event.SelectionChanged

//import java.awt.event.ActionEvent
import java.beans.PropertyChangeEvent
//import monix.reactive._
//import monix.execution.Scheduler.Implicits.global
//import monix.execution.Cancelable
//import monix.reactive.observables.ConnectableObservable

//import monix.execution.Cancelable
//import monix.reactive.Observable
//import monix.reactive.observers.Subscriber
import java.awt.{Dimension, Font}
//import java.awt.event.ActionEvent
import java.beans.PropertyChangeEvent
import javax.swing.{JComboBox, JLabel}
//import monix.execution.Cancelable
//import monix.reactive.Observable
//import monix.reactive.observers.Subscriber
import scala.swing.{BoxPanel, Dimension, Label, MainFrame, Orientation, SimpleSwingApplication, TabbedPane}
import scala.swing.Swing._
import scala.swing.ComboBox
import scala.swing.event.SelectionChanged
import scala.swing.event.ButtonClicked
import scala.swing.{BoxPanel, ComboBox, Label, Orientation}
import scala.swing.event.SelectionChanged
import scala.swing.ListView.Renderer
import scalafx.Includes._
import scala.swing.GridBagPanel.Fill

//import scalafx.beans.property.{ObjectProperty, Var}
//import monix.execution.Scheduler.Implicits.global
import java.awt.{GridBagConstraints, GridBagLayout, Insets}
import javax.swing.{JLabel, JPanel, JTextField}
import javax.swing.{JButton, JLabel, JPanel, JTextField}
import scala.swing.GridBagPanel.Anchor
//scala.swing.GridBagPanel.Fill
import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global
import java.awt.event.KeyEvent.{VK_ESCAPE, getKeyText}
import scala.swing.Reactions.Reaction
import scala.swing.event.{ButtonClicked, KeyPressed}
import scala.swing.event.Key
import Key.Escape
import javax.swing._
import scala.swing._
import scala.swing.event._
//import java.awt._
import java.awt.event._
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import cavebot.CaveBot
import java.awt.Image


case class SwingApp(playerClassList: List[player.Player], inputCaveBotClassList: List[cavebot.CaveBot], MonstersClass: Monsters) extends MainFrame {
  var caveBotClassList: List[cavebot.CaveBot] = inputCaveBotClassList

  title = "TibiaYBB - Younger Brother Bot"
  preferredSize = new Dimension(600, 300)
  var runningBot = false




  def updateExample(): Unit = {
    val selectedName = exampleDropdown.selection.item
    val selectedExample = exampleMap(selectedName)
    exampleLabel.text = s"Name: ${selectedExample.characterName}, Level: ${selectedExample.charLevel}"
    lightHealSpellField.text = selectedExample.botLightHealSpell.toString
    lightHealHealthField.text = selectedExample.botLightHealHealth.toString
    lightHealManaField.text = selectedExample.botLightHealMana.toString
    strongHealSpellField.text = selectedExample.botStrongHealSpell.toString
    strongHealHealthField.text = selectedExample.botStrongHealHealth.toString
    strongHealManaField.text = selectedExample.botStrongHealMana.toString
    ihHealHealthField.text = selectedExample.botIhHealHealth.toString
    ihHealManaField.text = selectedExample.botIhHealMana.toString
    uhHealHealthField.text = selectedExample.botUhHealHealth.toString
    uhHealManaField.text = selectedExample.botUhHealMana.toString
    hPotionHealHealthField.text = selectedExample.botHPotionHealHealth.toString
    hPotionHealManaField.text = selectedExample.botHPotionHealMana.toString
    mPotionHealManaMinField.text = selectedExample.botMPotionHealManaMin.toString
  }

  def saveExample(): Unit = {
    val selectedName = exampleDropdown.selection.item
    val selectedExample = exampleMap(selectedName)
    val lightHealSpellVar = lightHealSpellField.text
    val lightHealHealthVar = lightHealHealthField.text.toInt
    val lightHealManaVar = lightHealManaField.text.toInt
    val strongHealSpellVar = strongHealSpellField.text
    val strongHealHealthVar = strongHealHealthField.text.toInt
    val strongHealManaVar = strongHealManaField.text.toInt
    val ihHealHealthVar = ihHealManaField.text.toInt
    val ihHealManaVar = ihHealManaField.text.toInt
    val uhHealHealthVar = uhHealHealthField.text.toInt
    val uhHealManaVar = uhHealManaField.text.toInt
    val hPotionHealHealthVar = hPotionHealHealthField.text.toInt
    val hPotionHealManaVar = hPotionHealManaField.text.toInt
    val mPotionHealManaMinVar = mPotionHealManaMinField.text.toInt

    selectedExample.updateAutoHeal(lightHealSpellVar, lightHealHealthVar, lightHealManaVar,
      strongHealSpellVar, strongHealHealthVar, strongHealManaVar,
      ihHealHealthVar, ihHealManaVar,
      uhHealHealthVar, uhHealManaVar,
      hPotionHealHealthVar, hPotionHealManaVar,
      mPotionHealManaMinVar)
  }

  val exampleNames = playerClassList.map(_.characterName)
  val exampleMap = playerClassList.map(e => e.characterName -> e).toMap

  val exampleDropdown = new ComboBox(exampleNames)

  val exampleLabel = new Label()
  val lightHealSpellField = new TextField()
  val lightHealHealthField = new TextField()
  val lightHealManaField = new TextField()
  val strongHealSpellField = new TextField()
  val strongHealHealthField = new TextField()
  val strongHealManaField = new TextField()
  val ihHealHealthField = new TextField()
  val ihHealManaField = new TextField()
  val uhHealHealthField = new TextField()
  val uhHealManaField = new TextField()
  val hPotionHealHealthField = new TextField()
  val hPotionHealManaField = new TextField()
  val mPotionHealManaMinField = new TextField()


  val updateButton = new Button("Update") {
    reactions += {
      case ButtonClicked(_) =>
        println("Update button clicked")
        saveExample()
        updateExample()
    }
  }

  contents = new TabbedPane {


    pages += new TabbedPane.Page("Main", new BoxPanel(Orientation.Vertical) {
      contents += exampleDropdown

      val autoHealCheckbox = new CheckBox("Auto Heal")
      val runeMakerCheckbox = new CheckBox("Rune Maker")
      val caveBotCheckbox = new CheckBox("Cave Bot")
      // Add the Label and TextField for Check interval
      val checkIntervalLabel = new Label("Check interval")
      val checkIntervalTextField = new TextField()

      contents += checkIntervalLabel
      contents += checkIntervalTextField
      contents += exampleDropdown
      contents += autoHealCheckbox
      contents += runeMakerCheckbox
      contents += caveBotCheckbox


      listenTo(autoHealCheckbox, runeMakerCheckbox, caveBotCheckbox)
      reactions += {
        case ButtonClicked(`autoHealCheckbox`) =>
          val selectedPlayer = playerClassList(exampleDropdown.selection.index)
          selectedPlayer.autoHealEnabled = autoHealCheckbox.selected
        case ButtonClicked(`runeMakerCheckbox`) =>
          val selectedPlayer = playerClassList(exampleDropdown.selection.index)
          selectedPlayer.runeMakerEnabled = runeMakerCheckbox.selected
        case ButtonClicked(`caveBotCheckbox`) =>
          val selectedPlayer = playerClassList(exampleDropdown.selection.index)
          selectedPlayer.caveBotEnabled = caveBotCheckbox.selected
      }

      def updateCheckboxes(selectedPlayer: player.Player): Unit = {
        autoHealCheckbox.selected = selectedPlayer.autoHealEnabled
        runeMakerCheckbox.selected = selectedPlayer.runeMakerEnabled
        caveBotCheckbox.selected = selectedPlayer.caveBotEnabled
      }

      // Update checkInterval value in the player.Player class when the TextField value changes
      listenTo(checkIntervalTextField)
      reactions += {
        case EditDone(`checkIntervalTextField`) =>
          val selectedPlayer = playerClassList(exampleDropdown.selection.index)
          selectedPlayer.checkInterval = checkIntervalTextField.text.toInt
      }

      // Load the checkInterval value from the selected player when the selection changes
      listenTo(exampleDropdown.selection)
      reactions += {
        case SelectionChanged(_) =>
          val selectedPlayer = playerClassList(exampleDropdown.selection.index)
          updateCheckboxes(selectedPlayer)
          checkIntervalTextField.text = selectedPlayer.checkInterval.toString
          updateExample()
      }


      listenTo(exampleDropdown.selection)
      reactions += {
        case SelectionChanged(_) =>
          val selectedPlayer = playerClassList(exampleDropdown.selection.index)
          updateCheckboxes(selectedPlayer)
          updateExample()
      }


      val stopBotButton = Button("Stop Bot") {
        runningBot = false
      }
      val runBotButton = Button("Run Bot") {
        runningBot = true
        Future {
          runBot(playerClassList, runningBot, MonstersClass)
        }
      }

      contents += runBotButton
      contents += stopBotButton

      listenTo(exampleDropdown.selection, this)

      val escapePressed: Reaction = {
        case KeyPressed(_, Escape, _, _) => stopBotButton.doClick()
      }
      val buttonPressed: Reaction = {
        case ButtonClicked(`stopBotButton`) => runningBot = false
        case ButtonClicked(`runBotButton`) => runningBot = true
      }

      reactions += buttonPressed
      reactions += escapePressed
      reactions += {
        case SelectionChanged(_) => updateExample()
      }

    })

    pages += new TabbedPane.Page("Auto Heal", Component.wrap(new JPanel(new GridBagLayout) {
      val lightHealLabel = new JLabel("LoSpell")
      val lightHealHealthLabel = new JLabel("Health")
      val lightHealManaLabel = new JLabel("Mana")
      val strongHealLabel = new JLabel("HiSpell")
      val strongHealHealthLabel = new JLabel("Health")
      val strongHealManaLabel = new JLabel("Mana")
      val ihHealLabel = new JLabel("IH rune")
      val ihHealHealthLabel = new JLabel("Health")
      val ihHealManaLabel = new JLabel("Mana")
      val uhHealLabel = new JLabel("UH rune")
      val uhHealHealthLabel = new JLabel("Health")
      val uhHealManaLabel = new JLabel("Mana")
      val hPotionHealLabel = new JLabel("H potion")
      val hPotionHealHealthLabel = new JLabel("Health")
      val hPotionHealManaLabel = new JLabel("Mana")
      val mPotionHealLabel = new JLabel("M potion")
      val mPotionHealManaMinLabel = new JLabel("Mana")


      val c = new GridBagConstraints()
      c.insets = new Insets(5, 5, 5, 5)

      // Define the preferred width for each text field
      val spellFieldWidth = 150
      val healthManaFieldWidth = 70

      // First row
      c.gridx = 0
      c.gridy = 0
      add(lightHealLabel, c)

      c.gridx = 1
      c.gridwidth = 2
      c.fill = GridBagConstraints.HORIZONTAL
      lightHealSpellField.peer.setPreferredSize(new Dimension(spellFieldWidth, lightHealSpellField.peer.getPreferredSize.height))
      add(lightHealSpellField.peer, c)

      c.gridx = 3
      c.gridwidth = 1
      c.fill = GridBagConstraints.NONE
      add(lightHealHealthLabel, c)

      c.gridx = 4
      c.gridwidth = 1
      c.fill = GridBagConstraints.HORIZONTAL
      lightHealHealthField.peer.setPreferredSize(new Dimension(healthManaFieldWidth, lightHealHealthField.peer.getPreferredSize.height))
      add(lightHealHealthField.peer, c)

      c.gridx = 5
      c.gridwidth = 1
      c.fill = GridBagConstraints.HORIZONTAL
      lightHealManaLabel.setPreferredSize(new Dimension(healthManaFieldWidth, lightHealManaLabel.getPreferredSize.height))
      add(lightHealManaLabel, c)

      c.gridx = 6
      c.gridwidth = 1
      c.fill = GridBagConstraints.HORIZONTAL
      lightHealManaField.peer.setPreferredSize(new Dimension(healthManaFieldWidth, lightHealManaField.peer.getPreferredSize.height))
      add(lightHealManaField.peer, c)

      // Second row
      c.gridx = 0
      c.gridy = 1
      add(strongHealLabel, c)

      c.gridx = 1
      c.gridwidth = 2
      c.fill = GridBagConstraints.HORIZONTAL
      strongHealSpellField.peer.setPreferredSize(new Dimension(spellFieldWidth, strongHealSpellField.peer.getPreferredSize.height))
      add(strongHealSpellField.peer, c)

      c.gridx = 3
      c.gridwidth = 1
      c.fill = GridBagConstraints.NONE
      add(strongHealHealthLabel, c)

      c.gridx = 4
      c.gridwidth = 1
      c.fill = GridBagConstraints.HORIZONTAL
      strongHealHealthField.peer.setPreferredSize(new Dimension(healthManaFieldWidth, strongHealHealthField.peer.getPreferredSize.height))
      add(strongHealHealthField.peer, c)

      c.gridx = 5
      c.gridwidth = 1
      c.fill = GridBagConstraints.HORIZONTAL
      strongHealManaLabel.setPreferredSize(new Dimension(healthManaFieldWidth, strongHealManaLabel.getPreferredSize.height))
      add(strongHealManaLabel, c)

      c.gridx = 6
      c.gridwidth = 1
      c.fill = GridBagConstraints.HORIZONTAL
      strongHealManaField.peer.setPreferredSize(new Dimension(healthManaFieldWidth, strongHealManaField.peer.getPreferredSize.height))
      add(strongHealManaField.peer, c)

      c.gridx = 0
      c.gridy = 2
      add(ihHealLabel, c)

      c.gridx = 3
      c.gridwidth = 1
      c.fill = GridBagConstraints.NONE
      add(ihHealHealthLabel, c)

      c.gridx = 4
      c.gridwidth = 1
      c.fill = GridBagConstraints.HORIZONTAL
      ihHealHealthField.peer.setPreferredSize(new Dimension(healthManaFieldWidth, ihHealHealthField.peer.getPreferredSize.height))
      add(ihHealHealthField.peer, c)

      c.gridx = 5
      c.gridwidth = 1
      c.fill = GridBagConstraints.HORIZONTAL
      ihHealManaLabel.setPreferredSize(new Dimension(healthManaFieldWidth, ihHealManaLabel.getPreferredSize.height))
      add(ihHealManaLabel, c)

      c.gridx = 6
      c.gridwidth = 1
      c.fill = GridBagConstraints.HORIZONTAL
      ihHealManaField.peer.setPreferredSize(new Dimension(healthManaFieldWidth, ihHealManaField.peer.getPreferredSize.height))
      add(ihHealManaField.peer, c)

      c.gridx = 0
      c.gridy = 3
      add(uhHealLabel, c)

      c.gridx = 3
      c.gridwidth = 1
      c.fill = GridBagConstraints.NONE
      add(uhHealHealthLabel, c)

      c.gridx = 4
      c.gridwidth = 1
      c.fill = GridBagConstraints.HORIZONTAL
      uhHealHealthField.peer.setPreferredSize(new Dimension(healthManaFieldWidth, uhHealHealthField.peer.getPreferredSize.height))
      add(uhHealHealthField.peer, c)

      c.gridx = 5
      c.gridwidth = 1
      c.fill = GridBagConstraints.HORIZONTAL
      uhHealManaLabel.setPreferredSize(new Dimension(healthManaFieldWidth, uhHealManaLabel.getPreferredSize.height))
      add(uhHealManaLabel, c)

      c.gridx = 6
      c.gridwidth = 1
      c.fill = GridBagConstraints.HORIZONTAL
      uhHealManaField.peer.setPreferredSize(new Dimension(healthManaFieldWidth, uhHealManaField.peer.getPreferredSize.height))
      add(uhHealManaField.peer, c)

      c.gridx = 0
      c.gridy = 4
      add(hPotionHealLabel, c)

      c.gridx = 3
      c.gridwidth = 1
      c.fill = GridBagConstraints.NONE
      add(hPotionHealHealthLabel, c)

      c.gridx = 4
      c.gridwidth = 1
      c.fill = GridBagConstraints.HORIZONTAL
      hPotionHealHealthField.peer.setPreferredSize(new Dimension(healthManaFieldWidth, hPotionHealHealthField.peer.getPreferredSize.height))
      add(hPotionHealHealthField.peer, c)

      c.gridx = 5
      c.gridwidth = 1
      c.fill = GridBagConstraints.HORIZONTAL
      hPotionHealManaLabel.setPreferredSize(new Dimension(healthManaFieldWidth, hPotionHealManaLabel.getPreferredSize.height))
      add(hPotionHealManaLabel, c)

      c.gridx = 6
      c.gridwidth = 1
      c.fill = GridBagConstraints.HORIZONTAL
      hPotionHealManaField.peer.setPreferredSize(new Dimension(healthManaFieldWidth, hPotionHealManaField.peer.getPreferredSize.height))
      add(hPotionHealManaField.peer, c)


      c.gridx = 0
      c.gridy = 5
      add(mPotionHealLabel, c)

      c.gridx = 5
      c.gridwidth = 1
      c.fill = GridBagConstraints.NONE
      add(mPotionHealManaMinLabel, c)

      c.gridx = 6
      c.gridwidth = 1
      c.fill = GridBagConstraints.HORIZONTAL
      mPotionHealManaMinField.peer.setPreferredSize(new Dimension(healthManaFieldWidth, mPotionHealManaMinField.peer.getPreferredSize.height))
      add(mPotionHealManaMinField.peer, c)


      // Button row
      c.gridy = 6
      c.gridx = 3
      c.gridwidth = 2
      add(updateButton.peer, c)

    }))


    pages += new TabbedPane.Page("Cave Bot", new GridBagPanel {
      val caveBotComboBox = new ComboBox(caveBotClassList.map(_.getCaveBotName))
      val waypointsScrollList = new ListView[Int]()
      val newCaveBotNameTextField = new TextField()
      val newCaveBotButton = new Button("New")
      val saveCaveBotButton = new Button("Save")

      val imageLabel = new Label {
        preferredSize = new Dimension(80, 80)
      }

      // Method to update the waypointsScrollList
      def updateWaypointsList(caveBot: CaveBot): Unit = {
        waypointsScrollList.listData = caveBot.waypointsList.indices.toList
      }

      // Method to update the imageLabel with a specific waypoint
      def updateImage(caveBot: CaveBot, waypointIndex: Int): Unit = {
        val waypointArray = caveBot.waypointsList(waypointIndex)
        val mat = arrayToMat(waypointArray)
        val img = matToBufferedImage(mat)
        imageLabel.icon = new ImageIcon(img)
      }

      // Setup UI components
      layout(caveBotComboBox) = new Constraints {
        grid = (0, 0)
        fill = Fill.Horizontal
      }
      layout(new ScrollPane(waypointsScrollList)) = new Constraints {
        grid = (0, 1)
        fill = Fill.Both
        weighty = 1
      }
      layout(imageLabel) = new Constraints {
        grid = (1, 1)
        fill = Fill.None
        anchor = Anchor.West
      }

      val actionButtons = List("Move", "Rope", "Stairs", "Ladder", "Shovel").map(new Button(_))
      for ((button, idx) <- actionButtons.zipWithIndex) {
        layout(button) = new Constraints {
          grid = (idx, 2)
          fill = Fill.Horizontal
        }
      }

      // Update UI components when a new CaveBot is selected
      listenTo(caveBotComboBox.selection)
      reactions += {
        case SelectionChanged(`caveBotComboBox`) =>
          val selectedCaveBot = caveBotClassList(caveBotComboBox.selection.index)
          val selectedPlayer = playerClassList(exampleDropdown.selection.index)
          selectedPlayer.setCaveBot(selectedCaveBot)
          updateWaypointsList(selectedCaveBot)
          if (selectedCaveBot.waypointsList.nonEmpty) {
            updateImage(selectedCaveBot, 0)
            waypointsScrollList.selectIndices(0)
          }
      }


      // Update the image when a new waypoint index is selected
      listenTo(waypointsScrollList.selection)
      reactions += {
        case SelectionChanged(`waypointsScrollList`) =>
          val selectedCaveBot = caveBotClassList(caveBotComboBox.selection.index)
          val selectedWaypointIndex = waypointsScrollList.selection.indices.headOption.getOrElse(-1)
          if (selectedWaypointIndex >= 0) {
            updateImage(selectedCaveBot, selectedWaypointIndex)
          }
      }

      // Initialize the UI with the first CaveBot
      if (caveBotClassList.nonEmpty) {
        val firstCaveBot = caveBotClassList.head
        updateWaypointsList(firstCaveBot)
        if (firstCaveBot.waypointsList.nonEmpty) {
          updateImage(firstCaveBot, 0)
          waypointsScrollList.selectIndices(0)
        }
      }

      actionButtons.head.reactions += {
        case ButtonClicked(_) =>
          val selectedCaveBot = caveBotClassList(caveBotComboBox.selection.index)
          val selectedWaypointIndex = waypointsScrollList.selection.indices.headOption.getOrElse(-1)
          var selectedPlayer = getSelectedPlayer(playerClassList)
          selectedPlayer.updateScreen()

          if (selectedWaypointIndex >= 0) {
            selectedCaveBot.waypointsList = selectedCaveBot.waypointsList.patch(selectedWaypointIndex + 1, List(recordMove(selectedPlayer, selectedCaveBot)), 0)
          } else if (selectedCaveBot.waypointsList.isEmpty) {
            selectedCaveBot.waypointsList = List(recordMove(selectedPlayer, selectedCaveBot))
          }

          updateWaypointsList(selectedCaveBot)
      }

      def getSelectedPlayer(playerClassList: List[Player]): Player = {
        val selectedName = exampleDropdown.selection.item
        playerClassList.find(_.characterName == selectedName).getOrElse {
          throw new NoSuchElementException(s"Player with character name '$selectedName' not found")
        }
      }

      layout(newCaveBotNameTextField) = new Constraints {
        grid = (0, 3)
        fill = Fill.Horizontal
      }
      layout(newCaveBotButton) = new Constraints {
        grid = (1, 3)
        fill = Fill.Horizontal
      }
      layout(saveCaveBotButton) = new Constraints {
        grid = (2, 3)
        fill = Fill.Horizontal
      }

      listenTo(newCaveBotButton)
      newCaveBotButton.reactions += {
        case ButtonClicked(_) =>
          val newCaveBotName = newCaveBotNameTextField.text // Get the text from the TextField
          val newCaveBot = new CaveBot(newCaveBotName) // Pass the name to the constructor
          caveBotClassList = caveBotClassList :+ newCaveBot
          caveBotComboBox.peer.setModel(ComboBox.newConstantModel(caveBotClassList.map(_.getCaveBotName)))
      }

      listenTo(saveCaveBotButton)
      reactions += {
        case ButtonClicked(`saveCaveBotButton`) =>
          val selectedCaveBot = caveBotClassList(caveBotComboBox.selection.index)
          selectedCaveBot.saveStateToFile(selectedCaveBot.getCaveBotName)
      }

    })


  }
  updateExample()
}