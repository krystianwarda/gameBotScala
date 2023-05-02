package userUI

//import monix.reactive.subjects.Var
//import monix.reactive.subjects.Var
import monix.reactive.subjects.Var
import player.Player
import scalafx.beans.property.ObjectProperty

import scala.swing.TabbedPane._
import scala.swing._
import scala.collection.mutable.ArrayBuffer
import java.awt.{Font, GraphicsEnvironment, GridBagLayout}
import javax.swing.{DefaultDesktopManager, UIManager}
import userUI.UserSettings

import scala.swing.GridBagPanel.Anchor
//import monix.reactive.{Observable, Observer}
//import monix.execution.Scheduler.Implicits.global
//import monix.execution.rstreams.Subscription
import scala.swing.event.SelectionChanged

import java.awt.event.ActionEvent
import java.beans.PropertyChangeEvent
//import monix.reactive._
//import monix.execution.Scheduler.Implicits.global
//import monix.execution.Cancelable
//import monix.reactive.observables.ConnectableObservable

//import monix.execution.Cancelable
//import monix.reactive.Observable
//import monix.reactive.observers.Subscriber
import java.awt.{Dimension, Font}
import java.awt.event.ActionEvent
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

case class SwingApp(examples: List[player.Player]) extends MainFrame {
  title = "TibiaYBB - Younger Brother Bot"
  preferredSize = new Dimension(600, 300)

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
    mPotionHealManaMaxField.text = selectedExample.botMPotionHealManaMax.toString
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

    val uhHealHealthVar = uhHealManaField.text.toInt
    val uhHealManaVar = uhHealManaField.text.toInt

    val hPotionHealHealthVar = hPotionHealHealthField.text.toInt
    val hPotionHealManaVar = hPotionHealManaField.text.toInt

    val mPotionHealManaMinVar = mPotionHealManaMinField.text.toInt
    val mPotionHealManaMaxVar = mPotionHealManaMaxField.text.toInt


    selectedExample.updateAutoHeal(lightHealSpellVar, lightHealHealthVar, lightHealManaVar,
      strongHealSpellVar, strongHealHealthVar, strongHealManaVar,
      ihHealHealthVar, ihHealManaVar,
      uhHealHealthVar, uhHealManaVar,
      hPotionHealHealthVar, hPotionHealManaVar,
      mPotionHealManaMinVar, mPotionHealManaMaxVar)
  }

  val exampleNames = examples.map(_.characterName)
  val exampleMap = examples.map(e => e.characterName -> e).toMap

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
  val mPotionHealManaMaxField = new TextField()


  val updateButton = new Button("Update") {
    reactions += {
      case ButtonClicked(_) =>
        saveExample()
        updateExample()
    }
  }

  contents = new TabbedPane {
    pages += new TabbedPane.Page("Main", new BoxPanel(Orientation.Vertical) {
      contents += exampleDropdown

      listenTo(exampleDropdown.selection)
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
      val mPotionHealManaMinLabel = new JLabel("MMin")
      val mPotionHealManaMaxLabel = new JLabel("MMax")


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

      c.gridx = 3
      c.gridwidth = 1
      c.fill = GridBagConstraints.NONE
      add(mPotionHealManaMinLabel, c)

      c.gridx = 4
      c.gridwidth = 1
      c.fill = GridBagConstraints.HORIZONTAL
      mPotionHealManaMinField.peer.setPreferredSize(new Dimension(healthManaFieldWidth, mPotionHealManaMinField.peer.getPreferredSize.height))
      add(mPotionHealManaMinField.peer, c)

      c.gridx = 5
      c.gridwidth = 1
      c.fill = GridBagConstraints.HORIZONTAL
      mPotionHealManaMaxLabel.setPreferredSize(new Dimension(healthManaFieldWidth, mPotionHealManaMaxLabel.getPreferredSize.height))
      add(mPotionHealManaMaxLabel, c)

      c.gridx = 6
      c.gridwidth = 1
      c.fill = GridBagConstraints.HORIZONTAL
      mPotionHealManaMaxField.peer.setPreferredSize(new Dimension(healthManaFieldWidth, mPotionHealManaMaxField.peer.getPreferredSize.height))
      add(mPotionHealManaMaxField.peer, c)


      // Button row
      c.gridy = 6
      c.gridx = 3
      c.gridwidth = 2
      add(updateButton.peer, c)

    }))
  }
  updateExample()
}





