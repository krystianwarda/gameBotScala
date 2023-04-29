package radar
import java.awt.image.BufferedImage
import java.io.{File, FileOutputStream, ObjectOutputStream}
import javax.imageio.ImageIO

object builds {
  def main(args: Array[String]): Unit = {
    convertImagestoArrays("images/radar/tibiaMaps", "FloorsArray.dat")
  }
  def convertImagestoArrays(directoryPath: String, outputFile: String): Unit = {
    val files = new File(directoryPath).listFiles.filter(_.getName.endsWith(".png")).sorted
    val allArrays = files.map { file =>
      val img = ImageIO.read(file)
      val width = img.getWidth
      val height = img.getHeight
      val arr = new Array[Array[Int]](height)
      for (y <- 0 until height) {
        arr(y) = new Array[Int](width)
        for (x <- 0 until width) {
          val rgb = img.getRGB(x, y)
          arr(y)(x) = rgb
        }
      }
      arr
    }
    val fos = new FileOutputStream(outputFile)
    val oos = new ObjectOutputStream(fos)
    oos.writeObject(allArrays)
    oos.close()
    fos.close()
  }


}
