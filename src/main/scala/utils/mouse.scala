package utils

import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.MouseInfo

object mouse {

    val robot = new Robot()

    def leftClick(x: Int, y: Int): Unit = {
    robot.mouseMove(x, y)
    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
    }
  def rightClick(loc: Option[(Int, Int)]): Unit = {
    loc.foreach { case (x, y) =>
      robot.mouseMove(x, y)
      robot.mousePress(InputEvent.BUTTON3_DOWN_MASK)
      robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK)
    }
  }

//    def rightClick(x: Int, y: Int): Unit = {
//      robot.mouseMove(x, y)
//      robot.mousePress(InputEvent.BUTTON3_DOWN_MASK)
//      robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK)
//    }

    def mouseMove(loc: Option[(Int, Int)]): Unit = {
      val (x, y) = loc.get
      robot.mouseMove(x, y)
    }

    def mouseMoveSmooth(loc: Option[(Int, Int)]): Unit = {
      loc.foreach { case (x, y) =>
        val currentLoc = MouseInfo.getPointerInfo.getLocation
        val xStep = (x - currentLoc.getX).toInt / 10
        val yStep = (y - currentLoc.getY).toInt / 10
        for (i <- 1 to 10) {
          robot.mouseMove(currentLoc.getX.toInt + i * xStep, currentLoc.getY.toInt + i * yStep)
          Thread.sleep(10)
        }
      }
    }


  def mouseScroll(scrolls: Int): Unit = {
    robot.mouseWheel(scrolls)
    }

//    def mouseDragFour(x1: Int, y1: Int, x2: Int, y2: Int): Unit = {
//        robot.mouseMove(x1, y1)
//        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
//        Thread.sleep(300)
//        robot.mouseMove(x2, y2)
//        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
//    }

    def mouseDrag(loc1: Option[(Int, Int)], loc2: Option[(Int, Int)]): Unit = {
        if (loc1.isDefined && loc2.isDefined) {
            val (x1, y1) = loc1.get
            val (x2, y2) = loc2.get
            robot.mouseMove(x1, y1)
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
            Thread.sleep(600)
            robot.mouseMove(x2, y2)
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
        } else {
            println("Cannot drag mouse. One or both locations not found.")
        }
    }

  def mouseDragSmooth(loc1: Option[(Int, Int)], loc2: Option[(Int, Int)]): Unit = {
    if (loc1.isDefined && loc2.isDefined) {
      val (x1, y1) = loc1.get
      val (x2, y2) = loc2.get

      // Get current mouse position
      val currentMousePos = MouseInfo.getPointerInfo.getLocation

      // Calculate the delta x and delta y
      val deltaX = x2 - x1
      val deltaY = y2 - y1

      // Calculate the number of steps for the drag
      val steps = 50

      // Calculate the step size for the drag
      val stepSizeX = deltaX / steps
      val stepSizeY = deltaY / steps

      // Perform the drag in steps
      robot.mouseMove(x1, y1)
      robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
      Thread.sleep(600)
      for (i <- 1 to steps) {
        val x = (x1 + stepSizeX * i).toInt
        val y = (y1 + stepSizeY * i).toInt
        robot.mouseMove(x, y)
        Thread.sleep(10)
      }
      robot.mouseMove(x2, y2)
      robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)

      // Move the mouse back to its original position
      robot.mouseMove(currentMousePos.x.toInt, currentMousePos.y.toInt)
    } else {
      println("Cannot drag mouse. One or both locations not found.")
    }
  }


}