//
//case class SwingApp(examples: List[player.Player]) extends MainFrame {
//  title = "TibiaYBB - Younger Brother Bot"
//  preferredSize = new Dimension(600, 300)
//
//  def updateExample(): Unit = {
//    val selectedName = exampleDropdown.selection.item
//    val selectedExample = exampleMap(selectedName)
//    exampleLabel.text = s"Name: ${selectedExample.characterName}, Level: ${selectedExample.charLevel}"
//    lightHealSpellField.text = selectedExample.botLightHealSpell.toString
//    lightHealHealthField.text = selectedExample.botLightHealHealth.toString
//    lightHealManaField.text = selectedExample.botLightHealMana.toString
//    strongHealSpellField.text = selectedExample.botStrongHealSpell.toString
//    strongHealHealthField.text = selectedExample.botStrongHealHealth.toString
//    strongHealManaField.text = selectedExample.botStrongHealMana.toString
//  }
//
//
//  def saveExample(): Unit = {
//    val selectedName = exampleDropdown.selection.item
//    val selectedExample = exampleMap(selectedName)
//    selectedExample.botLightHealSpell = lightHealSpellField.text
//    selectedExample.botLightHealHealth = lightHealHealthField.text.toInt
//    selectedExample.botLightHealMana = lightHealManaField.text.toInt
//    selectedExample.botStrongHealSpell = strongHealSpellField.text
//    selectedExample.botStrongHealHealth = strongHealHealthField.text.toInt
//    selectedExample.botStrongHealMana = strongHealManaField.text.toInt
//  }
//
//
//  val exampleNames = examples.map(_.characterName)
//  val exampleMap = examples.map(e => e.characterName -> e).toMap
//
//  val exampleDropdown = new ComboBox(exampleNames)
//
//  val exampleLabel = new Label()
//  val lightHealSpellField = new TextField()
//  val lightHealHealthField = new TextField()
//  val lightHealManaField = new TextField()
//  val strongHealSpellField = new TextField()
//  val strongHealHealthField = new TextField()
//  val strongHealManaField = new TextField()
//
//  val updateButton = new Button("Update") {
//    reactions += {
//      case ButtonClicked(_) =>
//        saveExample()
//        updateExample()
//    }
//  }
//
//  contents = new TabbedPane {
//    pages += new TabbedPane.Page("Examples", new BoxPanel(Orientation.Vertical) {
//      contents += exampleDropdown
//
//      listenTo(exampleDropdown.selection)
//      reactions += {
//        case SelectionChanged(_) =>
//          updateExample()
//          lightHealSpellField.text = exampleMap(exampleDropdown.selection.item).botLightHealSpell.toString
//          lightHealHealthField.text = exampleMap(exampleDropdown.selection.item).botLightHealHealth.toString
//          lightHealManaField.text = exampleMap(exampleDropdown.selection.item).botLightHealMana.toString
//          strongHealSpellField.text = exampleMap(exampleDropdown.selection.item).botStrongHealSpell.toString
//          strongHealHealthField.text = exampleMap(exampleDropdown.selection.item).botLightHealHealth.toString
//          strongHealManaField.text = exampleMap(exampleDropdown.selection.item).botLightHealMana.toString
//      }
//
//    })
//
//
//    pages += new TabbedPane.Page("Details", Component.wrap(new JPanel(new GridBagLayout) {
//      val lightHealLabel = new JLabel("LoSpell:")
//      val strongHealLabel = new JLabel("HiSpell:")
//      val lightHealHealthLabel = new JLabel("Health:")
//      val lightHealManaLabel = new JLabel("Mana:")
//      val strongHealHealthLabel = new JLabel("Health:")
//      val strongHealManaLabel = new JLabel("Mana:")
//
//      val lightHealSpellField = new JTextField(10)
//      val lightHealHealthField = new JTextField(5)
//      val lightHealManaField = new JTextField(5)
//      val strongHealSpellField = new JTextField(10)
//      val strongHealHealthField = new JTextField(5)
//      val strongHealManaField = new JTextField(5)
//
//      val c = new GridBagConstraints()
//      c.insets = new Insets(5, 5, 5, 5)
//
//
//      // First row
//      c.gridx = 0
//      c.gridy = 0
//      add(lightHealLabel, c)
//
//      c.gridx = 1
//      c.gridwidth = 2
//      c.fill = GridBagConstraints.HORIZONTAL
//      add(lightHealSpellField, c)
//
//      c.gridx = 3
//      c.gridwidth = 1
//      c.fill = GridBagConstraints.NONE
//      add(lightHealHealthLabel, c)
//
//      c.gridx = 4
//      c.gridwidth = 1
//      c.fill = GridBagConstraints.HORIZONTAL
//      add(lightHealHealthField, c)
//
//      c.gridx = 5
//      c.gridwidth = 1
//      c.fill = GridBagConstraints.HORIZONTAL
//      add(lightHealManaLabel, c)
//
//      c.gridx = 6
//      c.gridwidth = 1
//      c.fill = GridBagConstraints.HORIZONTAL
//      add(lightHealManaField, c)
//
//      // Second row
//      c.gridx = 0
//      c.gridy = 1
//      add(strongHealLabel, c)
//
//      c.gridx = 1
//      c.gridwidth = 2
//      c.fill = GridBagConstraints.HORIZONTAL
//      add(strongHealSpellField, c)
//
//      c.gridx = 3
//      c.gridwidth = 1
//      c.fill = GridBagConstraints.NONE
//      add(strongHealHealthLabel, c)
//
//      c.gridx = 4
//      c.gridwidth = 1
//      c.fill = GridBagConstraints.HORIZONTAL
//      add(strongHealHealthField, c)
//
//      c.gridx = 5
//      c.gridwidth = 1
//      c.fill = GridBagConstraints.HORIZONTAL
//      add(strongHealManaLabel, c)
//
//      c.gridx = 6
//      c.gridwidth = 1
//      c.fill = GridBagConstraints.HORIZONTAL
//      add(strongHealManaField, c)
//
//      // Button row
//      c.gridy = 2
//      c.gridx = 3
//      c.gridwidth = 2
//      val updateButton = new JButton("Update")
//      add(updateButton, c)
//
//    }))
//  }
//
//  updateExample()
//}


