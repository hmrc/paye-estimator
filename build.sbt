
name := "paye-estimator"

lazy val root = project.in(file("."))
//  TODO: temp comment until SbtAutoBuildPlugin has had PR for removing default settings with test arguments merged and release
//  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, ScalaJSPlugin)
  .enablePlugins(SbtGitVersioning, ScalaJSPlugin, UniversalPlugin, UniversalDeployPlugin)
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

    topLevelDirectory := None,
    mappings in Universal ++= Seq((target.value / "scala-2.11" / s"${name.value}-opt.js", s"${name.value}.js"))
  )
  .settings(LocalTempBuildSettings.localDefaultSettings : _*)


