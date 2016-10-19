import sbt.Keys._
import sbt._

object TestSettings {

  lazy val ItTest = config("it") extend Test

  val testSettings = Seq(
    // Configure it test not to run ItTest tag
    testOptions in Test := Seq(Tests.Argument(
      TestFrameworks.ScalaTest,
      "-l",
      "uk.gov.hmrc.tags.ItTestTag"))
  )

  val itTestSettings = Seq(
    // You might consider not to perform parallel integration testing
    parallelExecution in ItTest  := false,
    testOptions in ItTest := Seq(
      // Configure mock-server start and stop hooks
      Tests.Setup { _ =>
        // Start your fancy integration server here
      },
      Tests.Cleanup { _ =>
        // Stop your fancy integration server here
      },
      // Configure it test to run only ItTest tag
      Tests.Argument(
        TestFrameworks.ScalaTest,
        "-n",
        "uk.gov.hmrc.tags.ItTestTag"))
  )

}