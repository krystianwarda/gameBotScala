scalaVersion := "2.12.14"

name := "hello-world"
organization := "ch.epfl.scala"
version := "1.0"

libraryDependencies ++= Seq(
  "org.bytedeco" % "javacv" % "1.5.6",
  "org.scala-lang.modules" % "scala-parser-combinators" % "2.1.1",
  "net.java.dev.jna" % "jna" % "5.13.0",
  "net.java.dev.jna" % "jna-platform" % "5.13.0",
  "org.jogamp.jogl" % "nativewindow" % "2.3.2",
  "net.sourceforge.tess4j" % "tess4j" % "5.7.0",
  "org.bytedeco" % "javacpp" % "1.5.7",
  "org.bytedeco" % "javacv-platform" % "1.5.7",
  "org.bytedeco.javacpp-presets" % "opencv" % "4.0.1-1.4.4",
  "org.slf4j" % "slf4j-api" % "2.0.7",
  "org.bytedeco" % "opencv" % "4.5.5-1.5.7",
  "com.github.wichtounet" % "scalax11" % "2.0.0",
  "com.xuggle" % "xuggle-xuggler" % "5.4",
  "org.scala-lang.modules" % "scala-swing" % "3.0.0",
  "org.scalatest" % "scalatest" % "3.2.10" % "test"
)
//"com.liferay" % "com.xuggle.xuggler" % "5.4",