//
//class SwingApp(playerList: Seq[Player]) extends SimpleSwingApplication {
//
//
//
//
//  val selectedPlayerVar = ObjectProperty(playerList.head) // declare as a field of the class
//
//  def top = new MainFrame {
//    title = "TibiaYBB - Younger Brother Bot"
//
//    val tabs = new TabbedPane {
//      pages += new TabbedPane.Page("Activate", activatePanel(playerList))
//      pages += new TabbedPane.Page("Auto Heal", autoHealPanel(playerList))
//    }
//
//    listenTo(tabs.selection)
//
//    reactions += {
//      case SelectionChanged(_) =>
//        updateTabs(selectedPlayerVar(), tabs.selection.page.content)
//    }
//    //        updateAutoHealValues(selectedPlayerVar(), tabs.selection.page.content)
//
//    contents = tabs
//    font = new Font("Helvetica", Font.PLAIN, 8)
//    size = new Dimension(600, 400)
//
//    def updateAutoHealValues(player: Player, panel: BoxPanel): Unit = {
//      val autoHealSpellField = new TextField(selectedPlayerVar().botLightHealSpell)
//      val autoHealHealthField = new TextField(selectedPlayerVar().botLightHealHealth.toString)
//      val autoHealManaField = new TextField(selectedPlayerVar().botLightHealMana.toString)
//
//      val spellTextField = panel.contents.collectFirst { case tf: TextField if tf.peer eq autoHealSpellField.peer => tf }
//      val healthTextField = panel.contents.collectFirst { case tf: TextField if tf.peer eq autoHealHealthField.peer => tf }
//      val manaTextField = panel.contents.collectFirst { case tf: TextField if tf.peer eq autoHealManaField.peer => tf }
//      spellTextField.foreach(_.text = player.botLightHealSpell)
//      healthTextField.foreach(_.text = player.botLightHealHealth.toString)
//      manaTextField.foreach(_.text = player.botLightHealMana.toString)
//    }
//
//    def updateTabs(player: Player, component: Component): Unit = {
//      component match {
//        case activatePanel: BoxPanel =>
//          val nameLabel = activatePanel.contents.collectFirst { case lbl: Label if lbl.text.startsWith("Character") => lbl }
//          val levelLabel = activatePanel.contents.collectFirst { case lbl: Label if lbl.text.startsWith("Level") => lbl }
//          nameLabel.foreach(_.text = s"Character ${player.characterName}")
//          levelLabel.foreach(_.text = s"Level ${player.charLevel}")
//        case autoHealPanel: BoxPanel =>
//          val nameLabel = autoHealPanel.contents.collectFirst { case lbl: Label if lbl.text.startsWith("Character") => lbl }
//          val levelLabel = autoHealPanel.contents.collectFirst { case lbl: Label if lbl.text.startsWith("Level") => lbl }
//          nameLabel.foreach(_.text = s"Character ${player.characterName}")
//          levelLabel.foreach(_.text = s"Level ${player.charLevel}")
//          updateAutoHealValues(player, autoHealPanel)
//        case _ =>
//      }
//    }
//
//
//    def activatePanel(playerList: Seq[Player]): BoxPanel = new BoxPanel(Orientation.Vertical) {
//      // Declare nameLabel and levelLabel as var variables
//      var nameLabel: Label = _
//      var levelLabel: Label = _
//
//      // Create a combo box to select the active player
//      val playerComboBox = new ComboBox(playerList) {
//        renderer = Renderer(_.characterName)
//        selection.item = selectedPlayerVar()
//      }
//
//      // Add a listener to the combo box that updates the selected player variable and the auto-heal values in the text fields
//      playerComboBox.selection.reactions += {
//        case SelectionChanged(_) =>
//          selectedPlayerVar() = playerComboBox.selection.item
//          updateTabs(selectedPlayerVar(), this)
//      }
//
//      // Create a label to display the selected player's name and assign to nameLabel
//      nameLabel = new Label(s"Character ${selectedPlayerVar().characterName}")
//
//      // Create a label to display the selected player's level and assign to levelLabel
//      levelLabel = new Label(s"Level ${selectedPlayerVar().charLevel}")
//
//      // Add the label, combo box, and button to the panel
//      contents += playerComboBox
//      contents += nameLabel
//      contents += levelLabel
//    }
//
//    def autoHealPanel(playerList: Seq[Player]) = new BoxPanel(Orientation.Vertical) {
//
//      val playerComboBox = new ComboBox(playerList) {
//        renderer = Renderer(_.characterName)
//        selection.item = selectedPlayerVar()
//      }
//
//      val nameLabel = new Label(s"Character ${selectedPlayerVar().characterName}")
//      val levelLabel = new Label(s"Level ${selectedPlayerVar().charLevel}")
//      val manaStatusLabel = new Label(s"Mana status: ${selectedPlayerVar().manaPoints}")
//      val spellLabel = new Label(s"Spell: ")
//      val autoHealSpellField = new TextField(selectedPlayerVar().botLightHealSpell)
//      autoHealSpellField.columns = 10
//      val healthLabel = new Label("Health: ")
//      val autoHealHealthField = new TextField(selectedPlayerVar().botLightHealHealth.toString)
//      autoHealHealthField.columns = 5
//      val manaLabel = new Label("Mana: ")
//      val autoHealManaField = new TextField(selectedPlayerVar().botLightHealMana.toString)
//      autoHealManaField.columns = 5
//
//      contents += nameLabel
//      contents += levelLabel
//      contents += manaStatusLabel
//      contents += spellLabel
//      contents += autoHealSpellField
//      contents += healthLabel
//      contents += autoHealHealthField
//      contents += manaLabel
//      contents += autoHealManaField
//    }
//
//  }
//}



