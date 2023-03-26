resolvers += MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2")
resolvers += Resolver.url("HMRC-open-artefacts-ivy2", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(Resolver.ivyStylePatterns)

// newest version
addSbtPlugin("uk.gov.hmrc" % "sbt-auto-build" % "3.9.0")

// newest version
addSbtPlugin("uk.gov.hmrc" % "sbt-git-versioning" % "2.4.0")

// newest version
addSbtPlugin("uk.gov.hmrc" % "sbt-artifactory" % "2.0.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.26")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.15")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")

addSbtPlugin("uk.gov.hmrc" % "sbt-play-cross-compilation" % "2.3.0")
