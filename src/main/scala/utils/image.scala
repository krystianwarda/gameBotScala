package utils

import net.jpountz.xxhash.XXHashFactory

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import java.nio.ByteBuffer
import java.nio.file.{Files, Path, Paths}
import javax.imageio.ImageIO
import scala.collection.mutable.ArrayBuffer
import java.util.ArrayList
import org.bytedeco.javacpp.Loader
import org.opencv.core.{Core, CvType, MatOfByte, MatOfPoint2f, Rect, Scalar, Size}
import org.opencv.core._
import org.opencv.imgproc.Imgproc
import player.Player

import java.nio.file.Files
//import org.bytedeco.javacpp.opencv_core.Mat
import net.sourceforge.tess4j.Tesseract

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, File}
import scala.sys.process.stringSeqToProcess
import java.awt.image.{BufferedImage, DataBufferByte}
import javax.imageio.ImageIO
import java.awt.{Robot, Window}
import org.opencv.imgproc.Imgproc
import org.opencv.core.Core.MinMaxLocResult
import org.opencv.imgcodecs.Imgcodecs

import scala.collection.mutable.ListBuffer
import java.lang.System.setProperty
import java.awt.{Desktop, Robot, Toolkit}
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import java.awt.KeyboardFocusManager
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source
import java.awt.{Rectangle, Robot}
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File
import java.awt.{Dimension, Rectangle, Robot, Toolkit}
import java.io.File
import javax.imageio.ImageIO
import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.Await
import java.util.concurrent.TimeUnit
import java.awt.event.InputEvent.BUTTON1_DOWN_MASK
import java.awt.event.InputEvent.BUTTON3_DOWN_MASK
import java.awt.datatransfer._
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

import java.util
import scala.collection.JavaConverters._
import org.bytedeco.opencv.global.opencv_imgcodecs._
import org.bytedeco.opencv.global.opencv_imgproc._
import org.bytedeco.javacpp._

import java.io.File
import org.apache.commons.io.FilenameUtils
//import org.bytedeco.javacpp.opencv_core._
import org.opencv.core.MatOfByte
import utils.core.maximizeWindow
import utils.mouse.mouseMoveSmooth
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Core.MinMaxLocResult
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
//  import org.opencv.core.{CvType, Mat, Scalar}
//  import org.opencv.imgproc.Imgproc


object image {

  def refreshWindow(windowName: String): Unit = {
    makeScreenshot(windowName)
    val capturedImage = Imgcodecs.imread("window.png")
    val hsvImage = new Mat()
    Imgproc.cvtColor(capturedImage, hsvImage, Imgproc.COLOR_BGR2HSV)

    val outputImageFile = new File("output.png")
    Imgcodecs.imwrite(outputImageFile.getAbsolutePath(), capturedImage)
  }

  def extractRectangle(image: Mat, coordinates: Option[(Option[(Int, Int)], Option[(Int, Int)], Option[(Int, Int)], Option[(Int, Int)])]): Mat = {
    coordinates match {
      case Some((Some((x1, y1)), Some((x2, y2)), Some((x3, y3)), Some((x4, y4)))) =>
        val pts = new MatOfPoint2f(
          new Point(x1, y1),
          new Point(x2, y2),
          new Point(x3, y3),
          new Point(x4, y4)
        )
        val rect = Imgproc.boundingRect(pts)
        new Mat(image, rect)
      case _ =>
        throw new Exception("Invalid coordinates provided")
    }
  }


  def loadOpenCVSettings(): Unit = {
    System.setProperty("java.library.path", "/usr/opencv/build/lib")
    println(System.getProperty("java.library.path"))
    setProperty("java.library.path", "/usr/opencv/build/lib")
    System.load("/usr/opencv/build/lib/libopencv_java455.so")
    Loader.load(classOf[org.bytedeco.opencv.global.opencv_core])
    Loader.load(classOf[org.bytedeco.opencv.global.opencv_imgcodecs])
    Loader.load(classOf[org.bytedeco.opencv.global.opencv_imgproc])
  }

  def mouseOverRectangle(characterClass: Player, rectangleCoords: Option[(Option[(Int, Int)], Option[(Int, Int)], Option[(Int, Int)], Option[(Int, Int)])]): Unit = {
    // call mouseMoveSmooth on each coordinate using foreach
    rectangleCoords.foreach { case (a1, a2, b1, b2) =>
      // call mouseMoveSmooth on each coordinate
      Seq(a1, a2, b1, b2).flatten.foreach { loc =>
        println(Some(loc))
        mouseMoveSmooth(characterClass.getRobot(), Some(loc))
        Thread.sleep(2000) // pause for 2 seconds
      }
    }
  }


  def loadImage(imagePath: String): Mat = {
    Imgcodecs.imread(imagePath)
  }

  def loadImage2(imagePath: String): Mat = {
    val image = Imgcodecs.imread(imagePath)
    val channels = new util.ArrayList[Mat]()
    Core.split(image, channels)

    for (i <- 0 until channels.size()) {
      val channel = channels.get(i)
      Imgproc.threshold(channel, channel, 128, 255, Imgproc.THRESH_BINARY)
    }

    val result = new Mat()
    Core.merge(channels, result)
    result
  }

