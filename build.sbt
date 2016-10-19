//import org.scalajs.sbtplugin.ScalaJSPluginInternal

name := "paye-estimator"

lazy val root = project.in(file("."))
    .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, ScalaJSPlugin)
    .aggregate(crossJS, crossJVM)
    .settings(
      scalaVersion := "2.11.8",
      crossScalaVersions := Seq("2.11.8")
//      publish := {},
//      publishLocal := {}
    )

lazy val cross = crossProject.in(file("."))
  .settings(
    name := "paye-estimator",
    scalaVersion := "2.11.8",
    crossScalaVersions := Seq("2.11.8"),
    libraryDependencies ++= Seq(
      "org.scalatest" %%% "scalatest" % "3.0.0" % "test"
    )
  )
  .jvmSettings(
    // Add JVM-specific settings here
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      Dependencies.scalajsTime.value,
      Dependencies.tests.scalajsenvs),
    scalaJSStage in Global := FastOptStage,
    jsDependencies += RuntimeDOM % "test"
  )
//  .configs(TestSettings.ItTest)
//  .settings(inConfig(Test)(TestSettings.testSettings):_*)
//  .settings(inConfig(TestSettings.ItTest)(Defaults.testTasks):_*)
//  .jsSettings(inConfig(TestSettings.ItTest)(ScalaJSPluginInternal.scalaJSTestSettings):_*)
//  .settings(inConfig(TestSettings.ItTest)(TestSettings.itTestSettings):_*)
//  .jsSettings(
//    unmanagedSourceDirectories in TestSettings.ItTest ++=
//      CrossType.Full.sharedSrcDir(baseDirectory.value, "test").toSeq :+
//        CrossType.Full.jsDir(baseDirectory.value).getParentFile / "src" / "test" / "scala")

lazy val crossJVM = cross.jvm
lazy val crossJS = cross.js