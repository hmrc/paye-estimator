resolvers ++= Seq(Resolver.url("hmrc-sbt-plugin-releases", url("https://dl.bintray.com/hmrc/sbt-plugin-releases"))(Resolver.ivyStylePatterns),
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/")

//addSbtPlugin("uk.gov.hmrc" % "sbt-auto-build" % "1.4.0")

addSbtPlugin("uk.gov.hmrc" % "sbt-git-versioning" % "0.9.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.15")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.3.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.4")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")

//TODO : revert comment sbt-auto-build & delete the below plugins once ScalaTest have fixed '-u' option for Scala-js
addSbtPlugin("uk.gov.hmrc" % "sbt-settings" % "3.3.0")

addSbtPlugin("de.heikoseeberger" % "sbt-header" % "1.6.0")

addSbtPlugin("uk.gov.hmrc" % "sbt-git-versioning" % "0.9.0")