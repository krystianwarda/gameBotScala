package radar
import org.tensorflow._
import org.opencv.core.{CvType, Mat}
import player.Player
import utils.image.{convertImageToArray, cutOutRectangleImage, cutOutSquareImage, getLocationFromImageMidEdgeDetectLowConf, getLocationFromImageMidMatchTemp, getLocationFromImageMidMatchTempLowConf, loadImage}
import utils.mouse.{calcLocOffset, mouseMoveSmooth}

import java.nio.ByteBuffer
import net.jpountz.xxhash.XXHashFactory

import java.nio.ByteBuffer
import net.jpountz.lz4._
import net.jpountz.xxhash.XXHashFactory
import java.io.{File, FileInputStream, ObjectInputStream}
import scala.collection.mutable.ArrayBuffer
import java.io.File
import org.opencv.core.{Core, Mat, Point, Rect}

object core {

  def findCharLocation(radarImage: Mat): Unit = {

    println("Loading the array")

//    println(findImageInFloorsArray(convertImageToArray(radarImage)))
    println(getLocationFromImageMidMatchThreshold(convertImageToArray(radarImage), loadFloorsArray,0.70, 1))
//    locateRadarImage(radarImage)
//    val (previousCoordinateXPixel, previousCoordinateYPixel) = getPixelFromCoordinateHash(hashitHex(matToArray(radarImage)))
//    println(previousCoordinateXPixel)
//    println(previousCoordinateYPixel)
    println("to be completed")
  }

  def loadFloorsArray(): Array[Array[Array[Int]]] = {
    val floorsArrayFile = new File("images/radar/tibiaMapsArrays/FloorsArray.dat")
    val fis = new FileInputStream(floorsArrayFile)
    val ois = new ObjectInputStream(fis)
    val floorsArray = ois.readObject().asInstanceOf[Array[Array[Array[Int]]]]
    ois.close()
    fis.close()
    floorsArray
  }
  def findImageInFloorsArray(radarImageArray: Array[Array[Int]]): Option[(Int, Int, Int)] = {
    val floorsArrayFile = new File("images/radar/tibiaMapsArrays/FloorsArray.dat")
    val fis = new FileInputStream(floorsArrayFile)
    val ois = new ObjectInputStream(fis)
    val floorsArray = ois.readObject().asInstanceOf[Array[Array[Array[Int]]]]
    ois.close()
    fis.close()

    for {
      (floor, floorIndex) <- floorsArray.zipWithIndex
      yEnd = floor.length - radarImageArray.length
      xEnd = floor(0).length - radarImageArray(0).length
      y <- 0 to yEnd
      x <- 0 to xEnd
      if floor.slice(y, y + radarImageArray.length).map(_.slice(x, x + radarImageArray(0).length)) == radarImageArray
    } return Some((x + radarImageArray(0).length/2, y + radarImageArray.length/2, floorIndex))

    None
  }

//  def getLocationFromImageMidMatchThreshold(radarImg: Array[Array[Int]], floorsArr: Array[Array[Array[Int]]], threshold: Double): Option[(Int, Int, Int)] = {
//    val radarHeight = radarImg.length
//    val radarWidth = radarImg.head.length
//    val floorHeight = floorsArr.head.length
//    val floorWidth = floorsArr.head.head.length
//
//    for {
//      floorLevel <- floorsArr.indices
//      yStart <- 0 to (floorHeight - radarHeight)
//      xStart <- 0 to (floorWidth - radarWidth)
//    } {
//      var similaritySum = 0.0
//      var similarityCount = 0
//      for (y <- 0 until radarHeight) {
//        for (x <- 0 until radarWidth) {
//          val floorPixel = floorsArr(floorLevel)(yStart + y)(xStart + x)
//          val radarPixel = radarImg(y)(x)
//          val similarity = 1.0 - (distance(floorPixel, radarPixel) / 441.67) // normalizing by maximum possible distance
//          if (similarity > threshold) {
//            similaritySum += similarity
//            similarityCount += 1
//          }
//        }
//      }
//      if (similarityCount > 0 && similaritySum / similarityCount >= threshold) {
//        val xMid = xStart + radarWidth / 2
//        val yMid = yStart + radarHeight / 2
//        return Some((xMid, yMid, floorLevel))
//      }
//    }
//    None
//  }