  def loadImage3(imagePath: String): Mat = {
    val image = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE)
    Imgproc.threshold(image, image, 0, 255, Imgproc.THRESH_BINARY)
    image
  }

  def loadImageHSV(imagePath: String): Mat = {
    // Read the image
    val image = Imgcodecs.imread(imagePath)

    // Convert the image to HSV
    val hsvImage = new Mat()
    Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV)

    // Return the HSV image
    hsvImage
  }

  def cutOutRectangleImage(mainImage: Mat, point: Option[(Int, Int)], rectWidth: Int, rectHeight: Int): Mat = {
    point.foreach { case (x, y) =>
      // Calculate the top left corner point of the rectangle
      val topLeftPoint = new Point(x - rectWidth / 2, y - rectHeight / 2)

      // Create a new Mat object for the cut-out rectangle
      val rectImage = new Mat(mainImage, new Rect(topLeftPoint, new Size(rectWidth, rectHeight)))

      // Save the cut-out rectangle to a PNG file
      Imgcodecs.imwrite("rectangle.png", rectImage)

      // Return the cut-out rectangle Mat object
      return rectImage
    }

    // If point is None, return an empty Mat object
    new Mat()
  }

  def cutOutSquareImage(mainImage: Mat, point: Option[(Int, Int)], squareSideLength: Int): Mat = {
    point.foreach { case (x, y) =>
      // Calculate the top left corner point of the square
      val topLeftPoint = new Point(x - squareSideLength/2, y - squareSideLength/2)

      // Create a new Mat object for the cut-out square
      val squareImage = new Mat(mainImage, new Rect(topLeftPoint, new Size(squareSideLength, squareSideLength)))

      // Save the cut-out square to a PNG file
      Imgcodecs.imwrite("square.png", squareImage)

      // Return the cut-out square Mat object
      return squareImage
    }

    // If point is None, return an empty Mat object
    new Mat()
  }
  def getLocationFromImageMidLeft(tempImage: Mat, mainImage: Mat): Option[(Int, Int)] = {
    // Convert images to HSV format
    val tempImageHSV = new Mat()
    val mainImageHSV = new Mat()
    Imgproc.cvtColor(tempImage, tempImageHSV, Imgproc.COLOR_BGR2HSV)
    Imgproc.cvtColor(mainImage, mainImageHSV, Imgproc.COLOR_BGR2HSV)

    // Create the result matrix
    val result = Mat.zeros(mainImageHSV.rows - tempImageHSV.rows + 1, mainImageHSV.cols - tempImageHSV.cols + 1, CvType.CV_32FC1)

    // Match the images using template matching
    Imgproc.matchTemplate(mainImageHSV, tempImageHSV, result, Imgproc.TM_CCOEFF)

    // Find the best match location
    val minMaxLoc = Core.minMaxLoc(result)

    // Check if the match is good enough
    if (minMaxLoc.maxVal > 0.80) {
      // Return the location of the center of the left side of the matched area
      val centerX = minMaxLoc.maxLoc.x.toInt
      val centerY = (minMaxLoc.maxLoc.y + tempImageHSV.rows / 2).toInt
      Some((centerX, centerY))
    } else {
      println("Not found")
      None
    }
  }
  def getSideFromFilename(imageString: String): String = {
    if (imageString.contains("bottom")) {
      "bottom"
    } else if (imageString.contains("left")) {
      "left"
    } else if (imageString.contains("top")) {
      "top"
    } else if (imageString.contains("right")) {
      "right"
    } else {
      "Unknown side"
    }
  }

  def getCenterPoint(locations: List[Option[(Int, Int)]]): Option[(Int, Int)] = {
    val validLocations = locations.flatten
    if (validLocations.size == 4) {
      val bottom = validLocations(0)._2
      val left = validLocations(1)._1
      val top = validLocations(2)._2
      val right = validLocations(3)._1
      val centerX = (left + right) / 2
      val centerY = (top + bottom) / 2
      Some((centerX, centerY))
    } else {
      None
    }
  }

  def getCenterLoc(mainImage: Mat): Option[(Int, Int)] = {
    val stringList = List("bottomGameWindow", "leftGameWindow", "topGameWindow", "rightGameWindow")
    var locations = ListBuffer.empty[Option[(Int, Int)]]
    for (str <- stringList) {
      val tempImage = loadImage(s"images/screenInfo/$str.png")
      val tempLoc = getLocationFromImageMidMatchTemp(tempImage, mainImage)
      locations ++= tempLoc.map(Some(_))
//      println(str)
      Thread.sleep(1000)
//      mouseMoveSmooth(tempLoc)
    }
    getCenterPoint(locations.toList)
  }


  def getLocationFromImage(tempImage: Mat, mainImage: Mat, imageSide: String): Option[(Int, Int)] = {
    // Convert images to HSV format
    val tempImageHSV = new Mat()
    val mainImageHSV = new Mat()
    Imgproc.cvtColor(tempImage, tempImageHSV, Imgproc.COLOR_BGR2HSV)
    Imgproc.cvtColor(mainImage, mainImageHSV, Imgproc.COLOR_BGR2HSV)

    // Create the result matrix
    val result = Mat.zeros(mainImageHSV.rows - tempImageHSV.rows + 1, mainImageHSV.cols - tempImageHSV.cols + 1, CvType.CV_32FC1)

    // Match the images using template matching
    Imgproc.matchTemplate(mainImageHSV, tempImageHSV, result, Imgproc.TM_CCOEFF)

    // Find the best match location
    val minMaxLoc = Core.minMaxLoc(result)

    // Check if the match is good enough
    if (minMaxLoc.maxVal > 0.85) {
      // Return the location of the center of the matched area or one of the side locations
      val centerX = (minMaxLoc.maxLoc.x + tempImageHSV.cols / 2).toInt
      val centerY = (minMaxLoc.maxLoc.y + tempImageHSV.rows / 2).toInt
      val (width, height) = (tempImageHSV.width(), tempImageHSV.height())
      imageSide match {
        case "bottom" => Some((centerX, minMaxLoc.maxLoc.y.toInt + height))
        case "left" => Some((minMaxLoc.maxLoc.x.toInt, centerY.toInt))
        case "top" => Some((centerX, minMaxLoc.maxLoc.y.toInt - height))
        case "right" => Some((minMaxLoc.maxLoc.x.toInt + width, centerY.toInt))
        case "middle" => Some((centerX, centerY))
        case _ => None
      }
    } else {
      println("Not found")
      None
    }
  }

  def convertImageToArray(image: Mat): Array[Array[Int]] = {
    val width = image.cols
    val height = image.rows
    val arr = Array.ofDim[Int](height, width)
    for (y <- 0 until height) {
      for (x <- 0 until width) {
        val rgb = image.get(y, x).head.toInt & 0xFFFFFF // get the BGR values and mask out the alpha channel
        arr(y)(x) = rgb
      }
    }
    arr
  }

