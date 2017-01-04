import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies {

  val scalajsJTime = Def.setting("org.scala-js" %%% "scalajs-java-time" % "0.2.0")

  val upickle = Def.setting("com.lihaoyi" %%% "upickle" % "0.4.3")

  val slogging = Def.setting("biz.enef" %%% "slogging" % "0.5.2")

  object tests {
    val scalajsenvs = "org.scala-js" %% "scalajs-js-envs" % scalaJSVersion % "test"
  }

}