  def getLocationFromImageMidMatchThreshold(radarImg: Array[Array[Int]], floorsArr: Array[Array[Array[Int]]], threshold: Double, missingPixelPenalty: Double): Option[(Int, Int, Int)] = {
    val radarHeight = radarImg.length
    val radarWidth = radarImg.head.length
    val floorHeight = floorsArr.head.length
    val floorWidth = floorsArr.head.head.length

    var totalSimilaritySum = 0.0
    var totalSimilarityCount = 0
    var missingPixelCount = 0

    for {
      floorLevel <- floorsArr.indices
      yStart <- 0 to (floorHeight - radarHeight)
      xStart <- 0 to (floorWidth - radarWidth)
    } {
      var similaritySum = 0.0
      var similarityCount = 0
      var localMissingPixelCount = 0
      for (y <- 0 until radarHeight) {
        for (x <- 0 until radarWidth) {
          if (yStart + y >= floorHeight || xStart + x >= floorWidth) {
            localMissingPixelCount += 1
          } else {
            val floorPixel = floorsArr(floorLevel)(yStart + y)(xStart + x)
            val radarPixel = radarImg(y)(x)
            val similarity = 1.0 - (distance(floorPixel, radarPixel) / 441.67) // normalizing by maximum possible distance
            if (similarity > threshold) {
              similaritySum += similarity
              similarityCount += 1
            }
          }
        }
      }
      if (similarityCount > 0) {
        val similarityAvg = similaritySum / similarityCount
        val similarityWeighted = similarityAvg - (missingPixelCount * missingPixelPenalty)
        totalSimilaritySum += similarityWeighted
        totalSimilarityCount += 1
      } else if (localMissingPixelCount > 0) {
        missingPixelCount += localMissingPixelCount
      }
    }

    if (totalSimilarityCount > 0 && totalSimilaritySum / totalSimilarityCount >= threshold) {
      val xMid = (radarWidth - 1) / 2
      val yMid = (radarHeight - 1) / 2
      return Some((xMid, yMid, 0))
    }
    None
  }



  def distance(p1: Int, p2: Int): Double = {
    val r1 = (p1 >> 16) & 0xFF
    val g1 = (p1 >> 8) & 0xFF
    val b1 = p1 & 0xFF
    val r2 = (p2 >> 16) & 0xFF
    val g2 = (p2 >> 8) & 0xFF
    val b2 = p2 & 0xFF
    math.sqrt((r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2))
  }


  def cutRadarImage(charWindow: Mat): Mat = {
//    var radarWindowImage = loadImage("images/radar/radarWindow.png")
    var radarCrossImage = loadImage("images/radar/radarCenter.png")
//    var radarWindowLoc = getLocationFromImageMidMatchTemp(radarWindowImage, charWindow)
    var radarCenterLoc = getRadarCenterLoc(charWindow)
    var radarImage = cutOutRectangleImage(charWindow, radarCenterLoc, 120, 140)
    radarImage
  }

