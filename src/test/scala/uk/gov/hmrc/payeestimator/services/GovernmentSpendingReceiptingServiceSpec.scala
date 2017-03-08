package uk.gov.hmrc.payeestimator.services

import org.scalatest.{Matchers, WordSpecLike}
import play.api.libs.json.Json
import uk.gov.hmrc.payeestimator.domain.{GovernmentReceipt, GovernmentReceiptDataResponse, GovernmentSpending, GovernmentSpendingDataResponse}

import scala.io.Source

class GovernmentSpendingReceiptingServiceSpec extends WordSpecLike with Matchers {

  "LiveGovernmentSpendingReceiptingService getGovernmentReceiptingData " should {
    "return a valid GovernmentReceiptingDataResponse" in new LiveGovernmentSpendingReceiptingServiceSuccess {
      import GovernmentSpendingFormats._
      val result = service.buildGovernmentReceiptingData
      val expected = Json.parse(Source.fromURL(getClass.getResource("/data/governmentReceiptingData.json")).getLines().mkString).as[GovernmentReceiptDataResponse](governmentReceiptDataResponseFormat)
      result shouldBe expected
    }
  }

  "LiveGovernmentSpendingReceiptingService getGovernmentSpendingData " should {
    "return a valid GovernmentSpendingDataResponse" in new LiveGovernmentSpendingReceiptingServiceSuccess {
      import GovernmentSpendingFormats._
      val result = service.buildGovernmentSpendingData
      val expected = Json.parse(Source.fromURL(getClass.getResource("/data/governmentSpendingData.json")).getLines().mkString).as[GovernmentSpendingDataResponse](governmentSpendingDataResponseFormat)
      result shouldBe expected
    }
  }
}

object GovernmentSpendingFormats {
  implicit val governmentReceiptFormat = Json.format[GovernmentReceipt]
  implicit val governmentReceiptDataResponseFormat = Json.format[GovernmentReceiptDataResponse]
  implicit val governmentSpendingFormat = Json.format[GovernmentSpending]
  implicit val governmentSpendingDataResponseFormat = Json.format[GovernmentSpendingDataResponse]
}
