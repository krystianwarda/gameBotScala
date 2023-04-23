package player

import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import utils.image.{loadImage, saveMatAsPng}

import java.io.File
//val mainImg: Mat = loadImage("numbertest.png")
object skillsWindow {

  def createRectangle(loc: Option[(Int, Int)]): Option[(Option[(Int, Int)], Option[(Int, Int)], Option[(Int, Int)], Option[(Int, Int)])] = {
    val fixedX = 120 // fixed number to add to x value of loc
    val sideA = 70 // length of top and bottom sides
    val sideB = 28 // length of left and right sides

    loc match {
      case Some((x, y)) =>
        val centerX = x + fixedX
        val a1 = Some(centerX - (sideA / 2), y - (sideB / 2))
        val a2 = Some(centerX + (sideA / 2), y - (sideB / 2))
        val b1 = Some(centerX - (sideA / 2), y + (sideB / 2))
        val b2 = Some(centerX + (sideA / 2), y + (sideB / 2))
        Some(a1, a2, b1, b2)
      case None => None
    }
  }


    def numberDetection(mainImg: Mat): Int = {
    // Load main image and single digit images
    val digitPath = "images/skillsWindow/numbers"
    val digitFiles = new File(digitPath).listFiles.filter(f => f.getName.matches("^[0-9][r]*\\.png$"))
    val digits: Map[String, Mat] = digitFiles.map(f => (f.getName.dropRight(4), loadImage(f.getAbsolutePath))).toMap

    // Define confidence threshold for matching
    val confidence: Double = 0.95

    // Loop through all single digit images and find matches in the main image
    val matches = digits.flatMap { case (digit, digitImg) =>
      val matchMat: Mat = new Mat()
      Imgproc.matchTemplate(mainImg, digitImg, matchMat, Imgproc.TM_CCOEFF_NORMED)

      val digitLocations = for {
        row <- 0 until matchMat.rows
        col <- 0 until matchMat.cols
        if matchMat.get(row, col)(0) >= confidence
      } yield (col, digit, row)

      if (digitLocations.isEmpty) {
        //        println(s"No matches found for digit $digit")
        None
      } else {
        Some(digitLocations)
      }
    }

    // Sort the matches by their x-coordinate to get the order from left to right
    val sortedMatches = matches.flatten.toSeq.sortBy(_._1)

    // Print out the digits found in the order from left to right
    var digitsFound = ""
    var lastCol = -1
    for ((col, digit, row) <- sortedMatches) {
      if (col > lastCol) {
        digitsFound += digit
        lastCol = col
      }
    }

    // Remove "r" symbols if they exist in the digitsFound string
    digitsFound = digitsFound.filterNot(_ == 'r')

    // Convert the digitsFound string to an integer
    try {
      digitsFound.toInt
    } catch {
      case e: NumberFormatException =>
        println(s"Error converting $digitsFound to integer: ${e.getMessage}")
        0
    }
    digitsFound.toInt
  }

}
