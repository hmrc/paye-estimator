package uk.gov.hmrc.payeestimator.services

import java.net.URL
import java.time.LocalDate

import org.scalatest.{DiagrammedAssertions, Matchers, WordSpecLike}
import play.api.libs.json.Json
import play.api.libs.json.Json.{format, parse}
import uk.gov.hmrc.payeestimator.domain._
import uk.gov.hmrc.payeestimator.services.Formats.taxCalcFormat

import scala.io.Source.fromURL

class TaxCalculatorServiceSpec extends WordSpecLike with Matchers with DiagrammedAssertions with TaxYearChanges {

  val taxYear_2017_2018 = TaxYear_2017_2018()
  val scottish_TaxYear_2018_2017 = TaxYear_2017_2018(true)
  val taxYear_2018_2019 = TaxYear_2018_2019()
  val scottish_TaxYear_2018_2019 = TaxYear_2018_2019(true)

  private def taxCalcFromJson(json: String): TaxCalc = {
    val resource: URL = getClass.getResource(json)
    parse(fromURL(resource).getLines().mkString).as[TaxCalc](taxCalcFormat)
  }

  import org.scalatest.prop.TableDrivenPropertyChecks._

  val input = Table(
    ("Test Description", "isStatePensionAge", "taxCalcResource", "taxCode", "grossPayPence", "payPeriod", "hoursIn", "expectedJson"),

    ("return a annual TaxCalc response",
      "false", taxYear_2017_2018, "1100T", 1000000, "annual", -1, "/data/2017_TaxCalcResponse.json"),
    ("return a annual TaxCalc response with zero value National Insurance Contributions Section",
      "true", taxYear_2017_2018, "1100T", 1000000, "annual", -1, "/data/2017_no_NIC_Contribution_section_response.json"),
    ("return a NT TaxCalc response with no PAYE tax applied",
      "false", taxYear_2017_2018, "NT", 20000000, "annual", -1, "/data/2017_NT_TaxCode_Response.json"),
    ("return a BR TaxCalc response with PAYE tax applied at 20%",
      "false", taxYear_2017_2018, "BR", 20000000, "annual", -1, "/data/2017_BR_TaxCode_Response.json"),
    ("return a D0 TaxCalc response with PAYE tax applied at 40%",
      "false", taxYear_2017_2018, "D0", 20000000, "annual", -1, "/data/2017_D0_TaxCode_Response.json"),
    ("return a D1 TaxCalc response with PAYE tax applied at 45%",
      "false", taxYear_2017_2018, "D1", 20000000, "annual", -1, "/data/2017_D1_TaxCode_Response.json"),
    ("return tax calc response using an hourly rate input",
      "false", taxYear_2017_2018, "1100T", 9615, "annual", 40, "/data/2017_Hourly_Rate_Response.json"),
    ("return tax calc response using tapering with emergency tax code input",
      "false", taxYear_2017_2018, "1150L", 221200, "weekly", -1, "/data/2017_Tapering_Emergency_TaxCode_Response.json"),
    ("max tax rate should kick in when the paye amount is greater than 50% of annual salary",
      "false", taxYear_2017_2018, "K4000", 1000000, "annual", -1, "/data/2017_Max_Tax_Response.json"),
    ("max tax rate should kick in when the paye amount is greater than 50% of annual salary and round down for edge case where rounding increases the value to just over 50%",
      "false", taxYear_2017_2018, "K1100", 100000, "annual", -1, "/data/2017_Max_Tax_Edge_Case_Response.json"),

    ("return a annual TaxCalc response",
      "false", scottish_TaxYear_2018_2017, "S1100T", 1000000, "annual", -1, "/data/2017_Scottish_TaxCalcResponse.json"),
    ("return a annual TaxCalc response with zero value National Insurance Contributions Section",
      "true", scottish_TaxYear_2018_2017, "1100T", 1000000, "annual", -1, "/data/2017_Scottish_no_NIC_Contribution_section_response.json"),
    ("return a SBR TaxCalc response with PAYE tax applied at 20%",
      "false", scottish_TaxYear_2018_2017, "SBR", 20000000, "annual", -1, "/data/2017_SBR_TaxCode_Response.json"),
    ("return a SD0 TaxCalc response with PAYE tax applied at 40%",
      "false", scottish_TaxYear_2018_2017, "SD0", 20000000, "annual", -1, "/data/2017_SD0_TaxCode_Response.json"),
    ("return a SD1 TaxCalc response with PAYE tax applied at 45%",
      "false", scottish_TaxYear_2018_2017, "SD1", 20000000, "annual", -1, "/data/2017_SD1_TaxCode_Response.json"),
    ("return tax calc response using an hourly rate input",
      "false", scottish_TaxYear_2018_2017, "S1100T", 9615, "annual", 40, "/data/2017_Scottish_Hourly_Rate_Response.json"),
    ("return tax calc response using tapering with emergency tax code input",
      "false", scottish_TaxYear_2018_2017, "S1150L", 221200, "weekly", -1, "/data/2017_Scottish_Tapering_Emergency_TaxCode_Response.json"),
    ("max tax rate should kick in when the paye amount is greater than 50% of annual salary",
      "false", scottish_TaxYear_2018_2017, "SK4000", 1000000, "annual", -1, "/data/2017_Scottish_Max_Tax_Response.json"),
    ("max tax rate should kick in when the paye amount is greater than 50% of annual salary and round down for edge case where rounding increases the value to just over 50%",
      "false", scottish_TaxYear_2018_2017, "SK1100", 100000, "annual", -1, "/data/2017_Scottish_Max_Tax_Edge_Case_Response.json"),



    ("return a annual TaxCalc response",
      "false", taxYear_2018_2019, "1100T", 1000008, "annual", -1, "/data/2018_TaxCalcResponse.json"),
    ("return a annual TaxCalc response with zero value National Insurance Contributions Section",
      "true", taxYear_2018_2019, "1100T", 1000008, "annual", -1, "/data/2018_no_NIC_Contribution_section_response.json"),
    ("return a NT TaxCalc response with no PAYE tax applied",
      "false", taxYear_2018_2019, "nt", 20000000, "annual", -1, "/data/2018_NT_TaxCode_Response.json"),
    ("return a BR TaxCalc response with PAYE tax applied at 20%",
      "false", taxYear_2018_2019, "BR", 20000000, "annual", -1, "/data/2018_BR_TaxCode_Response.json"),
    ("return a D0 TaxCalc response with PAYE tax applied at 40%",
      "false", taxYear_2018_2019, "D0", 20000000, "annual", -1, "/data/2018_D0_TaxCode_Response.json"),
    ("return a D1 TaxCalc response with PAYE tax applied at 45%",
      "false", taxYear_2018_2019, "D1", 20000000, "annual", -1, "/data/2018_D1_TaxCode_Response.json"),
    ("return tax calc response using an hourly rate input",
      "false", taxYear_2018_2019, "1100T", 9615, "annual", 40, "/data/2018_Hourly_Rate_Response.json"),
    ("max tax rate should kick in when the paye amount is greater than 50% of annual salary",
      "false", taxYear_2018_2019, "K4000", 1000000, "annual", -1, "/data/2018_Max_Tax_Response.json"),
    ("max tax rate should kick in when the paye amount is greater than 50% of annual salary and round down for edge case where rounding increases the value to just over 50%",
      "false", taxYear_2018_2019, "K1100", 100000, "annual", -1, "/data/2018_Max_Tax_Edge_Case_Response.json"),


    ("return a annual TaxCalc response",
        "false", scottish_TaxYear_2018_2019, "S1100T", 1000000, "annual", -1, "/data/2018_Scottish_TaxCalcResponse.json"),
    ("return a annual TaxCalc response with zero value National Insurance Contributions Section",
      "true", scottish_TaxYear_2018_2019, "S1100T", 1000000, "annual", -1, "/data/2018_Scottish_no_NIC_Contribution_section_response.json"),
    ("return a SBR TaxCalc response with PAYE tax applied at 20%",
      "false", scottish_TaxYear_2018_2019, "SBR", 20000000, "annual", -1, "/data/2018_SBR_TaxCode_Response.json"),
    ("return a SD0 TaxCalc response with PAYE tax applied at 21%",
      "false", scottish_TaxYear_2018_2019, "SD0", 20000000, "annual", -1, "/data/2018_SD0_TaxCode_Response.json"),
    ("return a SD1 TaxCalc response with PAYE tax applied at 41%",
      "false", scottish_TaxYear_2018_2019, "SD1", 20000000, "annual", -1, "/data/2018_SD1_TaxCode_Response.json"),
    ("return a SD2 TaxCalc response with PAYE tax applied at 46%",
      "false", scottish_TaxYear_2018_2019, "SD2", 20000000, "annual", -1, "/data/2018_SD2_TaxCode_Response.json"),
    ("return tax calc response using an hourly rate input",
      "false", scottish_TaxYear_2018_2019, "S1100T", 9615, "annual", 40, "/data/2018_Scottish_Hourly_Rate_Response.json"),
    ("return tax calc response using tapering with emergency tax code input",
      "false", scottish_TaxYear_2018_2019, "S1185L", 221200, "weekly", -1, "/data/2018_Scottish_Tapering_Emergency_TaxCode_Response.json"),
    ("max tax rate should kick in when the paye amount is greater than 50% of annual salary",
      "false", scottish_TaxYear_2018_2019, "SK4000", 1000000, "annual", -1, "/data/2018_Scottish_Max_Tax_Response.json"),
    ("max tax rate should kick in when the paye amount is greater than 50% of annual salary and round down for edge case where rounding increases the value to just over 50%",
      "false", scottish_TaxYear_2018_2019, "SK1185", 100000, "annual", -1, "/data/2018_Scottish_Max_Tax_Edge_Case_Response.json")
  )

