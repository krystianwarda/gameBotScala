package cavebot
import org.opencv.core.Mat
import java.nio.charset.StandardCharsets
import io.circe._
import io.circe.generic.auto.exportEncoder
import io.circe.generic.semiauto._
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import java.io.{BufferedWriter, OutputStreamWriter}
import scala.collection.mutable.ListBuffer
import java.io.{File, FileOutputStream, BufferedWriter, OutputStreamWriter}
import java.nio.charset.StandardCharsets
import java.io.{BufferedWriter, File, FileOutputStream, OutputStreamWriter}
//import java.nio.charset.
import java.io._
import java.io.{BufferedOutputStream, ObjectOutputStream}
import java.nio.file.{Files, Paths}
import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import org.opencv.core.Mat
import org.opencv.core.CvType
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.core.Core


  @SerialVersionUID(1234L)
class CaveBot(caveBotName: String) extends Serializable {
  var waypointsList: List[Array[Array[Int]]] = List()

  // mutable field to store the list of lootbag itemsss
  var lootbagList: List[String] = List()

  // field to store the index of the previously returned waypoint
  var previousWaypointItem: Int = -1

  // method to add a waypoint image to the list
  def addWaypointArray(waypointArray: Array[Array[Int]]): Unit = {
    waypointsList = waypointsList :+ waypointArray
  }


  // method to return the next waypoint from the list in a circular fashion
  def getNextWaypoint(): Option[Array[Array[Int]]] = {
    println(s"waypointsList: $waypointsList") // Debugging line
    if (waypointsList.nonEmpty) {
      previousWaypointItem = (previousWaypointItem + 1) % waypointsList.size
      println(s"previousWaypointItem: $previousWaypointItem") // Debugging line
      Some(waypointsList(previousWaypointItem))
    } else {
      None
    }
  }

  def getCaveBotName(): String = {
    return caveBotName
  }

  def saveStateToFile(filename: String): Unit = {
    val fileOutput: String = "classes/cavebot/" + filename + ".ser"
    val file = new FileOutputStream(fileOutput)
    val out = new ObjectOutputStream(file)
    out.writeObject(this)
    out.close()
    file.close()
  }

  def loadStateFromFile(filename: String): Unit = {
    val file = new FileInputStream(filename)
    val in = new ObjectInputStream(file)
    val bot = in.readObject().asInstanceOf[CaveBot]
//    mats = bot.mats
//    waypointsList = bot.waypointsList
    in.close()
    file.close()
  }
}
