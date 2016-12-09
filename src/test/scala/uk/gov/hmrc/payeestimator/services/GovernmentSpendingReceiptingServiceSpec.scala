package uk.gov.hmrc.payeestimator.services

import org.scalatest.{Matchers, WordSpecLike}

class GovernmentSpendingReceiptingServiceSpec extends WordSpecLike with Matchers {

  "LiveGovernmentSpendingReceiptingService do dah day " should {
    "return a response" in new LiveGovernmentSpendingReceiptingServiceSuccess {
      val result = service.getGovernmentReceiptingData()
      println("########################  " + result)
      result shouldBe TaxCalculatorTestData.taxCalculator_2016_response
    }
  }

}