//  def getLocationFromImage(tempImage: Mat, mainImage: Mat, imageSide: String): Option[(Int, Int)] = {
//    // Convert images to HSV format
//    val tempImageHSV = new Mat()
//    val mainImageHSV = new Mat()
//    Imgproc.cvtColor(tempImage, tempImageHSV, Imgproc.COLOR_BGR2HSV)
//    Imgproc.cvtColor(mainImage, mainImageHSV, Imgproc.COLOR_BGR2HSV)
//
//    // Create the result matrix
//    val result = Mat.zeros(mainImageHSV.rows - tempImageHSV.rows + 1, mainImageHSV.cols - tempImageHSV.cols + 1, CvType.CV_32FC1)
//
//    // Match the images using template matching
//    Imgproc.matchTemplate(mainImageHSV, tempImageHSV, result, Imgproc.TM_CCOEFF)
//
//    // Find the best match location
//    val minMaxLoc = Core.minMaxLoc(result)
//
//    // Check if the match is good enough
//    if (minMaxLoc.maxVal > 0.80) {
//      // Return the location of the center of the matched area or one of the side locations
//      val centerX = (minMaxLoc.maxLoc.x + tempImageHSV.cols / 2).toInt
//      val centerY = (minMaxLoc.maxLoc.y + tempImageHSV.rows / 2).toInt
//      val (width, height) = (tempImageHSV.width(), tempImageHSV.height())
//      imageSide match {
//        case "bottom" => Some((centerX, minMaxLoc.maxLoc.y.toInt + height))
//        case "left" => Some((minMaxLoc.maxLoc.x.toInt, centerY.toInt))
//        case "top" => Some((centerX, minMaxLoc.maxLoc.y.toInt))
//        case "right" => Some((minMaxLoc.maxLoc.x.toInt + width, centerY))
//        case "middle" => Some((centerX, centerY))
//        case _ => None
//      }
//    } else {
//      println("Not found")
//      None
//    }
//  }



  def getLocationFromImageRight(tempImage: Mat, mainImage: Mat): Option[(Int, Int)] = {
    // Convert images to HSV format
    val tempImageHSV = new Mat()
    val mainImageHSV = new Mat()
    Imgproc.cvtColor(tempImage, tempImageHSV, Imgproc.COLOR_BGR2HSV)
    Imgproc.cvtColor(mainImage, mainImageHSV, Imgproc.COLOR_BGR2HSV)

    // Create the result matrix
    val result = Mat.zeros(mainImageHSV.rows - tempImageHSV.rows + 1, mainImageHSV.cols - tempImageHSV.cols + 1, CvType.CV_32FC1)

    // Match the images using template matching
    Imgproc.matchTemplate(mainImageHSV, tempImageHSV, result, Imgproc.TM_CCOEFF)

    // Find the best match location
    val minMaxLoc = Core.minMaxLoc(result)

    // Check if the match is good enough
    if (minMaxLoc.maxVal > 0.80) {
      // Return the location of the point on the right side of the matched area
      val rightX = (minMaxLoc.maxLoc.x + tempImageHSV.cols).toInt
      val centerY = (minMaxLoc.maxLoc.y + tempImageHSV.rows / 2).toInt
      Some((rightX, centerY))
    } else {
      println("Not found")
      None
    }
  }
  def getLocationFromImageMidGrey(tempImage: Mat, mainImage: Mat): Option[(Int, Int)] = {
    // Create the result matrix
    val result = new Mat()
    Imgproc.matchTemplate(mainImage, tempImage, result, Imgproc.TM_CCOEFF_NORMED)

    // Threshold the result to eliminate weak matches
    val threshold = 0.99
    val thresholded = new Mat()
    Core.compare(result, new Scalar(threshold), thresholded, Core.CMP_GT)

    // Find the best match location
    val minMaxLoc = Core.minMaxLoc(result)

    // Check if the match is good enough
    if (minMaxLoc.maxVal > threshold) {
      // Return the location of the center of the matched area
      val centerX = (minMaxLoc.maxLoc.x + tempImage.cols / 2).toInt
      val centerY = (minMaxLoc.maxLoc.y + tempImage.rows / 2).toInt
      Some((centerX, centerY))
    } else {
      println("Not found")
      None
    }
  }

  def getLocationFromImageMidMatchTempLowConf(tempImage: Mat, mainImage: Mat): Option[(Int, Int)] = {
    // Create the result matrix
    val result = new Mat()
    val matchMethod = Imgproc.TM_CCOEFF_NORMED
    Imgproc.matchTemplate(mainImage, tempImage, result, matchMethod)

    // Find the best match location
    val minMaxLoc = Core.minMaxLoc(result)

    // Check if the match is good enough
    if (minMaxLoc.maxVal > 0.5) {
      // Return the location of the center of the matched area
      val centerX = (minMaxLoc.maxLoc.x + tempImage.cols / 2).toInt
      val centerY = (minMaxLoc.maxLoc.y + tempImage.rows / 2).toInt
      Some((centerX, centerY))
    } else {
      println("Not found")
      None
    }
  }


  def getLocationFromImageHashMidMatchLowConf(tempImageBytes: Array[Byte], mainImage: Mat): Option[(Int, Int)] = {
    // Convert the byte array to a Mat object
    val tempImage = {
      val inputStream = new ByteArrayInputStream(tempImageBytes)
      val bufferedImage = ImageIO.read(inputStream)
      val mat = new Mat(bufferedImage.getHeight, bufferedImage.getWidth, CvType.CV_8UC3)
      val data = bufferedImage.getData.getPixels(0, 0, bufferedImage.getWidth, bufferedImage.getHeight, new Array[Int](bufferedImage.getWidth * bufferedImage.getHeight * 3))
      mat.put(0, 0, data)
      mat
    }

    // Create the result matrix
    val result = new Mat()
    val matchMethod = Imgproc.TM_CCOEFF_NORMED
    Imgproc.matchTemplate(mainImage, tempImage, result, matchMethod)

    // Find the best match location
    val minMaxLoc = Core.minMaxLoc(result)

    // Check if the match is good enough
    if (minMaxLoc.maxVal > 0.8) {
      // Return the location of the center of the matched area
      val centerX = (minMaxLoc.maxLoc.x + tempImage.cols / 2).toInt
      val centerY = (minMaxLoc.maxLoc.y + tempImage.rows / 2).toInt
      Some((centerX, centerY))
    } else {
      println("Not found")
      None
    }
  }

  def hashit(mat: Mat): Array[Byte] = {
    val rows = mat.rows()
    val cols = mat.cols()
    val channels = mat.channels()
    val dataType = mat.`type`()
    val pixelCount = rows * cols
    val buffer = ByteBuffer.allocate(pixelCount * channels * CvType.ELEM_SIZE(dataType))

    for (row <- 0 until rows) {
      for (col <- 0 until cols) {
        val pixel = new Array[Float](channels)
        mat.get(row, col, pixel)
        for (channel <- 0 until channels) {
          dataType match {
            case CvType.CV_32FC1 =>
              buffer.putFloat(pixel(channel))
            case CvType.CV_8UC1 =>
              buffer.put(pixel(channel).toByte)
            case CvType.CV_16UC1 =>
              buffer.putShort(pixel(channel).toShort)
            case _ =>
              throw new UnsupportedOperationException(s"Unsupported data type: $dataType")
          }
        }
      }
    }

    buffer.array()
  }


  def fromBytes(bytes: Array[Byte]): Mat = {
    val hashBytes = bytes.slice(0, 4)
    val dimensions = bytes.slice(4, 8)
    val rows = dimensions(0) & 0xff
    val cols = dimensions(1) & 0xff
    val channels = dimensions(2) & 0xff
    val dataType = dimensions(3) & 0xff
    val dataBytes = bytes.slice(8, bytes.length)
    val mat = new Mat(rows, cols, dataType)
    val buffer = ByteBuffer.wrap(dataBytes)

    for (row <- 0 until rows) {
      for (col <- 0 until cols) {
        val pixel = new Array[Float](channels)
        dataType match {
          case CvType.CV_32FC1 =>
            for (channel <- 0 until channels) {
              pixel(channel) = buffer.getFloat()
            }
          case CvType.CV_8UC1 =>
            for (channel <- 0 until channels) {
              pixel(channel) = (buffer.get() & 0xff).toFloat
            }
          case _ =>
            throw new UnsupportedOperationException(s"Unsupported data type: $dataType")
        }
        mat.put(row, col, pixel)
      }
    }
    mat
  }

//  def saveMatToDatFile(mat: Mat, filename: String): Unit = {
//    val buffer = new Array[Byte](mat.total() * mat.elemSize().toInt)
//    mat.get(0, 0, buffer)
//    val stream = new FileOutputStream(new File(filename))
//    val objStream = new ObjectOutputStream(stream)
//    objStream.writeObject(buffer)
//    objStream.close()
//    stream.close()
//  }
//  def loadMatFromDatFile(filename: String, rows: Int, cols: Int, `type`: Int): Mat = {
//    val stream = new FileInputStream(new File(filename))
//    val objStream = new ObjectInputStream(stream)
//    val buffer = objStream.readObject().asInstanceOf[Array[Byte]]
//    objStream.close()
//    stream.close()
//    val mat = new Mat(rows, cols, `type`)
//    mat.put(0, 0, buffer)
//    mat
//  }

  def hashMat(mat: Mat): (Int, Int, String) = {
    val seed: Int = 20220605
    val byteBuffer: ByteBuffer = ByteBuffer.allocate(mat.total.toInt * mat.elemSize.toInt)
    mat.get(0, 0, byteBuffer.array())
    val xxHashFactory: XXHashFactory = XXHashFactory.fastestInstance()
    val xxHash: net.jpountz.xxhash.XXHash32 = xxHashFactory.hash32()
    val hashValue: Int = xxHash.hash(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining(), seed)
    val hashString: String = Integer.toHexString(hashValue)
    (seed, hashValue, hashString)
  }

  def hashitHex(arr: Array[Int]): (Int, Int, String) = {
    val seed: Int = 20220605
    val byteBuffer: ByteBuffer = ByteBuffer.allocate(arr.length * 4)
    byteBuffer.asIntBuffer.put(arr)
    val xxHashFactory: XXHashFactory = XXHashFactory.fastestInstance()
    val xxHash: net.jpountz.xxhash.XXHash32 = xxHashFactory.hash32()
    val hashValue: Int = xxHash.hash(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining(), seed)
    val hashString: String = Integer.toHexString(hashValue)
    (seed, hashValue, hashString)
  }

  def matToArray(mat: Mat): Array[Array[Int]] = {
    val width = mat.cols()
    val height = mat.rows()
    val channels = mat.channels()
    val pixels = Array.ofDim[Int](height, width)

    // Convert image to BGR color space if necessary
    val bgrMat = if (channels == 4) {
      val bgraMat = new Mat()
      Imgproc.cvtColor(mat, bgraMat, Imgproc.COLOR_BGRA2BGR)
      bgraMat
    } else if (channels == 1) {
      val grayMat = new Mat()
      Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_GRAY2BGR)
      grayMat
    } else {
      mat
    }

    for (y <- 0 until height) {
      for (x <- 0 until width) {
        channels match {
          case 1 => pixels(y)(x) = bgrMat.get(y, x)(0).toInt
          case 3 => {
            val bgr = bgrMat.get(y, x)
            val b = bgr(0).toInt & 0xFF
            val g = bgr(1).toInt & 0xFF
            val r = bgr(2).toInt & 0xFF
            val pixelValue = (r << 16) | (g << 8) | b
            pixels(y)(x) = pixelValue
          }
          case _ => throw new UnsupportedOperationException("Unsupported number of channels")
        }
      }
    }

    // Release the intermediate matrix if it was created
    if (bgrMat != mat) {
      bgrMat.release()
    }

    pixels
  }

  def matToImage(mat: Mat): BufferedImage = {
    val bytes = new Array[Byte](mat.total.toInt * mat.channels)
    mat.get(0, 0, bytes)
    val bufferedImage = new BufferedImage(mat.width, mat.height, BufferedImage.TYPE_3BYTE_BGR)
    bufferedImage.getRaster.setDataElements(0, 0, mat.width, mat.height, bytes)
    bufferedImage
  }


  // Method to convert a Mat to a BufferedImage
  def matToBufferedImage(mat: Mat): BufferedImage = {
    val img = new BufferedImage(mat.width, mat.height, BufferedImage.TYPE_3BYTE_BGR)
    val buf = img.getRaster.getDataBuffer.asInstanceOf[DataBufferByte]
    mat.get(0, 0, buf.getData)
    img
  }
  def arrayToMat(pixels: Array[Array[Int]]): Mat = {
    val height = pixels.length
    val width = pixels(0).length
    val mat = new Mat(height, width, CvType.CV_8UC3)
    for (y <- 0 until height) {
      for (x <- 0 until width) {
        val rgb = pixels(y)(x)
        val r = (rgb >> 16) & 0xFF
        val g = (rgb >> 8) & 0xFF
        val b = rgb & 0xFF
        mat.put(y, x, Array(b.toByte, g.toByte, r.toByte))
      }
    }
    mat
  }


        //  def matToArray(mat: Mat): Array[Array[Int]] = {