  def getRadarCenterLoc(charWindow: Mat): Option[(Int, Int)] = {
    var radarCrossImage = loadImage("images/radar/radarCenter.png")
    var radarCenterLoc = getLocationFromImageMidEdgeDetectLowConf(radarCrossImage, charWindow)
    radarCenterLoc
  }

//  def findRadarCrossImage(charWindowImage: Mat, radarWindowLoc: Option[(Int, Int)], aproxDist: Int, radarCrossImage: Mat): Option[(Int, Int)] = {
//    // Find search area
//    val searchArea = radarWindowLoc match {
//      case Some((x, y)) =>
//        val startX = x - radarCrossImage.cols()
//        val startY = y - aproxDist - radarCrossImage.rows()
//        new Rect(startX, startY, charWindowImage.cols() - startX, y - startY)
//      case None =>
//        new Rect(0, 0, charWindowImage.cols(), charWindowImage.rows())
//    }
//
//    // Convert input images to the correct format if needed
//    val inputImage = if (charWindowImage.depth() != CvType.CV_8U) {
//      val converted = new Mat()
//      charWindowImage.convertTo(converted, CvType.CV_8U)
//      converted
//    } else {
//      charWindowImage
//    }
//
//    val templateImage = if (radarCrossImage.depth() != CvType.CV_8U) {
//      val converted = new Mat()
//      radarCrossImage.convertTo(converted, CvType.CV_8U)
//      converted
//    } else {
//      radarCrossImage
//    }
//
//    // Find location of radar cross image
//    val inputSubmat = new Mat(inputImage, searchArea)
//    val foundLoc = getLocationFromImageMidMatchTemp(templateImage, inputSubmat)
//
//
//    // Convert location to global coordinates
//    foundLoc.map(loc => (loc._1 + searchArea.x + radarCrossImage.cols() / 2, loc._2 + searchArea.y + radarCrossImage.rows() / 2))
//  }



//  def matToArray(mat: Mat): Array[Int] = {
//    val numChannels = mat.channels()
//    val numRows = mat.rows()
//    val numCols = mat.cols()
//
//    val buffer = ByteBuffer.allocate(numRows * numCols * numChannels * 4) // 4 bytes per Int
//    mat.get(0, 0, buffer.array)
//
//    val array = new Array[Int](numRows * numCols)
//    var i = 0
//    while (i < array.length) {
//      array(i) = buffer.getInt(i * 4)
//      i += 1
//    }
//    array
//  }

//  def hashitHex(arr: Array[Int]): String = {
//    val seed: Int = 20220605
//    val byteBuffer: ByteBuffer = ByteBuffer.allocate(arr.length * 4)
//    byteBuffer.asIntBuffer.put(arr)
//    val xxHashFactory: XXHashFactory = XXHashFactory.fastestInstance()
//    val xxHash: net.jpountz.xxhash.XXHash32 = xxHashFactory.hash32()
//    val hashValue: Int = xxHash.hash(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining(), seed)
//    Integer.toHexString(hashValue)
//  }

  def getPixelFromCoordinateHash(arr: (Int, Int, String)): (Int, Int) = {
    val (seed, hashValue, _) = arr
    val xxHashFactory: XXHashFactory = XXHashFactory.fastestInstance()
    val xxHash: net.jpountz.xxhash.XXHash32 = xxHashFactory.hash32()
    val hashBytes: Array[Byte] = Array(
      (seed >> 24).toByte, (seed >> 16).toByte, (seed >> 8).toByte, seed.toByte,
      (hashValue >> 24).toByte, (hashValue >> 16).toByte, (hashValue >> 8).toByte, hashValue.toByte
    )
    val x: Int = (hashBytes(0) & 0xFF) << 24 | (hashBytes(1) & 0xFF) << 16 | (hashBytes(2) & 0xFF) << 8 | (hashBytes(3) & 0xFF)
    val y: Int = (hashBytes(4) & 0xFF) << 24 | (hashBytes(5) & 0xFF) << 16 | (hashBytes(6) & 0xFF) << 8 | (hashBytes(7) & 0xFF)
    (x - 31744, y - 30976)
  }
  def locateRadarImage(radarImage: Mat): Option[(Int, Int, Int)] = {
    val floorDirectory = new File("images/radar/tibiaMaps")
    val floors = (7 to 0 by -1) ++ (8 to 15)

    for (floor <- floors) {
      println(floor)
      val tempFloorImage = s"${floorDirectory.getAbsolutePath}/floor-$floor.png"
      if (new File(tempFloorImage).exists()) {
        val location = getLocationFromImageMidEdgeDetectLowConf(radarImage, loadImage(tempFloorImage))
        if (location.isDefined) {
          return Some((location.get._1, location.get._2, floor))
        }
      }
    }

    None
  }

//  def locateRadarImage(radarImage: Mat): Unit = {
//    val stringList = List("bottomGameWindow", "leftGameWindow", "topGameWindow", "rightGameWindow")
//    var locations = ListBuffer.empty[Option[(Int, Int)]]
//    for (str <- stringList) {
//      val tempImage = loadImage(s"images/screenInfo/$str.png")
//      val tempLoc = getLocationFromImageMidMatchTemp(tempImage, mainImage)
//    }
//  }


}
