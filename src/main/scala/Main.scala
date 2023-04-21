import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.Source
import scala.jdk.CollectionConverters._
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import org.opencv.core.CvType
import utils.image.loadOpenCVSettings
import utils.image.{makeScreenshot, refreshWindow}
import player.Player
import scala.concurrent.ExecutionContext.Implicits.global
import credentials._
import player.core.checkSkills
import utils.keyboard.{sendText}

object Main {
  def main(args: Array[String]): Unit = {
    loadOpenCVSettings()

    val captureInterval = 1000 // capture interval in milliseconds
//    val cascadeClassifier = new CascadeClassifier("path/to/your/cascade/classifier.xml") // path to your cascade classifier file
//    val scaleFactor = 1.2 // scale factor for object detection
//    val minNeighbors = 3 // minimum number of neighbors for object detection
    makeScreenshot(windowName)
    checkSkills()
    while (true) {
      refreshWindow(windowName)
      if (Player.manaPoints > 300) {
        sendText("adori vis")
        Thread.sleep(2000)
      }



      Thread.sleep(captureInterval)
    }

  }

}