//    val width = mat.cols()
//    val height = mat.rows()
//    val channels = mat.channels()
//    val pixels = new Array[Array[Int]](height)
//    for (y <- 0 until height) {
//      pixels(y) = new Array[Int](width)
//      for (x <- 0 until width) {
//        channels match {
//          case 1 => pixels(y)(x) = mat.get(y, x)(0).toInt
//          case 3 => {
//            val bgr = mat.get(y, x)
//            val b = bgr(0).toInt & 0xFF
//            val g = bgr(1).toInt & 0xFF
//            val r = bgr(2).toInt & 0xFF
//            val pixelValue = (b << 16) | (g << 8) | r
//            pixels(y)(x) = pixelValue
//          }
//          case _ => throw new UnsupportedOperationException("Unsupported number of channels")
//        }
//      }
//    }
//    pixels
//  }
//
//  def arrayToMat(pixels: Array[Array[Int]]): Mat = {
//    val height = pixels.length
//    val width = pixels(0).length
//    val mat = new Mat(height, width, CvType.CV_8UC3)
//    for (y <- 0 until height) {
//      for (x <- 0 until width) {
//        val rgb = pixels(y)(x)
//        val r = (rgb >> 16) & 0xFF
//        val g = (rgb >> 8) & 0xFF
//        val b = rgb & 0xFF
//        mat.put(y, x, Array(b.toByte, g.toByte, r.toByte))
//      }
//    }
//    mat
//  }


//
//  def matToArray(mat: Mat): Array[Array[Int]] = {
//    val width = mat.cols()
//    val height = mat.rows()
//    val channels = mat.channels()
//    val pixels = new Array[Array[Int]](height)
//    for (y <- 0 until height) {
//      pixels(y) = new Array[Int](width)
//      for (x <- 0 until width) {
//        channels match {
//          case 1 => pixels(y)(x) = mat.get(y, x)(0).toInt
//          case 3 => {
//            val bgr = mat.get(y, x)
//            val b = bgr(0).toInt & 0xFF
//            val g = bgr(1).toInt & 0xFF
//            val r = bgr(2).toInt & 0xFF
//            val pixelValue = (r << 16) | (g << 8) | b
//            pixels(y)(x) = pixelValue
//          }
//          case _ => throw new UnsupportedOperationException("Unsupported number of channels")
//        }
//      }
//    }
//    pixels
//  }
//
//  def arrayToMat(pixels: Array[Array[Int]]): Mat = {
//    val height = pixels.length
//    val width = pixels(0).length
//    val mat = new Mat(height, width, CvType.CV_8UC3)
//    for (y <- 0 until height) {
//      for (x <- 0 until width) {
//        val rgb = pixels(y)(x)
//        val b = (rgb >> 16) & 0xFF
//        val g = (rgb >> 8) & 0xFF
//        val r = rgb & 0xFF
//        mat.put(y, x, Array(b.toByte, g.toByte, r.toByte))
//      }
//    }
//    mat
//  }

  def saveMatToFile(mat: Mat, filename: String): Boolean = {
    Imgcodecs.imwrite(filename + ".png", mat)
  }


//  def matToArray(mat: Mat): Array[Array[Double]] = {
//    val rows = mat.rows
//    val cols = mat.cols
//    val arr = Array.ofDim[Double](rows, cols)
//    for (i <- 0 until rows; j <- 0 until cols) {
//      arr(i)(j) = mat.get(i, j)(0)
//    }
//    arr
//  }

