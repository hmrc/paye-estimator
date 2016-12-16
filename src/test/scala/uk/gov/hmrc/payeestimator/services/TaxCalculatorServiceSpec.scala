package uk.gov.hmrc.payeestimator.services

import org.scalatest.{Matchers, WordSpecLike}

class TaxCalculatorServiceSpec extends WordSpecLike with Matchers {

  "LiveTaxCalculatorController calculate tax for 2016 tax year" should {
    "return a annual TaxCalc response" in new LiveTaxCalcServiceSuccess {
      val result = service.calculateTax("false", 2016, "1100T", 1000008, "annual", -1)
      result shouldBe TaxCalculatorTestData.taxCalculator_2016_response
    }
    "return a annual TaxCalc response with zero value National Insurance Contributions Section" in new LiveTaxCalcServiceSuccess {
      val result = service.calculateTax("true", 2016, "1100T", 1000008, "annual", -1)
      result shouldBe TaxCalculatorTestData.no_NIC_Contribution_section_response
    }

    "return a NT TaxCalc response with no PAYE tax applied" in new LiveTaxCalcServiceSuccess {
      val result = service.calculateTax(false.toString, 2016, "NT", 20000000, "annual", -1)
      result shouldBe TaxCalculatorTestData.NT_taxCode_response
    }

    "return a BR TaxCalc response with PAYE tax applied at 20%" in new LiveTaxCalcServiceSuccess {
      val result = service.calculateTax(false.toString, 2016, "BR", 20000000, "annual", -1)
      result shouldBe TaxCalculatorTestData.BR_taxCode_response
    }

    "return a D0 TaxCalc response with PAYE tax applied at 40%" in new LiveTaxCalcServiceSuccess {
      val result = service.calculateTax(false.toString, 2016, "D0", 20000000, "annual", -1)
      result shouldBe TaxCalculatorTestData.D0_taxCode_response
    }

    "return a D1 TaxCalc response with PAYE tax applied at 45%" in new LiveTaxCalcServiceSuccess {
      val result = service.calculateTax(false.toString, 2016, "D1", 20000000, "annual", -1)
      result shouldBe TaxCalculatorTestData.D1_taxCode_response
    }

    "return weekly tax calc response using an hourly rate input" in new LiveTaxCalcServiceSuccess {
      val result = service.calculateTax(false.toString, 2016, "1100T", 9615, "weekly", 40)
      result shouldBe TaxCalculatorTestData.hour_rate_weekly_response
    }

    "return weekly tax calc response using tapering with emergency taxcode input" in new LiveTaxCalcServiceSuccess {
      val result = service.calculateTax(false.toString, 2016, "SK1100", 221200, "weekly", -1)
      println(result)
      result shouldBe TaxCalculatorTestData.tapering_emergency_code_response
    }

    "fail with exception max amount exceeded when greater than 999999999" in new LiveTaxCalcServiceSuccess {
      intercept[Exception] {
        service.calculateTax("false", 2016, "SK1100", 1999999999, "annual", -1)
      }
    }

    "max tax rate should kick in when the paye amount is greater than 50% of annual salary" in new LiveTaxCalcServiceSuccess {
      val result = service.calculateTax(false.toString, 2016, "K4000", 1000000, "annual", -1)
      result shouldBe TaxCalculatorTestData.max_tax_response
    }
  }

}
