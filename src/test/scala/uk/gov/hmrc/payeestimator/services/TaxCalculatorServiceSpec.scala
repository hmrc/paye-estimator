package uk.gov.hmrc.payeestimator.services

import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain.TaxBreakdown

class TaxCalculatorServiceSpec extends WordSpecLike with Matchers {

  "LiveTaxCalculatorService calculate tax for 2016 tax year" should {
    "return a annual TaxCalc response" in new LiveTaxCalcServiceSuccess {
      val result = service.calculateTax("false", date, "1100T", 1000008, "annual", -1)
      result shouldBe TaxCalculatorTestData.taxCalculator_2016_response
    }
    "return a annual TaxCalc response with zero value National Insurance Contributions Section" in new LiveTaxCalcServiceSuccess {
      val result = service.calculateTax("true", date, "1100T", 1000008, "annual", -1)
      result shouldBe TaxCalculatorTestData.no_NIC_Contribution_section_response
    }

    "return a NT TaxCalc response with no PAYE tax applied" in new LiveTaxCalcServiceSuccess {
      val result = service.calculateTax(false.toString, date, "NT", 20000000, "annual", -1)
      result shouldBe TaxCalculatorTestData.NT_taxCode_response
    }

    "return a BR TaxCalc response with PAYE tax applied at 20%" in new LiveTaxCalcServiceSuccess {
      val result = service.calculateTax(false.toString, date, "BR", 20000000, "annual", -1)
      result shouldBe TaxCalculatorTestData.BR_taxCode_response
    }

    "return a D0 TaxCalc response with PAYE tax applied at 40%" in new LiveTaxCalcServiceSuccess {
      val result = service.calculateTax(false.toString, date, "D0", 20000000, "annual", -1)
      result shouldBe TaxCalculatorTestData.D0_taxCode_response
    }

    "return a D1 TaxCalc response with PAYE tax applied at 45%" in new LiveTaxCalcServiceSuccess {
      val result = service.calculateTax(false.toString, date, "D1", 20000000, "annual", -1)
      result shouldBe TaxCalculatorTestData.D1_taxCode_response
    }

    "return weekly tax calc response using an hourly rate input" in new LiveTaxCalcServiceSuccess {
      val result = service.calculateTax(false.toString, date, "1100T", 9615, "weekly", 40)
      result shouldBe TaxCalculatorTestData.hour_rate_weekly_response
    }

    "return weekly tax calc response using tapering with emergency taxcode input" in new LiveTaxCalcServiceSuccess {
      val result = service.calculateTax(false.toString, date, "SK1100", 221200, "weekly", -1)
      result shouldBe TaxCalculatorTestData.tapering_emergency_code_response
    }

    "fail with exception max amount exceeded when greater than 999999999" in new LiveTaxCalcServiceSuccess {
      intercept[Exception] {
        service.calculateTax("false", date, "SK1100", 1999999999, "annual", -1)
      }
    }

    "max tax rate should kick in when the paye amount is greater than 50% of annual salary" in new LiveTaxCalcServiceSuccess {
      val result = service.calculateTax(false.toString, date, "K4000", 1000000, "annual", -1)
      result shouldBe TaxCalculatorTestData.max_tax_response
    }

    "max tax rate should kick in when the paye amount is greater than 50% of annual salary and round down for edge case where rounding increases the value to just over 50%" in new LiveTaxCalcServiceSuccess {
      val result = service.calculateTax(false.toString, date, "K1100", 100000, "annual", -1)
      result shouldBe TaxCalculatorTestData.max_tax_response_edge_case
    }
  }

  "LiveTaxCalculatorService annualiseGrossPay" should {
    "convert 12345600 to its Pound Value if the payPeriod is 'annual'" in new LiveTaxCalcServiceSuccess {
      val result = service.annualiseGrossPay(12345600, None, "annual")
      result.value shouldBe BigDecimal(123456.00)
    }

    "multiply 12345600 by 12 and convert to its Pound Value if the payPeriod is 'monthly'" in new LiveTaxCalcServiceSuccess {
      val result = service.annualiseGrossPay(12345600, None, "monthly")
      result.value shouldBe BigDecimal(12345600 * 12 / 100)
    }

    "multiply 12345600 by 52 and convert to its Pound Value if the payPeriod is 'weekly'" in new LiveTaxCalcServiceSuccess {
      val result = service.annualiseGrossPay(12345600, None, "weekly")
      result.value shouldBe BigDecimal(12345600*52/100)
    }

    "throw an exception when the amount exceeds 999999999 regardless of the pay period" in new LiveTaxCalcServiceSuccess {
      intercept[Exception] {
        service.annualiseGrossPay(1999999999, None, "weekly")
      }
      intercept[Exception] {
        service.annualiseGrossPay(1999999999, None, "monthly")
      }
      intercept[Exception] {
        service.annualiseGrossPay(1999999999, None, "annual")
      }
    }
  }

  "LiveTaxCalculatorService convertToBoolean" should {
    "convert String value to its boolean equivalent" in new LiveTaxCalcServiceSuccess {
      service.convertToBoolean("true") shouldBe true
      service.convertToBoolean("TRUE") shouldBe true
      service.convertToBoolean("false") shouldBe false
      service.convertToBoolean("FALSE") shouldBe false
      intercept[Exception] {
        service.convertToBoolean("notA_Boolean_String")
      }
    }
  }

  "LiveTaxCalculatorService calculateAverageAnnualTaxRate" should {
    "calculate the average annual tax rate" in new LiveTaxCalcServiceSuccess {

      val tbWeek = Option(TaxBreakdown("weekly",grossPay = BigDecimal(1000.00), BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0), Seq(), totalDeductions = BigDecimal(200.00), BigDecimal(0)))
      val tbMonth = Option(TaxBreakdown("monthly",grossPay = BigDecimal(7000.00), BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0), Seq(), totalDeductions = BigDecimal(2000.00), BigDecimal(0)))
      val tbAnnual = Option(TaxBreakdown("annual",grossPay = BigDecimal(60000.00), BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0), Seq(), totalDeductions = BigDecimal(24000.00), BigDecimal(0)))

      service.calculateAverageAnnualTaxRate(tbWeek).value shouldBe BigDecimal(20.00)
      service.calculateAverageAnnualTaxRate(tbMonth).value shouldBe BigDecimal(28.57)
      service.calculateAverageAnnualTaxRate(tbAnnual).value shouldBe BigDecimal(40.00)
      service.calculateAverageAnnualTaxRate(None).value shouldBe BigDecimal(0.00)
    }
  }
}
