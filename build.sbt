
name := "paye-estimator"

lazy val root = project.in(file("."))
//  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, ScalaJSPlugin)
  .enablePlugins(SbtGitVersioning, ScalaJSPlugin)
  .settings(
    scalaVersion := "2.11.8",
    crossScalaVersions := Seq("2.11.8"),
    libraryDependencies ++= Seq(
      Dependencies.scalajsTime.value,
      Dependencies.tests.scalajsenvs,
      "org.scalatest" %%% "scalatest" % "3.0.0" % "test",
      "org.pegdown" % "pegdown" % "1.6.0"
    ),
    scalaJSStage in Global := FastOptStage,
    jsDependencies += RuntimeDOM % "test"
  )