//class InputClass(inputs: Array[Array[String]])


//    pages += new TabbedPane.Page("Auto Healing", autoHealing) {
//      peer.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10))
//    }
//
////    pages += new TabbedPane.Page("Cavebot", caveBot)
//    def autoHealing = new BoxPanel(Orientation.Vertical) {
//      val rows = ArrayBuffer[ArrayBuffer[Component]]()
//
//      val row1 = rowAutoHealing("Strong spell", "", "Health: ", "", "Mana: ", "")
//      val row2 = rowAutoHealing("Light spell:", "", "Health: ", "", "Mana: ", "")
//      val row3 = rowAutoHealing("UH Rune:", "", "Health: ", "", "Mana: ", "")
//      val row4 = rowAutoHealing("IH Rune:", "", "Health: ", "", "Mana: ", "")
//      val row5 = rowAutoHealing("HP Potion:", "", "Health: ", "", "Mana: ", "")
//      val row6 = rowAutoHealing("MP Potion:", "", "Health: ", "", "Mana: ", "")
//
//      contents += row1
//      contents += row2
//      contents += row3
//      contents += row4
//      contents += row5
//      contents += row6
//
//      val storeInputButton = new Button("Store Input") {
//        reactions += {
//          case event.ButtonClicked(_) => storeInput()
//        }
//      }
//
//      contents += Swing.VStrut(10)
//      contents += storeInputButton
//
//      def storeInput(): Unit = {
//        val inputArray = rows.map(row => row.map {
//          case tf: TextField => tf.text
//          case c: Component => c.toString
//        }.toArray).toArray
//        val inputClass = new InputClass(inputArray)
//        // Do something with the inputClass object
//      }
//    }