//  def arrayToMat(arr: Array[Array[Double]]): Mat = {
//    val rows = arr.length
//    val cols = arr.head.length
//    val mat = new Mat(rows, cols, CvType.CV_64FC1)
//    for (i <- 0 until rows; j <- 0 until cols) {
//      mat.put(i, j, arr(i)(j))
//    }
//    mat
//  }


  def getLocationFromImageMidMatchTemp(tempImage: Mat, mainImage: Mat): Option[(Int, Int)] = {
    // Create the result matrix
    val result = new Mat()
    val matchMethod = Imgproc.TM_CCOEFF_NORMED
    Imgproc.matchTemplate(mainImage, tempImage, result, matchMethod)

    // Find the best match location
    val minMaxLoc = Core.minMaxLoc(result)

    // Check if the match is good enough
    if (minMaxLoc.maxVal > 0.99) {
      // Return the location of the center of the matched area
      val centerX = (minMaxLoc.maxLoc.x + tempImage.cols / 2).toInt
      val centerY = (minMaxLoc.maxLoc.y + tempImage.rows / 2).toInt
      Some((centerX, centerY))
    } else {
      println("Not found")
      None
    }
  }

  def getLocationFromImageMidEdgeDetectLowConf(tempImage: Mat, mainImage: Mat): Option[(Int, Int)] = {
    // Convert images to grayscale
    val tempImageGray = new Mat()
    val mainImageGray = new Mat()
    Imgproc.cvtColor(tempImage, tempImageGray, Imgproc.COLOR_BGR2GRAY)
    Imgproc.cvtColor(mainImage, mainImageGray, Imgproc.COLOR_BGR2GRAY)

    // Detect edges
    val tempImageEdges = new Mat()
    val mainImageEdges = new Mat()
    val threshold1 = 50
    val threshold2 = 200
    val apertureSize = 3
    Imgproc.Canny(tempImageGray, tempImageEdges, threshold1, threshold2, apertureSize, false)
    Imgproc.Canny(mainImageGray, mainImageEdges, threshold1, threshold2, apertureSize, false)

    // Create the result matrix
    val result = new Mat()
    Imgproc.matchTemplate(mainImageEdges, tempImageEdges, result, Imgproc.TM_CCOEFF_NORMED)

    // Find the best match location
    val minMaxLoc = Core.minMaxLoc(result)

    // Check if the match is good enough
    if (minMaxLoc.maxVal > 0.70) {
      // Return the location of the center of the matched area
      val centerX = (minMaxLoc.maxLoc.x + tempImage.cols / 2).toInt
      val centerY = (minMaxLoc.maxLoc.y + tempImage.rows / 2).toInt
      Some((centerX, centerY))
    } else {
      println("Not found")
      None
    }
  }

  def getLocationFromImageMidEdgeDetect(tempImage: Mat, mainImage: Mat): Option[(Int, Int)] = {
    // Convert images to grayscale
    val tempImageGray = new Mat()
    val mainImageGray = new Mat()
    Imgproc.cvtColor(tempImage, tempImageGray, Imgproc.COLOR_BGR2GRAY)
    Imgproc.cvtColor(mainImage, mainImageGray, Imgproc.COLOR_BGR2GRAY)

    // Detect edges
    val tempImageEdges = new Mat()
    val mainImageEdges = new Mat()
    val threshold1 = 50
    val threshold2 = 200
    val apertureSize = 3
    Imgproc.Canny(tempImageGray, tempImageEdges, threshold1, threshold2, apertureSize, false)
    Imgproc.Canny(mainImageGray, mainImageEdges, threshold1, threshold2, apertureSize, false)

    // Create the result matrix
    val result = new Mat()
    Imgproc.matchTemplate(mainImageEdges, tempImageEdges, result, Imgproc.TM_CCOEFF_NORMED)

    // Find the best match location
    val minMaxLoc = Core.minMaxLoc(result)

    // Check if the match is good enough
    if (minMaxLoc.maxVal > 0.99) {
      // Return the location of the center of the matched area
      val centerX = (minMaxLoc.maxLoc.x + tempImage.cols / 2).toInt
      val centerY = (minMaxLoc.maxLoc.y + tempImage.rows / 2).toInt
      Some((centerX, centerY))
    } else {
      println("Not found")
      None
    }
  }


  def getLocationFromImageMid(tempImage: Mat, mainImage: Mat): Option[(Int, Int)] = {
    // Convert images to HSV format
    val tempImageHSV = new Mat()
    val mainImageHSV = new Mat()
    Imgproc.cvtColor(tempImage, tempImageHSV, Imgproc.COLOR_BGR2HSV)
    Imgproc.cvtColor(mainImage, mainImageHSV, Imgproc.COLOR_BGR2HSV)

    // Create the result matrix
    val result = Mat.zeros(mainImageHSV.rows - tempImageHSV.rows + 1, mainImageHSV.cols - tempImageHSV.cols + 1, CvType.CV_32FC1)

    // Match the images using template matching
    Imgproc.matchTemplate(mainImageHSV, tempImageHSV, result, Imgproc.TM_CCOEFF)

    // Find the best match location
    val minMaxLoc = Core.minMaxLoc(result)

    // Check if the match is good enough
    if (minMaxLoc.maxVal > 0.80) {
      // Return the location of the center of the matched area
      val centerX = (minMaxLoc.maxLoc.x + tempImageHSV.cols / 2).toInt
      val centerY = (minMaxLoc.maxLoc.y + tempImageHSV.rows / 2).toInt
      Some((centerX, centerY))
    } else {
      println("Not found")
      None
    }
  }
  def getLocationFromImagePoint(tempImage: Mat, mainImage: Mat, edgePoint: Option[(Int, Int)], pointSide: String): Option[(Int, Int)] = {
    // Convert images to HSV format
    val tempImageHSV = new Mat()
    val mainImageHSV = new Mat()
    Imgproc.cvtColor(tempImage, tempImageHSV, Imgproc.COLOR_BGR2HSV)
    Imgproc.cvtColor(mainImage, mainImageHSV, Imgproc.COLOR_BGR2HSV)

    // Set the search area based on the edge point and the side
    val searchArea = pointSide match {
      case "left" => new Rect(0, 0, edgePoint.map(_._1).getOrElse(0), mainImageHSV.rows)
      case "right" => new Rect(edgePoint.map(_._1).getOrElse(mainImageHSV.cols), 0, mainImageHSV.cols - edgePoint.map(_._1).getOrElse(mainImageHSV.cols), mainImageHSV.rows)
      case "top" => new Rect(0, 0, mainImageHSV.cols, edgePoint.map(_._2).getOrElse(0))
      case "bottom" => new Rect(0, edgePoint.map(_._2).getOrElse(mainImageHSV.rows), mainImageHSV.cols, mainImageHSV.rows - edgePoint.map(_._2).getOrElse(mainImageHSV.rows))
      case _ => new Rect(0, 0, mainImageHSV.cols, mainImageHSV.rows)
    }

    // Create the result matrix
    val result = Mat.zeros(searchArea.height - tempImageHSV.rows + 1, searchArea.width - tempImageHSV.cols + 1, CvType.CV_32FC1)

    // Match the images using template matching
    val searchRegion = new Mat(mainImageHSV, searchArea)
    Imgproc.matchTemplate(searchRegion, tempImageHSV, result, Imgproc.TM_CCOEFF)

    // Find the best match location
    val minMaxLoc = Core.minMaxLoc(result)

    // Check if the match is good enough
    if (minMaxLoc.maxVal > 0.99) {
      // Return the location of the center of the matched area
      val centerX = (minMaxLoc.maxLoc.x + tempImageHSV.cols / 2 + searchArea.x).toInt
      val centerY = (minMaxLoc.maxLoc.y + tempImageHSV.rows / 2 + searchArea.y).toInt
      Some((centerX, centerY))
    } else {
      println("Not found")
      None
    }
  }

  def getLocationFromCroppedImage(tempImage: Mat, mainImage: Mat, equipmentArea: Option[Rect]): Option[(Int, Int)] = {
    // Convert images to HSV format
    val tempImageHSV = new Mat()
    val mainImageHSV = new Mat()
    Imgproc.cvtColor(tempImage, tempImageHSV, Imgproc.COLOR_BGR2HSV)
    equipmentArea.foreach(rect => Imgproc.cvtColor(new Mat(mainImage, rect), mainImageHSV, Imgproc.COLOR_BGR2HSV))

    // Create the result matrix
    val result = Mat.zeros(mainImageHSV.rows - tempImageHSV.rows + 1, mainImageHSV.cols - tempImageHSV.cols + 1, CvType.CV_32FC1)

    // Match the images using template matching
    Imgproc.matchTemplate(mainImageHSV, tempImageHSV, result, Imgproc.TM_CCOEFF)

    // Find the best match location
    val minMaxLoc = Core.minMaxLoc(result)

    // Check if the match is good enough
    if (minMaxLoc.maxVal > 0.95) {
      // Return the location of the center of the matched area
      val rectX = equipmentArea.map(_.x).getOrElse(0)
      val rectY = equipmentArea.map(_.y).getOrElse(0)
      val centerX = (minMaxLoc.maxLoc.x + tempImageHSV.cols / 2 + rectX).toInt
      val centerY = (minMaxLoc.maxLoc.y + tempImageHSV.rows / 2 + rectY).toInt
      Some((centerX, centerY))
    } else {
      println("Not found")
      None
    }
  }



  //  def getLocationFromCroppedImage(tempImage: Mat, mainImage:Mat, equipmentArea: Option[Rect]): Option[(Int, Int)] = {
