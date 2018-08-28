resolvers ++= Seq(Resolver.url("hmrc-sbt-plugin-releases", url("https://dl.bintray.com/hmrc/sbt-plugin-releases"))(Resolver.ivyStylePatterns),
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "HMRC Releases" at "https://dl.bintray.com/hmrc/releases")

//addSbtPlugin("uk.gov.hmrc" % "sbt-auto-build" % "1.4.0")

addSbtPlugin("uk.gov.hmrc" % "sbt-git-versioning" % "1.9.0")

addSbtPlugin("uk.gov.hmrc" % "sbt-artifactory" % "0.12.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.14")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.4")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")

//TODO : revert comment sbt-auto-build & delete the below plugins once ScalaTest have fixed '-u' option for Scala-js
addSbtPlugin("uk.gov.hmrc" % "sbt-settings" % "3.3.0")

addSbtPlugin("de.heikoseeberger" % "sbt-header" % "1.6.0")