//def caveBot = new BoxPanel(Orientation.Vertical) {
//  val rows = ArrayBuffer[ArrayBuffer[TextField]]()
//  for (i <- 1 to 10) {
//    val row = ArrayBuffer[TextField]()
//    val col1 = new TextField()
//    val col2 = new TextField()
//    val col3 = new TextField()
//    val col4 = new TextField()
//    val col5 = new TextField()
//    val col6 = new TextField()
//
//    row += col1
//    row += col2
//    row += col3
//    row += col4
//    row += col5
//    row += col6
//
//    contents += new BoxPanel(Orientation.Horizontal) {
//      contents ++= row
//    }
//    rows += row
//  }
//
//  val storeInputButton = new Button("Store Input") {
//    reactions += {
//      case event.ButtonClicked(_) => storeInput()
//    }
//  }
//
//  contents += storeInputButton
//
//  def storeInput(): Unit = {
//    val inputArray = rows.map(row => row.map(_.text).toArray).toArray
//    val inputClass = new InputClass(inputArray)
//    // Do something with the inputClass object
//  }
//}
//
//def rowAutoHealing(col1Label: String, col2Label: String, col3Label: String, col4Label: String, col5Label: String, col6Label: String): BoxPanel = {
//  val row = ArrayBuffer[Component]()
//  val col1 = new Label(col1Label) {
//    preferredSize = new Dimension(100, 10)
//  }
//  val col2 = new TextField() {
//    preferredSize = new Dimension(100, 10)
//  }
//  val col3 = new Label(col3Label) {
//    preferredSize = new Dimension(50, 10)
//  }
//  val col4 = new TextField() {
//    preferredSize = new Dimension(50, 10)
//  }
//  val col5 = new Label(col5Label) {
//    preferredSize = new Dimension(50, 10)
//  }
//  val col6 = new TextField() {
//    preferredSize = new Dimension(50, 10)
//  }
//
//  row += col1
//  row += col2
//  row += col3
//  row += col4
//  row += col5
//  row += col6
//
//  new BoxPanel(Orientation.Horizontal) {
//    contents ++= row
//    border = Swing.EmptyBorder(3, 3, 3, 3)
//  }
//}
//



//pages += new Page("Initial tab", new BoxPanel(Orientation.Vertical) {
//  contents += new Label("This is Tab 1")
//  contents += new Button("Click me!")
//  contents += new ComboBox(List("Option 1", "Option 2", "Option 3"))
//  contents += new CheckBox("Check me!")
//})
//pages += new Page("radio buttons", new BoxPanel(Orientation.Vertical) {
//  contents += new Label("This is Tab 2")
//  contents += new RadioButton("Option 1")
//  contents += new RadioButton("Option 2")
//  contents += new RadioButton("Option 3")
//})
//pages += new Page("Healing", new BoxPanel(Orientation.Vertical) {
//  contents += new Label("This is Tab 2")
//  contents += new RadioButton("Option 1")
//  contents += new RadioButton("Option 2")
//  contents += new RadioButton("Option 3")
//})
//pages += new Page("Cavebot", new BoxPanel(Orientation.Vertical) {
//  contents += new Label("This is Tab 3")
//  contents += new TextField(20)
//  contents += new PasswordField(20)
//  contents += new TextArea(5, 20)
//})
//pages += new Page("Aimbot", new BoxPanel(Orientation.Vertical) {
//  contents += new Label("This is Tab 4")
//  contents += new Slider()
//  contents += new ProgressBar()
//  contents += new ScrollPane(new Label("Scrollable content"))
//})