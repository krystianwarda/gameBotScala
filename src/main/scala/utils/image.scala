package utils
import java.util.ArrayList
import org.bytedeco.javacpp.Loader
import net.sourceforge.tess4j.Tesseract

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import org.opencv.core.{Mat, MatOfPoint2f, Point, Rect}
import org.opencv.imgproc.Imgproc
import scala.sys.process.stringSeqToProcess
import java.awt.image.{BufferedImage, DataBufferByte}
import javax.imageio.ImageIO
import org.opencv.core.{Core, CvType, Mat, MatOfPoint2f, Point, Rect, Scalar}

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
import utils.core.maximizeWindow
import utils.mouse.mouseMoveSmooth

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

  def mouseOverRectangle(rectangleCoords: Option[(Option[(Int, Int)], Option[(Int, Int)], Option[(Int, Int)], Option[(Int, Int)])]): Unit = {
    // call mouseMoveSmooth on each coordinate using foreach
    rectangleCoords.foreach { case (a1, a2, b1, b2) =>
      // call mouseMoveSmooth on each coordinate
      Seq(a1, a2, b1, b2).flatten.foreach { loc =>
        println(Some(loc))
        mouseMoveSmooth(Some(loc))
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
      val tempLoc = getLocationFromImage(tempImage, mainImage, getSideFromFilename(str))
      locations ++= tempLoc.map(Some(_))
      Thread.sleep(1000)
      mouseMoveSmooth(tempLoc)
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