//    // Convert images to HSV format
//    val tempImageHSV = new Mat()
//    val mainImageHSV = new Mat()
//    Imgproc.cvtColor(tempImage, tempImageHSV, Imgproc.COLOR_BGR2HSV)
//    equipmentArea.foreach(rect => Imgproc.cvtColor(new Mat(mainImage, rect), mainImageHSV, Imgproc.COLOR_BGR2HSV))
//
//    // Create the result matrix
//    val result = Mat.zeros(mainImageHSV.rows - tempImageHSV.rows + 1, mainImageHSV.cols - tempImageHSV.cols + 1, CvType.CV_32FC1)
//
//    // Match the images using template matching
//    Imgproc.matchTemplate(mainImageHSV, tempImageHSV, result, Imgproc.TM_CCOEFF)
//
//    // Find the best match location
//    val minMaxLoc = Core.minMaxLoc(result)
//
//    // Check if the match is good enough
//    if (minMaxLoc.maxVal > 0.95) {
//      // Return the location of the center of the matched area
//      val rectX = equipmentArea.map(_.x).getOrElse(0)
//      val rectY = equipmentArea.map(_.y).getOrElse(0)
//      val centerX = (minMaxLoc.maxLoc.x + tempImageHSV.cols / 2 + rectX).toInt
//      val centerY = (minMaxLoc.maxLoc.y + tempImageHSV.rows / 2 + rectY).toInt
//      Some((centerX, centerY))
//    } else {
//      println("Not found")
//      None
//    }
//  }

  def cropImageRect(mainImage: Mat, croppedArea: Option[Rect]): Mat = {
    croppedArea match {
      case Some(rect) =>
        // Create a new Mat to hold the cropped image
        val croppedImage = new Mat(mainImage, rect)
        // Clone the Mat to avoid modifying the original image
        val result = new Mat()
        croppedImage.copyTo(result)
        result
      case None =>
        // If no rectangle is provided, return the original image
        mainImage.clone()
    }
  }

  def singleCutWindow(mainImage: Mat, cutLoc: Option[(Int, Int)], croppedSide: String): Option[Rect] = {
    // Check if cutLoc is defined
    if (cutLoc.isDefined) {
      // Get the cut location
      val (cutX, cutY) = cutLoc.get

      // Get the size of the main image
      val (width, height) = (mainImage.cols, mainImage.rows)

      // Determine the size of the new rectangle
      val newWidth = if (croppedSide == "left" || croppedSide == "right") {
        if (croppedSide == "left") cutX else width - cutX
      } else {
        width
      }

      val newHeight = if (croppedSide == "top" || croppedSide == "bottom") {
        if (croppedSide == "top") cutY else height - cutY
      } else {
        height
      }

      // Determine the position of the new rectangle
      val newX = if (croppedSide == "right") {
        cutX
      } else {
        0
      }

      val newY = if (croppedSide == "bottom") {
        cutY
      } else {
        0
      }

      // Create the new rectangle
      val newRect = new Rect(newX, newY, newWidth, newHeight)

      // Return the new rectangle
      Some(newRect)
    } else {
      // Cut location is not defined
      None
    }
  }

  def foodDetection(mainImg: Mat): Option[(Int, Int)] = {

    // Load the food images
    val foodFiles = new File("images/food").listFiles.filter(f => f.getName.matches(".*\\.png$"))
    val foods: Seq[Mat] = foodFiles.map(f => loadImage(f.getAbsolutePath))

    // Define confidence threshold for matching
    val confidence: Double = 0.9

    // Loop through all food images and find matches in the main image
    for (food <- foods) {
      val matchMat: Mat = new Mat()
      Imgproc.matchTemplate(mainImg, food, matchMat, Imgproc.TM_CCOEFF_NORMED)

      val locations = for {
        row <- 0 until matchMat.rows
        col <- 0 until matchMat.cols
        if matchMat.get(row, col)(0) >= confidence
      } yield (col + food.cols / 2, row + food.rows / 2)

      if (locations.nonEmpty) {
        // Return the location of the center of the matched area
        val (xSum, ySum) = locations.reduce((a, b) => (a._1 + b._1, a._2 + b._2))
        val centerX = (xSum / locations.length).toInt
        val centerY = (ySum / locations.length).toInt
        return Some((centerX, centerY))
      }
    }
    println("No food found")
    None
  }



  def getLocationFromImage2(tempImage: Mat, mainImage: Mat): Option[(Int, Int)] = {
    // Apply thresholding to each channel separately
    val channels = new util.ArrayList[Mat]()
    Core.split(mainImage, channels)
    for (i <- 0 until channels.size()) {
      val channel = channels.get(i)
      Imgproc.threshold(channel, channel, 128, 255, Imgproc.THRESH_BINARY)
    }
    val mainImageThresholded = new Mat()
    Core.merge(channels, mainImageThresholded)

    // Create the result matrix
    val result = Mat.zeros(mainImage.rows - tempImage.rows + 1, mainImage.cols - tempImage.cols + 1, CvType.CV_32FC1)

    // Match the images using template matching
    Imgproc.matchTemplate(mainImageThresholded, tempImage, result, Imgproc.TM_CCOEFF)

    // Find the best match location
    val minMaxLoc = Core.minMaxLoc(result)

    // Check if the match is good enough
    if (minMaxLoc.maxVal > 0.80) {
      // Return the location of the center of the matched area
      val centerX = (minMaxLoc.maxLoc.x + tempImage.cols / 2).toInt
      val centerY = (minMaxLoc.maxLoc.y + tempImage.rows / 2).toInt
      Some((centerX, centerY))
    } else {
      println("Not found")
      None
    }
  }


  def getLocationSample(): Option[(Int, Int)] = {
    System.setProperty("java.library.path", "/usr/opencv/build/lib")
    println(System.getProperty("java.library.path"))
    setProperty("java.library.path", "/usr/opencv/build/lib")
    System.load("/usr/opencv/build/lib/libopencv_java455.so")
    Loader.load(classOf[org.bytedeco.opencv.global.opencv_core])
    Loader.load(classOf[org.bytedeco.opencv.global.opencv_imgcodecs])
    Loader.load(classOf[org.bytedeco.opencv.global.opencv_imgproc])

    // Load the images
    val tempImage = Imgcodecs.imread("images/temp/coin.png")
    val mainImage = Imgcodecs.imread("window.png")

    // Create the result matrix
    val result = Mat.zeros(mainImage.rows - tempImage.rows + 1, mainImage.cols - tempImage.cols + 1, CvType.CV_32FC1)

    // Match the images using template matching
    Imgproc.matchTemplate(mainImage, tempImage, result, Imgproc.TM_CCOEFF)

    // Find the best match location
    val minMaxLoc = Core.minMaxLoc(result)

    // Check if the match is good enough
    if (minMaxLoc.maxVal > 0.8) {
      // Return the location of the center of the matched area
      val centerX = (minMaxLoc.maxLoc.x + tempImage.cols / 2).toInt
      val centerY = (minMaxLoc.maxLoc.y + tempImage.rows / 2).toInt
      Some((centerX, centerY))
    } else {
      println("Not found")
      None
    }
  }

  def sendInsertKeyTest1(getLocationSample: () => Option[(Int, Int)], windowSubstring: String): Unit = {
    // Open the window with the specified substring in the background
    val processBuilder = new ProcessBuilder(Seq("xdotool", "search", "--name", windowSubstring).toList.asJava)
    val windowID = Source.fromInputStream(processBuilder.start().getInputStream()).mkString.trim()

    // Maximize the window
    val screenSize = Toolkit.getDefaultToolkit.getScreenSize
    val maxCommand = Seq("xdotool", "windowsize", windowID, screenSize.getWidth.toInt.toString, screenSize.getHeight.toInt.toString)
    val maxProcess = new ProcessBuilder(maxCommand.toList.asJava).start()
    Await.result(Future(maxProcess.waitFor()), 10.seconds)

    // Click in the window to give it focus
    val location = getLocationSample().getOrElse((0, 0))
    val robot = new Robot()
    robot.mouseMove(location._1, location._2)
    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)

  }

  def sendInsertKeyTest2(getLocationSample: () => Option[(Int, Int)], windowSubstring: String): Unit = {
    // Open the window with the specified substring in the background
    val processBuilder = new ProcessBuilder(Seq("xdotool", "search", "--name", windowSubstring).toList.asJava)
    val windowID = Source.fromInputStream(processBuilder.start().getInputStream()).mkString.trim()

    // Maximize the window
    val screenSize = Toolkit.getDefaultToolkit.getScreenSize
    val maxCommand = Seq("xdotool", "windowsize", windowID, screenSize.getWidth.toInt.toString, screenSize.getHeight.toInt.toString)
    val maxProcess = new ProcessBuilder(maxCommand.toList.asJava).start()
    Await.result(Future(maxProcess.waitFor()), 10.seconds)

    // Click in the window to give it focus
    val location = getLocationSample().getOrElse((0, 0))
    val robot = new Robot()
    robot.mouseMove(location._1, location._2)

    robot.keyPress(KeyEvent.VK_SHIFT)
    Thread.sleep(200)
    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
    Thread.sleep(200)
    robot.keyRelease(KeyEvent.VK_SHIFT)
  }



  def extractChannels(inputImagePath: String): util.ArrayList[Mat] = {
    // Load the input image
    val inputImage = Imgcodecs.imread(inputImagePath)

    // Create a list to hold the extracted channels
    val channels = new util.ArrayList[Mat]()

    // Split the input image into its blue, green, and red channels
    Core.split(inputImage, channels)

    // Return the extracted channels
    channels
  }

  def preprocessImage(inputImage: Mat): Mat = {
    // Extract the blue, green, and red channels
    val channels = new util.ArrayList[Mat]()
    Core.split(inputImage, channels)

    // Threshold each channel separately
    val thresholdValue = 150.0
    val blueChannel = new Mat()
    val greenChannel = new Mat()
    val redChannel = new Mat()
    Imgproc.threshold(channels.get(0), blueChannel, thresholdValue, 255.0, Imgproc.THRESH_BINARY)
    Imgproc.threshold(channels.get(1), greenChannel, thresholdValue, 255.0, Imgproc.THRESH_BINARY)
    Imgproc.threshold(channels.get(2), redChannel, thresholdValue, 255.0, Imgproc.THRESH_BINARY)

    // create a List of Mat objects from the Seq
    val channelsList = channels.asScala.toList.asJava

    // Combine the channels back into a single image
    val outputImage = new Mat()
    Core.merge(channelsList, outputImage)

    // Return the preprocessed image
    outputImage
  }

  def makeScreenshots(windowIDs: List[String]): Unit = {
    // Maximize and take screenshots of all matching windows
    for (windowID <- windowIDs) {
      maximizeWindow(windowID)
      Thread.sleep(500)
      val windowTitle = Seq("xdotool", "getwindowname", windowID).!!.trim
      val secondSection = windowTitle.split(" - ")(1).replaceAll("\\s", "_")
      val screenshot = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit.getScreenSize))
      val filename = s"window_$secondSection.png"
      val window = new File(filename)
      ImageIO.write(screenshot, "png", window)
    }
  }