  "LiveTaxCalculatorService calculate tax" should {
    forAll(input) {

      (testDescription, isStatePensionAge, taxCalcResource, taxCode, grossPayPence, payPeriod, hoursIn, expectedJson) =>

        s"${taxCalcResource.taxYear} $testDescription" in new LiveTaxCalcServiceSuccess {

          val result = service.buildTaxCalc(isStatePensionAge, taxCalcResource, taxCode, grossPayPence, payPeriod, hoursIn)

          val expected = taxCalcFromJson(expectedJson)

          result shouldBe expected
        }
    }

  }

  "fail with exception max amount exceeded when greater than 999999999" in new LiveTaxCalcServiceSuccess {
    intercept[Exception] {
      service.buildTaxCalc("false", taxYear_2018_2019, "SK1100", 1999999999, "annual", -1)
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
      result.value shouldBe BigDecimal(12345600 * 52 / 100)
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

      val tbWeek = Option(TaxBreakdown("weekly", grossPay = BigDecimal(1000.00), BigDecimal(0), BigDecimal(0), BigDecimal(0), Option(BigDecimal(0)), BigDecimal(0), Seq(), totalDeductions = BigDecimal(200.00), BigDecimal(0)))
      val tbMonth = Option(TaxBreakdown("monthly", grossPay = BigDecimal(7000.00), BigDecimal(0), BigDecimal(0), BigDecimal(0), Option(BigDecimal(0)), BigDecimal(0), Seq(), totalDeductions = BigDecimal(2000.00), BigDecimal(0)))
      val tbAnnual = Option(TaxBreakdown("annual", grossPay = BigDecimal(60000.00), BigDecimal(0), BigDecimal(0), BigDecimal(0), Option(BigDecimal(0)), BigDecimal(0), Seq(), totalDeductions = BigDecimal(24000.00), BigDecimal(0)))

      service.calculateAverageAnnualTaxRate(tbWeek).value shouldBe BigDecimal(20.00)
      service.calculateAverageAnnualTaxRate(tbMonth).value shouldBe BigDecimal(28.57)
      service.calculateAverageAnnualTaxRate(tbAnnual).value shouldBe BigDecimal(40.00)
      service.calculateAverageAnnualTaxRate(None).value shouldBe BigDecimal(0.00)
    }
  }

  "LiveTaxCalculatorService parseDate" should {
    "parse a Date String yyyy-MM-dd to a LocalDate" in new LiveTaxCalcServiceSuccess {
      LocalDate.of(2017, 3, 8) shouldBe service.parseDate("2017-03-08")
    }
  }
}


object Formats {
  implicit val taxCalcFormatAggregation = format[Aggregation]
  implicit val taxCalcFormatTaxCategory = format[TaxCategory]
  implicit val taxCalcFormatBreakdown = format[TaxBreakdown]
  implicit val taxCalcFormat = format[TaxCalc]
}

