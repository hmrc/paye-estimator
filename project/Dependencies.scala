import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies {
  object tests {
    val scalajsenvs = "org.scala-js" %% "scalajs-js-envs" % scalaJSVersion % "test"
    val scalatest = "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    val playJson = "com.typesafe.play" %% "play-json" % "2.5.19" % "test"
  }
}
