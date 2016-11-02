import org.scalajs.core.ir.ScalaJSVersions
import org.scalajs.sbtplugin.ScalaJSCrossVersion
import org.scalajs.sbtplugin.Stage.FullOpt

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

//scalaJSStage in Global := FastOptStage
scalaJSStage in Global := FullOptStage

//crossVersion := ScalaJSCrossVersion.binary

topLevelDirectory := None

stagingDirectory := (target.value / "scala-2.11")

mappings in Universal ++= Seq((target.value / "scala-2.11" / s"${name.value}-opt.js", s"${name.value}.js"))

val packageTgz = taskKey[File]("package-tgz")
packageTgz := target.value / "universal" / (name.value + "-" + version.value + ".tgz")

artifact in (Universal, packageTgz) ~= { (art:Artifact) => art.copy(`type` = "tgz", extension = "tgz") }
addArtifact(artifact in (Universal, packageTgz), packageTgz in Universal)

publish <<= publish dependsOn (packageZipTarball in Universal)

publishM2 <<= publishM2 dependsOn (packageZipTarball in Universal)

publishLocal <<= publishLocal dependsOn (packageZipTarball in Universal)


val scalaJSVersion = ScalaJSVersions.current
val scalaJSIsSnapshotVersion = ScalaJSVersions.currentIsSnapshot
val scalaJSBinaryVersion = ScalaJSCrossVersion.currentBinaryVersion


//lazy val foo = crossProject.in(file(".")).
//  settings(
//        // other settings
//  ).
//  jvmSettings(
//        libraryDependencies += "com.lihaoyi" %% "scalatags" % "0.4.3"
//  ).
//  jsSettings(
//        libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.4.3"
//  )