//  def makeScreenshots(windowIDs: List[String]): Unit = {
//    // Maximize and take screenshots of all matching windows
//    for (windowID <- windowIDs) {
//      maximizeWindow(windowID)
//      Thread.sleep(500)
//      val windowTitle = Seq("xdotool", "getwindowname", windowID).!!.trim
//      val secondSection = windowTitle.split(" - ")(1).replaceAll("\\s", "")
//      val screenshot = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit.getScreenSize))
//      val filename = s"window_$secondSection.png"
//      val window = new File(filename)
//      ImageIO.write(screenshot, "png", window)
//    }
//  }

  def makeScreenshotMultiple(windowName: String): Unit = {
    // Window name
    val windowSubstring = windowName

    // Search for windows that match the window name substring
    val searchCommand = Seq("xdotool", "search", "--name", windowSubstring)
    val searchProcess = new ProcessBuilder(searchCommand.toList.asJava).start()
    val windowIDs = Source.fromInputStream(searchProcess.getInputStream()).getLines().toList

    // Maximize and take screenshots of all matching windows
    for (windowID <- windowIDs) {
      maximizeWindow(windowID)
      Thread.sleep(500)
      val windowTitle = Seq("xdotool", "getwindowname", windowID).!!.trim
      val secondSection = windowTitle.split(" - ")(1).replaceAll("\\s", "")
      val screenshot = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit.getScreenSize))
      val filename = s"window_$secondSection.png"
      val window = new File(filename)
      ImageIO.write(screenshot, "png", window)
    }
  }

  def areImagesIdentical(img1: Mat, img2: Mat): Boolean = {
    if (img1.size != img2.size || img1.depth != img2.depth) {
      false
    } else {
      val diff = new Mat()
      Core.compare(img1, img2, diff, Core.CMP_NE)
      val nonZeroElements = Core.countNonZero(diff.reshape(1))
      nonZeroElements == 0
    }
  }


  def cropCenter(sourceImage: Mat, cropWidth: Int, cropHeight: Int): Mat = {
    val centerX = sourceImage.width() / 2
    val centerY = sourceImage.height() / 2
    val startX = centerX - cropWidth / 2
    val startY = centerY - cropHeight / 2

    val roi = new Rect(startX, startY, cropWidth, cropHeight)
    val croppedImage = new Mat(sourceImage, roi)
    croppedImage
  }

  def makeScreenshotMat(windowID: String, characterName: String): Mat = {
    // Create a robot and take a screenshot of the specified window
    val screenSize = Toolkit.getDefaultToolkit.getScreenSize
    val robot = new Robot()
    val screenRectangle = new Rectangle(screenSize)
    val screenshot: BufferedImage = robot.createScreenCapture(screenRectangle)

    // Save the screenshot to a file
    val file = new File(s"window_${characterName}.png")
    ImageIO.write(screenshot, "png", file)

    // Read the saved file as a Mat object
    val bytes = Files.readAllBytes(file.toPath)
    val screenshotMat = Imgcodecs.imdecode(new MatOfByte(bytes: _*), Imgcodecs.IMREAD_UNCHANGED)

    // Return the Mat object
    screenshotMat
  }


  def makeScreenshotID(windowID: String, characterName: String): String = {
    // Create a robot and take a screenshot of the specified window
    val screenSize = Toolkit.getDefaultToolkit.getScreenSize
    val robot = new Robot()
    val screenRectangle = new Rectangle(screenSize)
    val screenshot = robot.createScreenCapture(screenRectangle)

    // Save the screenshot to a file
    val file = new File(s"window_${characterName}.png")
    ImageIO.write(screenshot, "png", file)

    // Return the path of the saved file
    file.getAbsolutePath
  }

  def makeScreenshot(windowName: String): Unit = {
    // Window name
    val windowSubstring = windowName

    // Open the window
    val processBuilder = new ProcessBuilder(Seq("xdotool", "search", "--name", windowSubstring).toList.asJava)
    val windowID = Source.fromInputStream(processBuilder.start().getInputStream()).mkString.trim()

    // Maximize the window
    val screenSize = Toolkit.getDefaultToolkit.getScreenSize
    val maxCommand = Seq("xdotool", "windowsize", windowID, screenSize.getWidth.toInt.toString, screenSize.getHeight.toInt.toString)
    val maxProcess = new ProcessBuilder(maxCommand.toList.asJava).start()
    Await.result(Future(maxProcess.waitFor()), 10.seconds)

    // Wait for a short period of time for the window to be maximized
    Thread.sleep(500)

    // Create a robot and take a screenshot of the activated window
    val robot = new Robot()
    val screenRectangle = new Rectangle(screenSize)
    val screenshot = robot.createScreenCapture(screenRectangle)

    val window = new File("window.png")
    ImageIO.write(screenshot, "png", window)
  }



  def saveMatAsPng(mat: Mat, filename: String): Unit = {
    val bufferedImage = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_BYTE_GRAY)
    mat.get(0, 0, bufferedImage.getRaster().getDataBuffer.asInstanceOf[java.awt.image.DataBufferByte].getData)
    val file = new File(filename)
    val finalFilename = if (filename.endsWith(".png")) filename else s"$filename.png"
    ImageIO.write(bufferedImage, "png", new File(finalFilename))
  }

