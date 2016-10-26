
enablePlugins(SbtGitVersioning, ScalaJSPlugin, UniversalPlugin)

name := "paye-estimator"

LocalTempBuildSettings.localDefaultSettings

scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.11.8")

libraryDependencies ++= Seq(
      Dependencies.scalajsTime.value,
      Dependencies.scalajsJson.value,
      Dependencies.tests.scalajsenvs,
      "org.scalatest" %%% "scalatest" % "3.0.0" % "test",
      "org.pegdown" % "pegdown" % "1.6.0"
    )

scalaJSStage in Global := FastOptStage


topLevelDirectory := None

stagingDirectory := (target.value / "scala-2.11")

mappings in Universal ++= Seq((target.value / "scala-2.11" / s"${name.value}-opt.js", s"${name.value}.js"))

val packageTgz = taskKey[File]("package-tgz")
packageTgz := (baseDirectory in Compile).value / "target" / "universal" / (name.value + "-" + version.value + ".tgz")
artifact in (Universal, packageTgz) ~= { (art:Artifact) => art.copy(`type` = "tgz", extension = "tgz") }
addArtifact(artifact in (Universal, packageTgz), packageTgz in Universal)

publish <<= publish dependsOn (packageZipTarball in Universal)

publishM2 <<= publishM2 dependsOn (packageZipTarball in Universal)

publishLocal <<= publishLocal dependsOn (packageZipTarball in Universal)
