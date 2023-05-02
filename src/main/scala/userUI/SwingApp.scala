package userUI

//import monix.reactive.subjects.Var
//import monix.reactive.subjects.Var
import monix.reactive.subjects.Var
import player.Player
import scalafx.beans.property.ObjectProperty

import scala.swing.TabbedPane._
import scala.swing._
import scala.collection.mutable.ArrayBuffer
import java.awt.{Font, GraphicsEnvironment}
import javax.swing.{DefaultDesktopManager, UIManager}
import userUI.UserSettings
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
//import scalafx.beans.property.{ObjectProperty, Var}
//import monix.execution.Scheduler.Implicits.global


class SwingApp(playerList: Seq[Player]) extends SimpleSwingApplication {

  val selectedPlayerVar = ObjectProperty(playerList.head) // declare as a field of the class

  def top = new MainFrame {
    title = "TibiaYBB - Younger Brother Bot"

    val tabs = new TabbedPane {
      pages += new TabbedPane.Page("Activate", activatePanel(playerList))
      pages += new TabbedPane.Page("Auto Heal", autoHealPanel(playerList))
    }

    listenTo(tabs.selection)

    reactions += {
      case SelectionChanged(_) =>
        updateTabs(selectedPlayerVar(), tabs.selection.page.content)
    }
    //        updateAutoHealValues(selectedPlayerVar(), tabs.selection.page.content)

    contents = tabs
    font = new Font("Helvetica", Font.PLAIN, 8)
    size = new Dimension(600, 400)

    def updateAutoHealValues(player: Player, panel: BoxPanel): Unit = {
      val autoHealSpellField = new TextField(selectedPlayerVar().botLightHealSpell)
      val autoHealHealthField = new TextField(selectedPlayerVar().botLightHealHealth.toString)
      val autoHealManaField = new TextField(selectedPlayerVar().botLightHealMana.toString)

      val spellTextField = panel.contents.collectFirst { case tf: TextField if tf.peer eq autoHealSpellField.peer => tf }
      val healthTextField = panel.contents.collectFirst { case tf: TextField if tf.peer eq autoHealHealthField.peer => tf }
      val manaTextField = panel.contents.collectFirst { case tf: TextField if tf.peer eq autoHealManaField.peer => tf }
      spellTextField.foreach(_.text = player.botLightHealSpell)
      healthTextField.foreach(_.text = player.botLightHealHealth.toString)
      manaTextField.foreach(_.text = player.botLightHealMana.toString)
    }

    def updateTabs(player: Player, component: Component): Unit = {
      component match {
        case activatePanel: BoxPanel =>
          val nameLabel = activatePanel.contents.collectFirst { case lbl: Label if lbl.text.startsWith("Character") => lbl }
          val levelLabel = activatePanel.contents.collectFirst { case lbl: Label if lbl.text.startsWith("Level") => lbl }
          nameLabel.foreach(_.text = s"Character ${player.characterName}")
          levelLabel.foreach(_.text = s"Level ${player.charLevel}")
        case autoHealPanel: BoxPanel =>
          val nameLabel = autoHealPanel.contents.collectFirst { case lbl: Label if lbl.text.startsWith("Character") => lbl }
          val levelLabel = autoHealPanel.contents.collectFirst { case lbl: Label if lbl.text.startsWith("Level") => lbl }
          nameLabel.foreach(_.text = s"Character ${player.characterName}")
          levelLabel.foreach(_.text = s"Level ${player.charLevel}")
          updateAutoHealValues(player, autoHealPanel)
        case _ =>
      }
    }


    def activatePanel(playerList: Seq[Player]): BoxPanel = new BoxPanel(Orientation.Vertical) {
      // Declare nameLabel and levelLabel as var variables
      var nameLabel: Label = _
      var levelLabel: Label = _

      // Create a combo box to select the active player
      val playerComboBox = new ComboBox(playerList) {
        renderer = Renderer(_.characterName)
        selection.item = selectedPlayerVar()
      }

      // Add a listener to the combo box that updates the selected player variable and the auto-heal values in the text fields
      playerComboBox.selection.reactions += {
        case SelectionChanged(_) =>
          selectedPlayerVar() = playerComboBox.selection.item
          updateTabs(selectedPlayerVar(), this)
      }

      // Create a label to display the selected player's name and assign to nameLabel
      nameLabel = new Label(s"Character ${selectedPlayerVar().characterName}")

      // Create a label to display the selected player's level and assign to levelLabel
      levelLabel = new Label(s"Level ${selectedPlayerVar().charLevel}")

      // Add the label, combo box, and button to the panel
      contents += playerComboBox
      contents += nameLabel
      contents += levelLabel
    }

    def autoHealPanel(playerList: Seq[Player]) = new BoxPanel(Orientation.Vertical) {

      val playerComboBox = new ComboBox(playerList) {
        renderer = Renderer(_.characterName)
        selection.item = selectedPlayerVar()
      }

      val nameLabel = new Label(s"Character ${selectedPlayerVar().characterName}")
      val levelLabel = new Label(s"Level ${selectedPlayerVar().charLevel}")
      val manaStatusLabel = new Label(s"Mana status: ${selectedPlayerVar().manaPoints}")
      val spellLabel = new Label(s"Spell: ")
      val autoHealSpellField = new TextField(selectedPlayerVar().botLightHealSpell)
      autoHealSpellField.columns = 10
      val healthLabel = new Label("Health: ")
      val autoHealHealthField = new TextField(selectedPlayerVar().botLightHealHealth.toString)
      autoHealHealthField.columns = 5
      val manaLabel = new Label("Mana: ")
      val autoHealManaField = new TextField(selectedPlayerVar().botLightHealMana.toString)
      autoHealManaField.columns = 5

      contents += nameLabel
      contents += levelLabel
      contents += manaStatusLabel
      contents += spellLabel
      contents += autoHealSpellField
      contents += healthLabel
      contents += autoHealHealthField
      contents += manaLabel
      contents += autoHealManaField
    }

  }
}



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