import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies {

  val scalajsTime = Def.setting("org.scala-js" %%% "scalajs-java-time" % "0.2.0")

  val scalajsJson = Def.setting("com.lihaoyi" %%% "upickle" % "0.4.3")

  object tests {
//    val scalajsenvs = "org.scala-js" %% "scalajs-js-envs" % scalaJSVersion % "test"
    val scalatest = "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  }

}
