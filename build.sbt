import PlayCrossCompilation._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import uk.gov.hmrc.SbtArtifactory

enablePlugins(SbtGitVersioning, ScalaJSPlugin, UniversalPlugin, SbtArtifactory)

makePublicallyAvailableOnBintray := true

name := "paye-estimator"

LocalTempBuildSettings.localDefaultSettings

majorVersion := 2

scalaVersion := "2.11.12"

crossScalaVersions := Seq("2.11.12")

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-java-time" % "0.2.0",
  "com.lihaoyi" %%% "upickle" % "0.4.3"
) ++ Dependencies()

playCrossCompilationSettings

makePublicallyAvailableOnBintray := true

scalaJSStage in Global := FullOptStage

topLevelDirectory := None

stagingDirectory := (target.value / "scala-2.11")

mappings in Universal ++= Seq((target.value / "scala-2.11" / s"${name.value}-opt.js", s"${name.value}.js"))

val packageTgz = taskKey[File]("package-tgz")
packageTgz := target.value / "universal" / (name.value + "-" + version.value + ".tgz")

artifact in(Universal, packageTgz) ~= { art: Artifact => art.copy(`type` = "tgz", extension = "tgz") }
addArtifact(artifact in(Universal, packageTgz), packageTgz in Universal)


publishAndDistribute := (publishAndDistribute dependsOn (fullOptJS in Compile)).value

publish <<= publish dependsOn (packageZipTarball in Universal)

publishM2 <<= publishM2 dependsOn (packageZipTarball in Universal)

publishLocal <<= publishLocal dependsOn (packageZipTarball in Universal)
