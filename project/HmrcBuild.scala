import sbt.Keys._
import sbt._
import org.scalajs.sbtplugin._
import ScalaJSPlugin.autoImport._

object HmrcBuild extends Build {

  import uk.gov.hmrc.SbtAutoBuildPlugin
  import uk.gov.hmrc.versioning.SbtGitVersioning
  import SbtAutoBuildPlugin._
  import Dependencies._
  
  val nameApp = "paye-estimator"

  lazy val payeEstimator = Project(nameApp, file("."))
    .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, ScalaJSPlugin)
    .settings(
      autoSourceHeader := false,
      scalaVersion := "2.11.8",
      libraryDependencies ++= Seq(

        Test.scalaTest,
        Test.pegdown,

        //add scala js wrapper ('%%%') libs here
        "org.scala-js" %%% "scalajs-java-time" % "0.2.0",
        "com.lihaoyi" %%% "utest" % "0.4.3" % "test",
        "com.lihaoyi" %%% "upickle" % "0.4.3"
      ),

      testFrameworks += new TestFramework("utest.runner.Framework"),

      resolvers += Resolver.typesafeRepo("releases"),
//      crossScalaVersions := Seq("2.11.8"),
//      scalaJSOutputWrapper := ("var __ScalaJSEnv = { exportsNamespace: exports };", ""),
      //Rhino JS interpreter disabled, Node.js must be installed
      scalaJSUseRhino := false,
      //add js libraries
      skip in packageJSDependencies := false,
      //minify
      scalaJSStage := FullOptStage
    )
}

// TODO: Remove once all tests are converted. 
object Dependencies {

  object Compile {
  }

  sealed abstract class Test(scope: String) {
    val scalaTest = "org.scalatest" %% "scalatest" % "3.0.0" % scope
    val pegdown = "org.pegdown" % "pegdown" % "1.6.0" % scope
  }

  object Test extends Test("test")

  object IntegrationTest extends Test("it")

}