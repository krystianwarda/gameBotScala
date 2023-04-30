package userUI

import player.Player

import scala.swing.TabbedPane._
import scala.swing._
import scala.collection.mutable.ArrayBuffer
import java.awt.{Font, GraphicsEnvironment}
import javax.swing.{DefaultDesktopManager, UIManager}
import userUI.UserSettings
//import monix.reactive.{Observable, Observer}
import monix.execution.Scheduler.Implicits.global
import monix.execution.rstreams.Subscription
import scala.swing.event.SelectionChanged

import java.awt.event.ActionEvent
import java.beans.PropertyChangeEvent
import monix.reactive._
import monix.execution.Scheduler.Implicits.global
import monix.execution.Cancelable
import monix.reactive.observables.ConnectableObservable

import monix.execution.Cancelable
import monix.reactive.Observable
import monix.reactive.observers.Subscriber
import java.awt.{Dimension, Font}
import java.awt.event.ActionEvent
import java.beans.PropertyChangeEvent
import javax.swing.{JComboBox, JLabel}
import monix.execution.Cancelable
import monix.reactive.Observable
import monix.reactive.observers.Subscriber
import scala.swing.{BoxPanel, Dimension, Label, MainFrame, Orientation, SimpleSwingApplication, TabbedPane}
import scala.swing.Swing._
import scala.swing.ComboBox
import scala.swing.event.SelectionChanged
import scala.swing.event.ButtonClicked

class SwingApp(playerList: List[Player]) extends SimpleSwingApplication {
  // Define a ComboBox with player names
  val playerComboBox = new ComboBox(playerList.map(_.characterName))

  // Get a list of player names from the playerList
  val playerNames = playerList.map(_.characterName)

  // Define class members for the text fields
  private val strongSpellField = new TextField
  private val strongHealthField = new TextField
  private val strongManaField = new TextField
  private val lightSpellField = new TextField
  private val lightHealField = new TextField
  private val lightManaField = new TextField

  def top = new MainFrame {
    title = "TibiaYBB - Younger Brother Bot"

    val tabs = new TabbedPane {
      pages += new TabbedPane.Page("Activate", activate)
      pages += new TabbedPane.Page("Auto Heal", autoHeal)
    }
    contents = tabs
    font = new Font("Helvetica", Font.PLAIN, 8)
    size = new Dimension(600, 400)

    def activate = new BoxPanel(Orientation.Vertical) {
      // Create a label for "Character name"
      val characterNameLabel = new Label("Character name:")

      // Create a ComboBox with player names
      val playerComboBox = new ComboBox(playerNames)

      // Wrap the ComboBox inside a BorderPanel to align it to the left
      val comboBoxPanel = new BorderPanel {
        layout(playerComboBox) = BorderPanel.Position.West
      }

      // Add the label and ComboBox panel to a FlowPanel to arrange them horizontally
      contents += new FlowPanel(FlowPanel.Alignment.Left)(characterNameLabel, comboBoxPanel)
    }

    // Define the autoHeal panel
    def autoHeal = new BoxPanel(Orientation.Vertical) {
      // Get the selected player from the ComboBox
      val selectedPlayer = playerList(playerComboBox.selection.index)

      // Create a label to display the selected player's name
      val nameLabel = new Label(s"Character ${selectedPlayer.characterName}")

      // Create labels and text fields for the strong heal values
      val strongSpellLabel = new Label("Strong Spell: ")
      strongSpellField.text = selectedPlayer.botStrongHealSpell
      strongSpellField.columns = 10
      val strongHealthLabel = new Label("Health: ")
      strongHealthField.text = selectedPlayer.botStrongHealHealth.toString
      strongHealthField.columns = 5
      val strongManaLabel = new Label("Mana: ")
      strongManaField.text = selectedPlayer.botStrongHealMana.toString
      strongManaField.columns = 5

      // Create labels and text fields for the light heal values
      val lightSpellLabel = new Label("Light Spell: ")
      lightSpellField.text = selectedPlayer.botLightHealSpell
      lightSpellField.columns = 10
      val lightHealLabel = new Label("Health: ")
      lightHealField.text = selectedPlayer.botLightHealValue.toString
      lightHealField.columns = 5
      val lightManaLabel = new Label("Mana: ")
      lightManaField.text = selectedPlayer.botLightHealMana.toString
      lightManaField.columns = 5

      // Create an update button to apply the changes
      val updateButton = new Button("Update") {
        reactions += {
          case ButtonClicked(_) =>
            // Update the values in the selected player object
            selectedPlayer.botStrongHealSpell = strongSpellField.text
            selectedPlayer.botStrongHealHealth = strongHealthField.text.toInt
            selectedPlayer.botStrongHealMana = strongManaField.text.toInt
            selectedPlayer.botLightHealSpell = lightSpellField.text
            selectedPlayer.botLightHealValue = lightHealField.text.toInt
            selectedPlayer.botLightHealMana = lightManaField.text.toInt
        }
      }

      // Add the labels and fields to the panel
      contents += nameLabel
      contents += new FlowPanel(strongSpellLabel, strongSpellField, strongHealthLabel, strongHealthField, strongManaLabel, strongManaField)
      contents += new FlowPanel(lightSpellLabel, lightSpellField, lightHealLabel, lightHealField, lightManaLabel, lightManaField)
      contents += updateButton
    }



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


    def caveBot = new BoxPanel(Orientation.Vertical) {
      val rows = ArrayBuffer[ArrayBuffer[TextField]]()
      for (i <- 1 to 10) {
        val row = ArrayBuffer[TextField]()
        val col1 = new TextField()
        val col2 = new TextField()
        val col3 = new TextField()
        val col4 = new TextField()
        val col5 = new TextField()
        val col6 = new TextField()

        row += col1
        row += col2
        row += col3
        row += col4
        row += col5
        row += col6

        contents += new BoxPanel(Orientation.Horizontal) {
          contents ++= row
        }
        rows += row
      }

      val storeInputButton = new Button("Store Input") {
        reactions += {
          case event.ButtonClicked(_) => storeInput()
        }
      }

      contents += storeInputButton

      def storeInput(): Unit = {
        val inputArray = rows.map(row => row.map(_.text).toArray).toArray
        val inputClass = new InputClass(inputArray)
        // Do something with the inputClass object
      }
    }

    def rowAutoHealing(col1Label: String, col2Label: String, col3Label: String, col4Label: String, col5Label: String, col6Label: String): BoxPanel = {
      val row = ArrayBuffer[Component]()
      val col1 = new Label(col1Label) {
        preferredSize = new Dimension(100, 10)
      }
      val col2 = new TextField() {
        preferredSize = new Dimension(100, 10)
      }
      val col3 = new Label(col3Label) {
        preferredSize = new Dimension(50, 10)
      }
      val col4 = new TextField() {
        preferredSize = new Dimension(50, 10)
      }
      val col5 = new Label(col5Label) {
        preferredSize = new Dimension(50, 10)
      }
      val col6 = new TextField() {
        preferredSize = new Dimension(50, 10)
      }

      row += col1
      row += col2
      row += col3
      row += col4
      row += col5
      row += col6

      new BoxPanel(Orientation.Horizontal) {
        contents ++= row
        border = Swing.EmptyBorder(3, 3, 3, 3)
      }
    }
  }
}

class InputClass(inputs: Array[Array[String]])

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