package uk.gov.hmrc.payeestimator.services

import java.time.LocalDate

import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain.Money

class AnnualTaperingDeductionCalculatorSpec extends WordSpecLike with Matchers {

  "AnnualTaperingDeductionCalculator calculate()" should {
    "not calculate annual deductions if the tax code is not an emergency tax code" in {
      val result = AnnualTaperingDeductionCalculator("11L", LocalDate.now, Money(100000)).calculate()
      result.success shouldBe true
      result.isTapered shouldBe false
      result.result shouldBe "11L"
    }
    "calculate new annual deductions taxCode if the tax code is an emergency tax code" in {
      val result = AnnualTaperingDeductionCalculator("1100L", LocalDate.now, Money(120000)).calculate()
      result.success shouldBe true
      result.isTapered shouldBe true
      result.result shouldBe "100.00L"
    }
    "calculate annual deductions if the tax code is an emergency tax code, should trigger ZERO allowance" in {
      val result = AnnualTaperingDeductionCalculator("1100L", LocalDate.now, Money(440000)).calculate()
      result.success shouldBe true
      result.isTapered shouldBe true
      result.result shouldBe "ZERO"
    }
    "not calculate annual deductions if the gross pay is less than the annual income threshold" in {
      val result = AnnualTaperingDeductionCalculator("1100L", LocalDate.now, Money(100000)).calculate()
      result.success shouldBe true
      result.isTapered shouldBe false
      result.result shouldBe "1100L"
    }
  }
}
