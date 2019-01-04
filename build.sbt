import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import uk.gov.hmrc.SbtArtifactory

enablePlugins(SbtGitVersioning, ScalaJSPlugin, UniversalPlugin, SbtArtifactory)

name := "paye-estimator"

LocalTempBuildSettings.localDefaultSettings

majorVersion := 2

scalaVersion := "2.11.12"

crossScalaVersions := Seq("2.11.12")

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-java-time" % "0.2.0",
  "com.lihaoyi" %%% "upickle" % "0.4.3",
  Dependencies.tests.scalajsenvs,
  Dependencies.tests.scalatest,
  Dependencies.tests.playJson,
  "org.pegdown" % "pegdown" % "1.6.0"
)

makePublicallyAvailableOnBintray := true

scalaJSStage in Global := FastOptStage

topLevelDirectory := None

// generate commit.mf file for non-standard builds (see https://github.com/hmrc/releaser#additional-filesnon-standard-artefacts)
resourceGenerators in Compile <+= Def.task {
  val commitMfFile = target.value / "commit.mf"
  IO.write(commitMfFile, CommitMF().toString)
  Seq(commitMfFile)
}

val commitMfTask = taskKey[File]("commit-mf")
commitMfTask := target.value / "commit.mf"
artifact in(Compile, commitMfTask) ~= { art: Artifact =>
  art.copy("commit", "mf", "mf")
}
addArtifact(artifact in(Compile, commitMfTask), commitMfTask in Compile)

stagingDirectory := (target.value / "scala-2.11")

mappings in Universal ++= Seq((target.value / "scala-2.11" / s"${name.value}-opt.js", s"${name.value}.js"))

val packageTgz = taskKey[File]("package-tgz")
packageTgz := target.value / "universal" / (name.value + "-" + version.value + ".tgz")

artifact in(Universal, packageTgz) ~= { art: Artifact => art.copy(`type` = "tgz", extension = "tgz") }
addArtifact(artifact in(Universal, packageTgz), packageTgz in Universal)


publish <<= publish dependsOn (packageZipTarball in Universal)

publishM2 <<= publishM2 dependsOn (packageZipTarball in Universal)

publishLocal <<= publishLocal dependsOn (packageZipTarball in Universal)
