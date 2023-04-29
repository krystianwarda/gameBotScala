//package cavebot
//
//import org.opencv.core.Mat
//
//import java.io.{ObjectInputStream, ObjectOutputStream}
//import org.opencv.core.Mat
//import org.opencv.core.CvType
//import org.opencv.imgcodecs.Imgcodecs
//import org.opencv.imgproc.Imgproc
//import org.opencv.core.Core
//
//class SerializableMat(mat: Mat) extends Serializable {
//  private def writeObject(out: ObjectOutputStream): Unit = {
//    val data = new Array[Byte](mat.total.toInt * mat.elemSize.toInt)
//    mat.get(0, 0, data)
//    out.writeInt(mat.rows)
//    out.writeInt(mat.cols)
//    out.writeInt(mat.`type`())
//    out.write(data)
//  }
//
//  private def readObject(in: ObjectInputStream): Unit = {
//    val rows = in.readInt()
//    val cols = in.readInt()
//    val `type` = in.readInt()
//    val data = new Array[Byte](rows * cols * Mat.channels(`type`))
//    in.readFully(data)
//    mat.create(rows, cols, `type`)
//    mat.put(0, 0, data)
//  }
//  def get(): Mat = mat
//}
