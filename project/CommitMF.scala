import org.eclipse.jgit.api.{Git => JGit}
import org.eclipse.jgit.lib.Constants._
import org.eclipse.jgit.lib.{ObjectId, Repository}
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.joda.time.format.ISODateTimeFormat._


import scala.collection.JavaConversions._

class CommitMF(sha : String, author : String, date : String) {

  override def toString: String =
    s"""sha=$sha
      |author=$author
      |date=$date""".stripMargin
}

object CommitMF {

  def repository = new FileRepositoryBuilder().readEnvironment.findGitDir.build

  def apply(): CommitMF = apply(repository)

  def apply(repository: Repository): CommitMF = {
    val git = new JGit(repository)
    val headId = repository.getRef(HEAD).getObjectId
    val headIdStr = ObjectId.toString(headId)
    val headRev = headCommit(git, headId)

    new CommitMF(headIdStr, commitAuthorName(headRev), commitDateTime(headRev))
  }

  private def commitDateTime(headRev: Option[RevCommit]): String = {
    dateTime.print(headRev.map(_.getCommitTime.toLong * 1000).getOrElse(0L))
  }

  private def commitAuthorName(headRev: Option[RevCommit]): String = {
    headRev.map(_.getCommitterIdent.getName).getOrElse("")
  }

  private def headCommit(git: JGit, headId: ObjectId): Option[RevCommit] = {
    git.log().add(headId).setMaxCount(1).call().toSeq.headOption
  }
}