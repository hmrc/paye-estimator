
name := "paye-estimator"

lazy val root = project.in(file("."))
//  TODO: temp comment until SbtAutoBuildPlugin has had PR for removing default settings with test arguments merged and release
//  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, ScalaJSPlugin)
  .enablePlugins(SbtGitVersioning, ScalaJSPlugin)
  .settings(
    scalaVersion := "2.11.8",
    crossScalaVersions := Seq("2.11.8"),
    libraryDependencies ++= Seq(
      Dependencies.scalajsTime.value,
      Dependencies.scalajsJson.value,
      Dependencies.tests.scalajsenvs,
      "org.scalatest" %%% "scalatest" % "3.0.0" % "test",
      "org.pegdown" % "pegdown" % "1.6.0"
    ),
    scalaJSStage in Global := FastOptStage,
    jsDependencies += RuntimeDOM % "test"
  )
  .settings(
    assemblyJarName in assembly := "paye-estimator.jar"
  )
  .settings(LocalTempBuildSettings.localDefaultSettings : _*)