//  def saveMatAsPng(mat: Mat, filename: String, coordinates: Option[(Option[(Int, Int)], Option[(Int, Int)], Option[(Int, Int)], Option[(Int, Int)])] = None): Unit = {
//    val croppedMat = coordinates match {
//      case Some((Some((x1, y1)), Some((x2, y2)), Some((x3, y3)), Some((x4, y4)))) =>
//        val pts = new MatOfPoint2f(
//          new Point(x1, y1),
//          new Point(x2, y2),
//          new Point(x3, y3),
//          new Point(x4, y4)
//        )
//        val rect = Imgproc.boundingRect(pts)
//        new Mat(mat, rect)
//      case _ =>
//        mat
//    }
//
//    val bufferedImage = new BufferedImage(croppedMat.width(), croppedMat.height(), BufferedImage.TYPE_BYTE_GRAY)
//    croppedMat.get(0, 0, bufferedImage.getRaster().getDataBuffer().asInstanceOf[java.nio.ByteBuffer].array())
//    val file = new File(filename)
//    ImageIO.write(bufferedImage, "png", file)
//  }

  def makeScreenshot2(windowName: String): Unit = {
    // Window name
    val windowSubstring = windowName

    // Open the window
    val processBuilder = new ProcessBuilder(Seq("xdotool", "search", "--name", windowSubstring).toList.asJava)
    val windowID = Source.fromInputStream(processBuilder.start().getInputStream()).mkString.trim()

    // Maximize the window
    val screenSize = Toolkit.getDefaultToolkit.getScreenSize
    val maxCommand = Seq("xdotool", "windowsize", windowID, screenSize.getWidth.toInt.toString, screenSize.getHeight.toInt.toString)
    val maxProcess = new ProcessBuilder(maxCommand.toList.asJava).start()
    Await.result(Future(maxProcess.waitFor()), 10.seconds)

    // Wait for a short period of time for the window to be maximized
    Thread.sleep(500)

    // Create a robot and take a screenshot of the activated window
    val robot = new Robot()
    val screenRectangle = new Rectangle(screenSize)
    val screenshot = robot.createScreenCapture(screenRectangle)

    val window = new File("window.png")
    ImageIO.write(screenshot, "png", window)
  }


  def makeScreenshotHSV(windowName: String): Unit = {
    // Window name
    val windowSubstring = windowName

    // Open the window
    val processBuilder = new ProcessBuilder(Seq("xdotool", "search", "--name", windowSubstring).toList.asJava)
    val windowID = Source.fromInputStream(processBuilder.start().getInputStream()).mkString.trim()

    // Maximize the window
    val screenSize = Toolkit.getDefaultToolkit.getScreenSize
    val maxCommand = Seq("xdotool", "windowsize", windowID, screenSize.getWidth.toInt.toString, screenSize.getHeight.toInt.toString)
    val maxProcess = new ProcessBuilder(maxCommand.toList.asJava).start()
    Await.result(Future(maxProcess.waitFor()), 10.seconds)

    // Wait for a short period of time for the window to be maximized
    Thread.sleep(500)

    // Create a robot and take a screenshot of the activated window
    val robot = new Robot()
    val screenRectangle = new Rectangle(screenSize)
    val screenshot = robot.createScreenCapture(screenRectangle)

    // Convert the screenshot to a Mat object
    val matImage = new Mat(screenshot.getHeight, screenshot.getWidth, CvType.CV_8UC3)
    val data = screenshot.getRGB(0, 0, screenshot.getWidth, screenshot.getHeight, null, 0, screenshot.getWidth)
    matImage.put(0, 0, data)

    // Convert to HSV color space
    val hsvImage = new Mat()
    Imgproc.cvtColor(matImage, hsvImage, Imgproc.COLOR_BGR2HSV)

    // Write the image to a file
    val window = new File("window.png")
    Imgcodecs.imwrite(window.getAbsolutePath, hsvImage)
  }


}

//  def robotMouseMove(position: (Double, Double)): Unit = {
//    val x = position._1.toInt
//    val y = position._2.toInt
//    robot.mouseMove(x, y)
//  }
//
//  def robotClick(): Unit = {
//    robot.mousePress(KeyEvent.BUTTON1_DOWN_MASK)
//    robot.mouseRelease(KeyEvent.BUTTON1_DOWN_MASK)
//  }
//
//  def robotPaste(): Unit = {
//    robot.keyPress(KeyEvent.VK_CONTROL)
//    robot.keyPress(KeyEvent.VK_V)
//    robot.keyRelease(KeyEvent.VK_V)
//    robot.keyRelease(KeyEvent.VK_CONTROL)
//  }
//
//  val robot = new Robot()


  //  def sendInsertKey(getLocationSample: () => Option[(Int, Int)], windowSubstring: String): Unit = {
//    // Create a new Robot instance
//    val robot = new Robot()
//
//    // Find the window by its name substring
//    val window = java.awt.Window.getWindows().find(w => w.getName().contains(windowSubstring)).getOrElse(
//      throw new RuntimeException(s"Window $windowSubstring not found"))
//
//    // Bring the window to the front
//    window.toFront()
//
//    // Get the window's location on the screen
//    val windowLocation = window.getLocationOnScreen()
//
//    // Get the target location on the screen
//    val targetLocation = getLocationSample().getOrElse((0, 0))
//    val targetX = windowLocation.getX() + targetLocation._1
//    val targetY = windowLocation.getY() + targetLocation._2
//
//    // Move the mouse cursor to the target location and click the right mouse button
//    robot.mouseMove(targetX.toInt, targetY.toInt)
//    robot.mousePress(InputEvent.BUTTON3_DOWN_MASK)
//    robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK)
//
//    // Send the "Insert" key event
//    robot.keyPress(KeyEvent.VK_INSERT)
//    robot.keyRelease(KeyEvent.VK_INSERT)
//  }

//
//def loadImage(imagePath: String): Mat = {
//  val image = Imgcodecs.imread(imagePath)
//  image
//}
//
//def getLocationFromImage(tempImage: Mat, mainImage: Mat): Option[(Int, Int)] = {
//  // Create the result matrix
//  val result = Mat.zeros(mainImage.rows - tempImage.rows + 1, mainImage.cols - tempImage.cols + 1, CvType.CV_32FC1)
//
//  // Match the images using template matching
//  Imgproc.matchTemplate(mainImage, tempImage, result, Imgproc.TM_CCOEFF)
//
//  // Find the best match location
//  val minMaxLoc = Core.minMaxLoc(result)
//
//  // Check if the match is good enough
//  if (minMaxLoc.maxVal > 0.90) {
//    // Return the location of the center of the matched area
//    val centerX = (minMaxLoc.maxLoc.x + tempImage.cols / 2).toInt
//    val centerY = (minMaxLoc.maxLoc.y + tempImage.rows / 2).toInt
//    Some((centerX, centerY))
//  } else {
//    println("Not found")
//    None
//  }
//}
