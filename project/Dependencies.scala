import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies {

  val scalajsTime = Def.setting("org.scala-js" %%% "scalajs-java-time" % "0.2.0")

  object tests {
    val scalajsenvs = "org.scala-js" %% "scalajs-js-envs" % scalaJSVersion % "test"
  }

}
