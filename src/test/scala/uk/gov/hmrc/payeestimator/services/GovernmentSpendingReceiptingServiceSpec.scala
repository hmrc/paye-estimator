package uk.gov.hmrc.payeestimator.services

import org.scalatest.{Matchers, WordSpecLike}

class GovernmentSpendingReceiptingServiceSpec extends WordSpecLike with Matchers {

  "LiveGovernmentSpendingReceiptingService getGovernmentReceiptingData " should {
    "return a valid GovernmentReceiptingDataResponse" in new LiveGovernmentSpendingReceiptingServiceSuccess {
      val result = service.getGovernmentReceiptingData()
      println(result)
      result shouldBe GovernmentSpendingReceiptingTestData.government_receipting_data_response
    }
  }

  "LiveGovernmentSpendingReceiptingService getGovernmentSpendingData " should {
    "return a valid GovernmentSpendingDataResponse" in new LiveGovernmentSpendingReceiptingServiceSuccess {
      val result = service.getGovernmentSpendingData()
      result shouldBe GovernmentSpendingReceiptingTestData.government_spending_data_response
    }
  }
}
