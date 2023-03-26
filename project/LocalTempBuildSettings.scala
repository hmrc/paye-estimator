/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import scala.util.matching.Regex
import org.eclipse.jgit.lib.{BranchConfig, Repository}
import sbt.Keys._
import sbt._

trait License {
  def apply(yyyy: String, copyrightOwner: String, commentStyle: String = "*"): (Regex, String) = {
    val text = createLicenseText(yyyy, copyrightOwner)
    (new Regex(""), "")
  }

  def createLicenseText(yyyy: String, copyrightOwner: String): String
}

object Apache2_0 extends License {
  override def createLicenseText(yyyy: String, copyrightOwner: String) = {
    s"""|Copyright $yyyy $copyrightOwner
        |
        |Licensed under the Apache License, Version 2.0 (the "License");
        |you may not use this file except in compliance with the License.
        |You may obtain a copy of the License at
        |
        |    http://www.apache.org/licenses/LICENSE-2.0
        |
        |Unless required by applicable law or agreed to in writing, software
        |distributed under the License is distributed on an "AS IS" BASIS,
        |WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        |See the License for the specific language governing permissions and
        |limitations under the License.
        |""".stripMargin
  }
}

object LocalTempBuildSettings extends AutoPlugin {

  import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._
  import uk.gov.hmrc.SbtBuildInfo
  import uk.gov.hmrc.DefaultBuildSettings._

  val localDefaultSettings: Seq[Setting[_]] =
    scalaSettings ++
      SbtBuildInfo() ++
      defaultSettings(false) ++
      PublishSettings() ++
      Resolvers() ++
      ArtefactDescription() ++
      Seq(
        targetJvm := "jvm-1.8"
        // headers := HeaderSettings()
      )
}

object Resolvers {

  val HmrcReleasesRepo = Resolver.bintrayRepo("hmrc", "releases")

  def apply() =
    resolvers := Seq(
      Opts.resolver.sonatypeReleases,
      Resolver.typesafeRepo("releases"),
      HmrcReleasesRepo
    )
}

object PublishSettings {
  def apply() = Seq(
    publishArtifact := true,
    publishArtifact in Test := false,
    publishArtifact in IntegrationTest := false,
    publishArtifact in(Test, packageDoc) := false,
    publishArtifact in(Test, packageSrc) := false,
    publishArtifact in(IntegrationTest, packageDoc) := false,
    publishArtifact in(IntegrationTest, packageSrc) := false
  )
}

object HeaderSettings {
  import org.joda.time.DateTime

  val copyrightYear = DateTime.now().getYear.toString
  val copyrightOwner = "HM Revenue & Customs"

  // The support for this type of mapping has been dropped
  // headerLicense := Some(HeaderLicense.MIT("2015", "Heiko Seeberger"))
  def apply() = {
    Map(
      "scala" -> Apache2_0(copyrightYear, copyrightOwner),
      "conf" -> Apache2_0(copyrightYear, copyrightOwner, "#")
    )
  }
}

object ArtefactDescription {

  private val logger = ConsoleLogger()

  def apply() = Seq(
    homepage := Git.homepage,
    organizationHomepage := Some(url("https://www.gov.uk/government/organisations/hm-revenue-customs")),
    scmInfo := buildScmInfo,

    // workaround for sbt/sbt#1834
    pomPostProcess := {

      import scala.xml.transform.{RewriteRule, RuleTransformer}
      import scala.xml.{Node => XmlNode, NodeSeq => XmlNodeSeq, _}

      (node: XmlNode) =>
        new RuleTransformer(new RewriteRule {
          override def transform(node: XmlNode): XmlNodeSeq = node match {
            case e: Elem if e.label == "developers" =>
              <developers>
                {developers.value.map { dev =>
                <developer>
                  <id>{dev.id}</id>
                  <name>{dev.name}</name>
                  <email>{dev.email}</email>
                  <url>{dev.url}</url>
                </developer>
              }}
              </developers>
            case _ => node
          }
        }).transform(node).head
    }
  )

  def buildScmInfo:Option[ScmInfo]={
    for(connUrl <- Git.findRemoteConnectionUrl;
        browserUrl <- Git.browserUrl)
        yield ScmInfo(url(browserUrl), connUrl)
  }
}

object Git extends Git {
  override lazy val repository: Repository = {
    import org.eclipse.jgit.storage.file.FileRepositoryBuilder
    val builder = new FileRepositoryBuilder
    builder.findGitDir.build
  }
}

trait Git {
  val logger = ConsoleLogger()
  val repository: Repository
  lazy val config = repository.getConfig

  def homepage: Option[URL] = browserUrl map url

  def browserUrl: Option[String] = {
    findRemoteConnectionUrl map browserUrl
  }

  def findRemoteConnectionUrl: Option[String] = {
    val currentBranchUrl = getUrlForBranch(repository.getBranch())

    val url = currentBranchUrl.orElse(getUrlForBranch("master")).orElse(getUrlForRemote("origin"))

    url.map { originUrl =>
      val gitTcpRex = "^(git:\\/\\/)".r
      gitTcpRex.replaceFirstIn(originUrl, "git@")
    }
  }

  private def getUrlForBranch(name: String) = {
    val branchConfig = new BranchConfig(config, name)
    getUrlForRemote(branchConfig.getRemote)
  }

  private def getUrlForRemote(name: String) = {
    Option(config.getString("remote", name, "url"))
  }

  private def browserUrl(remoteConnectionUrl: String): String = {
    val removedProtocol = removeProtocol(remoteConnectionUrl)
    val replacedSeparator = removedProtocol.toLowerCase.replaceFirst(":", "/")
    val removedGitSuffix = replacedSeparator.replaceFirst(".git$", "")
    s"https://$removedGitSuffix"
  }

  private def removeProtocol(connectionUrl: String): String = {
    "^(git@|git://|https://)".r.replaceFirstIn(connectionUrl, "")
  }
}
