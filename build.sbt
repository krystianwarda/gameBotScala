scalaVersion := "2.12.14"

name := "hello-world"
organization := "ch.epfl.scala"
version := "1.0"

libraryDependencies ++= Seq(
  "org.bytedeco" % "javacv" % "1.5.6",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
  "net.java.dev.jna" % "jna" % "5.13.0",
  "net.java.dev.jna" % "jna-platform" % "5.13.0",
  "org.jogamp.jogl" % "nativewindow" % "2.3.2",
  "net.sourceforge.tess4j" % "tess4j" % "5.7.0",
  "org.bytedeco" % "javacpp" % "1.5.7",
  "org.bytedeco" % "javacv-platform" % "1.5.7",
  "org.bytedeco.javacpp-presets" % "opencv" % "4.0.1-1.4.4",
  "org.slf4j" % "slf4j-api" % "2.0.7",
  "org.bytedeco" % "opencv" % "4.5.5-1.5.7",
  "org.tensorflow" % "tensorflow" % "1.15.0",
  "org.scalatest" %% "scalatest" % "3.2.14" % "Test",
  "net.jpountz.lz4" % "lz4" % "1.3.0",
  "io.circe" %% "circe-core" % "0.14.1",
  "io.circe" %% "circe-generic" % "0.14.1",
  "org.scala-lang.modules" %% "scala-swing" % "2.1.1",
  "org.scalafx" %% "scalafx" % "8.0.102-R11",
  "io.monix" %% "monix" % "3.4.0",
  "com.1stleg" % "jnativehook" % "2.1.0"
)

//"org.scala-lang" % "scala-library" % "2.11.12"
//"com.liferay" % "com.xuggle.xuggler" % "5.4",
//"com.github.wichtounet" % "scalax11" % "2.0.0",
//"com.xuggle" % "xuggle-xuggler" % "5.4",
//"org.scala-lang.modules" % "scala-parser-combinators" % "2.1.1",
//"org.scalatest" % "scalatest" % "3.2.10" % "test",
//"org.scala-lang" % "scala-library" % "2.13.8"
//
//"org.scalafx" %% "scalafx" % "12.0.2-R18",
//"org.openjfx" % "javafx-controls" % "20"
//  "org.scalafx" %% "scalafx" % "12.0.2-R18",
