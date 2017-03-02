package uk.gov.hmrc.payeestimator.services

import java.time.LocalDate

import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain.{Money, TaxYear_2016_2017}

class AnnualTaperingDeductionCalculatorSpec extends WordSpecLike with Matchers {

  "AnnualTaperingDeductionCalculator calculate()" should {
    "not calculate annual deductions if the tax code is not an emergency tax code" in {
      val result = AnnualTaperingDeductionCalculator("11L",  Money(100000), TaxYear_2016_2017).calculate()
      result.success shouldBe true
      result.isTapered shouldBe false
      result.result shouldBe "11L"
    }
    "calculate new annual deductions taxCode if the tax code is an emergency tax code" in {
      val result = AnnualTaperingDeductionCalculator("1100L",  Money(120000), TaxYear_2016_2017).calculate()
      result.success shouldBe true
      result.isTapered shouldBe true
      result.result shouldBe "100.00L"
    }
    "calculate annual deductions if the tax code is an emergency tax code, should trigger ZERO allowance" in {
      val result = AnnualTaperingDeductionCalculator("1100L",  Money(440000), TaxYear_2016_2017).calculate()
      result.success shouldBe true
      result.isTapered shouldBe true
      result.result shouldBe "ZERO"
    }
    "not calculate annual deductions if the gross pay is less than the annual income threshold" in {
      val result = AnnualTaperingDeductionCalculator("1100L",  Money(100000), TaxYear_2016_2017).calculate()
      result.success shouldBe true
      result.isTapered shouldBe false
      result.result shouldBe "1100L"
    }
  }
}
