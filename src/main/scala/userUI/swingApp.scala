package userUI

import scala.swing.TabbedPane._
import scala.swing._
import scala.collection.mutable.ArrayBuffer
import java.awt.{Font, GraphicsEnvironment}
import javax.swing.{DefaultDesktopManager, UIManager}

class swingApp extends SimpleSwingApplication {
  def top = new MainFrame {
    title = "My Tabbed Panel"

    val tabs = new TabbedPane {
      pages += new TabbedPane.Page("Activate", activate)
      pages += new TabbedPane.Page("Auto Healing", autoHealing) {
        peer.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10))
      }


      pages += new TabbedPane.Page("Cavebot", caveBot)
    }
    contents = tabs
    font = new Font("Helvetica", Font.PLAIN, 8)
    size = new Dimension(600, 400)

    def autoHealing = new BoxPanel(Orientation.Vertical) {
      val rows = ArrayBuffer[ArrayBuffer[Component]]()

      val row1 = rowAutoHealing("Strong spell", "", "Health: ", "", "Mana: ", "")
      val row2 = rowAutoHealing("Light spell:", "", "Health: ", "", "Mana: ", "")
      val row3 = rowAutoHealing("UH Rune:", "", "Health: ", "", "Mana: ", "")
      val row4 = rowAutoHealing("IH Rune:", "", "Health: ", "", "Mana: ", "")
      val row5 = rowAutoHealing("HP Potion:", "", "Health: ", "", "Mana: ", "")
      val row6 = rowAutoHealing("MP Potion:", "", "Health: ", "", "Mana: ", "")

      contents += row1
      contents += row2
      contents += row3
      contents += row4
      contents += row5
      contents += row6

      val storeInputButton = new Button("Store Input") {
        reactions += {
          case event.ButtonClicked(_) => storeInput()
        }
      }

      contents += Swing.VStrut(10)
      contents += storeInputButton

      def storeInput(): Unit = {
        val inputArray = rows.map(row => row.map {
          case tf: TextField => tf.text
          case c: Component => c.toString
        }.toArray).toArray
        val inputClass = new InputClass(inputArray)
        // Do something with the inputClass object
      }
    }

    def activate = new BoxPanel(Orientation.Vertical) {

    }

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