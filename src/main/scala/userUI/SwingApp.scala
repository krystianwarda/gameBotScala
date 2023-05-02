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
import utils.core.runBot

import scala.swing.GridBagPanel.Anchor
//import scala.swing.event.Key.Escape
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
import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global
import java.awt.event.KeyEvent.{VK_ESCAPE, getKeyText}
import scala.swing.Reactions.Reaction
import scala.swing.event.{ButtonClicked, KeyPressed}
import scala.swing.event.Key
import Key.Escape

case class SwingApp(examples: List[player.Player]) extends MainFrame {
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
    val uhHealHealthVar = uhHealManaField.text.toInt
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


  val updateButton = new Button("Update") {
    reactions += {
      case ButtonClicked(_) =>
        saveExample()
        updateExample()
    }
  }

  contents = new TabbedPane {

    import scala.swing.event.Key
    import Key.Escape

    pages += new TabbedPane.Page("Main", new BoxPanel(Orientation.Vertical) {
      contents += exampleDropdown

      val stopBotButton = Button("Stop Bot") {
        runningBot = false
      }
      val runBotButton = Button("Run Bot") {
        runningBot = true
        Future {
          runBot(examples, runningBot)
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

    pages += new TabbedPane.Page("Rune Maker", Component.wrap(new JPanel(new GridBagLayout) {}))
    pages += new TabbedPane.Page("Aim Bot", Component.wrap(new JPanel(new GridBagLayout) {}))
    pages += new TabbedPane.Page("Cave Bot", Component.wrap(new JPanel(new GridBagLayout) {}))

  }
  updateExample()
}
