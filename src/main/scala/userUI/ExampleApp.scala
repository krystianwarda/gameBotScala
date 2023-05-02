package userUI

//import monix.reactive.subjects.Var
//import monix.reactive.subjects.Var
import monix.reactive.subjects.Var
import player.{Example, Player}
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

import scala.swing._
import scala.swing.event._

import scala.swing._
import scala.swing.event._
import scala.swing._
import scala.swing.event._

import scala.swing._
import scala.swing.event._


case class ExampleApp(examples: List[player.Example]) extends MainFrame {
  title = "Example App"
  preferredSize = new Dimension(400, 300)

  def updateExample(): Unit = {
    val selectedName = exampleDropdown.selection.item
    val selectedExample = exampleMap(selectedName)
    exampleLabel.text = s"Name: ${selectedExample.name}, ID: ${selectedExample.id}"
    intField1.text = selectedExample.randomVar1.toString
    intField2.text = selectedExample.randomVar2.toString
  }

  def saveExample(): Unit = {
    val selectedName = exampleDropdown.selection.item
    val selectedExample = exampleMap(selectedName)
    val newRandomVar1 = intField1.text.toInt
    val newRandomVar2 = intField2.text.toInt
    selectedExample.updateVar(newRandomVar1, newRandomVar2)
  }

  val exampleNames = examples.map(_.name)
  val exampleMap = examples.map(e => e.name -> e).toMap

  val exampleDropdown = new ComboBox(exampleNames)

  val exampleLabel = new Label()
  val intField1 = new TextField()
  val intField2 = new TextField()

  val updateButton = new Button("Update") {
    reactions += {
      case ButtonClicked(_) =>
        saveExample()
        updateExample()
    }
  }

  contents = new TabbedPane {
    pages += new TabbedPane.Page("Examples", new BoxPanel(Orientation.Vertical) {
      contents += exampleDropdown

      listenTo(exampleDropdown.selection)
      reactions += {
        case SelectionChanged(_) => updateExample()
      }

    })

    pages += new TabbedPane.Page("Details", new BoxPanel(Orientation.Vertical) {
      contents += exampleLabel
      contents += new Label("Random 1:")
      contents += intField1
      contents += new Label("Random 2:")
      contents += intField2
      contents += updateButton
    })
  }

  updateExample()
}