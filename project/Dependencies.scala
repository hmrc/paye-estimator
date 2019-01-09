import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies {
  private val play25Version = "2.5.19"
  private val play26Version = "2.6.13"

  val compile: Seq[ModuleID] =
    PlayCrossCompilation.dependencies(
      shared = Seq(
        "org.scala-js" %% "scalajs-js-envs" % scalaJSVersion % "test",
        "org.scalatest" %% "scalatest" % "3.0.5" % "test",
        "org.pegdown" % "pegdown" % "1.6.0"
      ),
      play25 = Seq(
        "com.typesafe.play" %% "play-json" % play25Version % "test"
      ),
      play26 = Seq(
        "com.typesafe.play" %% "play-json" % play26Version % "test"
      )
    )

  def apply(): Seq[ModuleID] = compile
}
