package uk.gov.hmrc.payeestimator.services

import java.net.URL
import java.time.LocalDate

import org.scalatest.{DiagrammedAssertions, Matchers, WordSpecLike}
import play.api.libs.json.Json.{format, parse}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.payeestimator.domain._
import uk.gov.hmrc.payeestimator.services.Formats.taxCalcFormat

import scala.io.Source.fromURL

class TaxCalculatorServiceSpec extends WordSpecLike with Matchers with DiagrammedAssertions with TaxYearChanges {

  val taxYear_2017_2018 = TaxYear_2017_2018()
  val taxYear_2018_2019 = TaxYear_2018_2019()
  val taxYear_2019_2020 = TaxYear_2019_2020()

  val scottish_TaxYear_2017_2018 = TaxYear_2017_2018(isScottish = true)
  val scottish_TaxYear_2018_2019 = TaxYear_2018_2019(isScottish = true)
  val scottish_TaxYear_2019_2020 = TaxYear_2019_2020(isScottish = true)

  private def taxCalcFromJson(json: String): TaxCalc = {
    val resource: URL = getClass.getResource(json)
    parse(fromURL(resource).getLines().mkString).as[TaxCalc](taxCalcFormat)
  }

  import org.scalatest.prop.TableDrivenPropertyChecks._

  val input = Table(
    ("Test Description", "isStatePensionAge", "taxCalcResource", "taxCode", "grossPayPence", "payPeriod", "hoursIn", "expectedJson"),
    ("return a annual TaxCalc response", "false", taxYear_2017_2018, "1100T", 1000000, "annual", -1, "/data/2017_2018/2017_TaxCalcResponse.json"),
    (
      "return a annual TaxCalc response with zero value National Insurance Contributions Section",
      "true",
      taxYear_2017_2018,
      "1100T",
      1000000,
      "annual",
      -1,
      "/data/2017_2018/2017_no_NIC_Contribution_section_response.json"),
    (
      "return a NT TaxCalc response with no PAYE tax applied",
      "false",
      taxYear_2017_2018,
      "NT",
      20000000,
      "annual",
      -1,
      "/data/2017_2018/2017_NT_TaxCode_Response.json"),
    (
      "return a BR TaxCalc response with PAYE tax applied at 20%",
      "false",
      taxYear_2017_2018,
      "BR",
      20000000,
      "annual",
      -1,
      "/data/2017_2018/2017_BR_TaxCode_Response.json"),
    (
      "return a D0 TaxCalc response with PAYE tax applied at 40%",
      "false",
      taxYear_2017_2018,
      "D0",
      20000000,
      "annual",
      -1,
      "/data/2017_2018/2017_D0_TaxCode_Response.json"),
    (
      "return a D1 TaxCalc response with PAYE tax applied at 45%",
      "false",
      taxYear_2017_2018,
      "D1",
      20000000,
      "annual",
      -1,
      "/data/2017_2018/2017_D1_TaxCode_Response.json"),
    (
      "return tax calc response using an hourly rate input",
      "false",
      taxYear_2017_2018,
      "1100T",
      9615,
      "annual",
      40,
      "/data/2017_2018/2017_Hourly_Rate_Response.json"),
    (
      "return tax calc response using tapering with emergency tax code input",
      "false",
      taxYear_2017_2018,
      "1150L",
      221200,
      "weekly",
      -1,
      "/data/2017_2018/2017_Tapering_Emergency_TaxCode_Response.json"),
    (
      "max tax rate should kick in when the paye amount is greater than 50% of annual salary",
      "false",
      taxYear_2017_2018,
      "K4000",
      1000000,
      "annual",
      -1,
      "/data/2017_2018/2017_Max_Tax_Response.json"),
    (
      "max tax rate should kick in when the paye amount is greater than 50% of annual salary and round down for edge case where rounding increases the value to just over 50%",
      "false",
      taxYear_2017_2018,
      "K1100",
      100000,
      "annual",
      -1,
      "/data/2017_2018/2017_Max_Tax_Edge_Case_Response.json"),
    (
      "return a annual TaxCalc response",
      "false",
      scottish_TaxYear_2017_2018,
      "S1100T",
      1000000,
      "annual",
      -1,
      "/data/2017_2018/2017_Scottish_TaxCalcResponse.json"),
    (
      "return a annual TaxCalc response with zero value National Insurance Contributions Section",
      "true",
      scottish_TaxYear_2017_2018,
      "1100T",
      1000000,
      "annual",
      -1,
      "/data/2017_2018/2017_Scottish_no_NIC_Contribution_section_response.json"),
    (
      "return a SBR TaxCalc response with PAYE tax applied at 20%",
      "false",
      scottish_TaxYear_2017_2018,
      "SBR",
      20000000,
      "annual",
      -1,
      "/data/2017_2018/2017_SBR_TaxCode_Response.json"),
    (
      "return a SD0 TaxCalc response with PAYE tax applied at 40%",
      "false",
      scottish_TaxYear_2017_2018,
      "SD0",
      20000000,
      "annual",
      -1,
      "/data/2017_2018/2017_SD0_TaxCode_Response.json"),
    (
      "return a SD1 TaxCalc response with PAYE tax applied at 45%",
      "false",
      scottish_TaxYear_2017_2018,
      "SD1",
      20000000,
      "annual",
      -1,
      "/data/2017_2018/2017_SD1_TaxCode_Response.json"),
    (
      "return tax calc response using an hourly rate input",
      "false",
      scottish_TaxYear_2017_2018,
      "S1100T",
      9615,
      "annual",
      40,
      "/data/2017_2018/2017_Scottish_Hourly_Rate_Response.json"),
    (
      "return tax calc response using tapering with emergency tax code input",
      "false",
      scottish_TaxYear_2017_2018,
      "S1150L",
      221200,
      "weekly",
      -1,
      "/data/2017_2018/2017_Scottish_Tapering_Emergency_TaxCode_Response.json"),
    (
      "max tax rate should kick in when the paye amount is greater than 50% of annual salary",
      "false",
      scottish_TaxYear_2017_2018,
      "SK4000",
      1000000,
      "annual",
      -1,
      "/data/2017_2018/2017_Scottish_Max_Tax_Response.json"),
    (
      "max tax rate should kick in when the paye amount is greater than 50% of annual salary and round down for edge case where rounding increases the value to just over 50%",
      "false",
      scottish_TaxYear_2017_2018,
      "SK1100",
      100000,
      "annual",
      -1,
      "/data/2017_2018/2017_Scottish_Max_Tax_Edge_Case_Response.json"),
    ("return a annual TaxCalc response", "false", taxYear_2018_2019, "1100T", 1000008, "annual", -1, "/data/2018_2019/2018_TaxCalcResponse.json"),
    (
      "return a annual TaxCalc response with zero value National Insurance Contributions Section",
      "true",
      taxYear_2018_2019,
      "1100T",
      1000008,
      "annual",
      -1,
      "/data/2018_2019/2018_no_NIC_Contribution_section_response.json"),
    (
      "return a NT TaxCalc response with no PAYE tax applied",
      "false",
      taxYear_2018_2019,
      "nt",
      20000000,
      "annual",
      -1,
      "/data/2018_2019/2018_NT_TaxCode_Response.json"),
    (
      "return a BR TaxCalc response with PAYE tax applied at 20%",
      "false",
      taxYear_2018_2019,
      "BR",
      20000000,
      "annual",
      -1,
      "/data/2018_2019/2018_BR_TaxCode_Response.json"),
    (
      "return a D0 TaxCalc response with PAYE tax applied at 40%",
      "false",
      taxYear_2018_2019,
      "D0",
      20000000,
      "annual",
      -1,
      "/data/2018_2019/2018_D0_TaxCode_Response.json"),
    (
      "return a D1 TaxCalc response with PAYE tax applied at 45%",
      "false",
      taxYear_2018_2019,
      "D1",
      20000000,
      "annual",
      -1,
      "/data/2018_2019/2018_D1_TaxCode_Response.json"),
    (
      "return tax calc response using an hourly rate input",
      "false",
      taxYear_2018_2019,
      "1100T",
      9615,
      "annual",
      40,
      "/data/2018_2019/2018_Hourly_Rate_Response.json"),
    (
      "max tax rate should kick in when the paye amount is greater than 50% of annual salary",
      "false",
      taxYear_2018_2019,
      "K4000",
      1000000,
      "annual",
      -1,
      "/data/2018_2019/2018_Max_Tax_Response.json"),
    (
      "max tax rate should kick in when the paye amount is greater than 50% of annual salary and round down for edge case where rounding increases the value to just over 50%",
      "false",
      taxYear_2018_2019,
      "K1100",
      100000,
      "annual",
      -1,
      "/data/2018_2019/2018_Max_Tax_Edge_Case_Response.json"),
    (
      "return a annual TaxCalc response",
      "false",
      scottish_TaxYear_2018_2019,
      "S1100T",
      1000000,
      "annual",
      -1,
      "/data/2018_2019/2018_Scottish_TaxCalcResponse.json"),
    (
      "return a annual TaxCalc response with zero value National Insurance Contributions Section",
      "true",
      scottish_TaxYear_2018_2019,
      "S1100T",
      1000000,
      "annual",
      -1,
      "/data/2018_2019/2018_Scottish_no_NIC_Contribution_section_response.json"),
    (
      "return a SBR TaxCalc response with PAYE tax applied at 20%",
      "false",
      scottish_TaxYear_2018_2019,
      "SBR",
      20000000,
      "annual",
      -1,
      "/data/2018_2019/2018_SBR_TaxCode_Response.json"),
    (
      "return a SD0 TaxCalc response with PAYE tax applied at 21%",
      "false",
      scottish_TaxYear_2018_2019,
      "SD0",
      20000000,
      "annual",
      -1,
      "/data/2018_2019/2018_SD0_TaxCode_Response.json"),
    (
      "return a SD1 TaxCalc response with PAYE tax applied at 41%",
      "false",
      scottish_TaxYear_2018_2019,
      "SD1",
      20000000,
      "annual",
      -1,
      "/data/2018_2019/2018_SD1_TaxCode_Response.json"),
    (
      "return a SD2 TaxCalc response with PAYE tax applied at 46%",
      "false",
      scottish_TaxYear_2018_2019,
      "SD2",
      20000000,
      "annual",
      -1,
      "/data/2018_2019/2018_SD2_TaxCode_Response.json"),
    (
      "return tax calc response using an hourly rate input",
      "false",
      scottish_TaxYear_2018_2019,
      "S1100T",
      9615,
      "annual",
      40,
      "/data/2018_2019/2018_Scottish_Hourly_Rate_Response.json"),
    (
      "return tax calc response using tapering with emergency tax code input",
      "false",
      scottish_TaxYear_2018_2019,
      "S1185L",
      221200,
      "weekly",
      -1,
      "/data/2018_2019/2018_Scottish_Tapering_Emergency_TaxCode_Response.json"),
    (
      "max tax rate should kick in when the paye amount is greater than 50% of annual salary",
      "false",
      scottish_TaxYear_2018_2019,
      "SK4000",
      1000000,
      "annual",
      -1,
      "/data/2018_2019/2018_Scottish_Max_Tax_Response.json"),
    (
      "max tax rate should kick in when the paye amount is greater than 50% of annual salary and round down for edge case where rounding increases the value to just over 50%",
      "false",
      scottish_TaxYear_2018_2019,
      "SK1185",
      100000,
      "annual",
      -1,
      "/data/2018_2019/2018_Scottish_Max_Tax_Edge_Case_Response.json"),
    ("return a annual TaxCalc response", "false", taxYear_2019_2020, "1100T", 1000008, "annual", -1, "/data/2019_2020/2019_TaxCalcResponse.json"),
    (
      "return a annual TaxCalc response when Welsh",
      "false",
      taxYear_2019_2020,
      "C1100T",
      1000008,
      "annual",
      -1,
      "/data/2019_2020/2019_TaxCalcResponse_Welsh.json"),
    (
      "return a annual TaxCalc response with zero value National Insurance Contributions Section",
      "true",
      taxYear_2019_2020,
      "1100T",
      1000008,
      "annual",
      -1,
      "/data/2019_2020/2019_no_NIC_Contribution_section_response.json"),
    (
      "return a NT TaxCalc response with no PAYE tax applied",
      "false",
      taxYear_2019_2020,
      "nt",
      20000000,
      "annual",
      -1,
      "/data/2019_2020/2019_NT_TaxCode_Response.json"),
    (
      "return a BR TaxCalc response with PAYE tax applied at 20%",
      "false",
      taxYear_2019_2020,
      "BR",
      20000000,
      "annual",
      -1,
      "/data/2019_2020/2019_BR_TaxCode_Response.json"),
    (
      "return a D0 TaxCalc response with PAYE tax applied at 40%",
      "false",
      taxYear_2019_2020,
      "D0",
      20000000,
      "annual",
      -1,
      "/data/2019_2020/2019_D0_TaxCode_Response.json"),
    (
      "return a D1 TaxCalc response with PAYE tax applied at 45%",
      "false",
      taxYear_2019_2020,
      "D1",
      20000000,
      "annual",
      -1,
      "/data/2019_2020/2019_D1_TaxCode_Response.json"),
    (
      "return tax calc response using an hourly rate input",
      "false",
      taxYear_2019_2020,
      "1100T",
      9615,
      "annual",
      40,
      "/data/2019_2020/2019_Hourly_Rate_Response.json"),
    (
      "max tax rate should kick in when the paye amount is greater than 50% of annual salary",
      "false",
      taxYear_2019_2020,
      "K4000",
      1000000,
      "annual",
      -1,
      "/data/2019_2020/2019_Max_Tax_Response.json"),
    (
      "max tax rate should kick in when the paye amount is greater than 50% of annual salary and round down for edge case where rounding increases the value to just over 50%",
      "false",
      taxYear_2019_2020,
      "K1100",
      100000,
      "annual",
      -1,
      "/data/2019_2020/2019_Max_Tax_Edge_Case_Response.json"),
    (
      "return a annual TaxCalc response",
      "false",
      scottish_TaxYear_2019_2020,
      "S1100T",
      1000000,
      "annual",
      -1,
      "/data/2019_2020/2019_Scottish_TaxCalcResponse.json"),
    (
      "return a annual TaxCalc response with zero value National Insurance Contributions Section",
      "true",
      scottish_TaxYear_2019_2020,
      "S1100T",
      1000000,
      "annual",
      -1,
      "/data/2019_2020/2019_Scottish_no_NIC_Contribution_section_response.json"),
    (
      "return a SBR TaxCalc response with PAYE tax applied at 20%",
      "false",
      scottish_TaxYear_2019_2020,
      "SBR",
      20000000,
      "annual",
      -1,
      "/data/2019_2020/2019_SBR_TaxCode_Response.json"),
    (
      "return a SD0 TaxCalc response with PAYE tax applied at 21%",
      "false",
      scottish_TaxYear_2019_2020,
      "SD0",
      20000000,
      "annual",
      -1,
      "/data/2019_2020/2019_SD0_TaxCode_Response.json"),
    (
      "return a SD1 TaxCalc response with PAYE tax applied at 41%",
      "false",
      scottish_TaxYear_2019_2020,
      "SD1",
      20000000,
      "annual",
      -1,
      "/data/2019_2020/2019_SD1_TaxCode_Response.json"),
    (
      "return a SD2 TaxCalc response with PAYE tax applied at 46%",
      "false",
      scottish_TaxYear_2019_2020,
      "SD2",
      20000000,
      "annual",
      -1,
      "/data/2019_2020/2019_SD2_TaxCode_Response.json"),
    (
      "return tax calc response using an hourly rate input",
      "false",
      scottish_TaxYear_2019_2020,
      "S1100T",
      9615,
      "annual",
      40,
      "/data/2019_2020/2019_Scottish_Hourly_Rate_Response.json"),
    (
      "return tax calc response using tapering with emergency tax code input",
      "false",
      scottish_TaxYear_2019_2020,
      "S1250L",
      221200,
      "weekly",
      -1,
      "/data/2019_2020/2019_Scottish_Tapering_Emergency_TaxCode_Response.json"),
    (
      "max tax rate should kick in when the paye amount is greater than 50% of annual salary",
      "false",
      scottish_TaxYear_2019_2020,
      "SK4000",
      1000000,
      "annual",
      -1,
      "/data/2019_2020/2019_Scottish_Max_Tax_Response.json"),
    (
      "max tax rate should kick in when the paye amount is greater than 50% of annual salary and round down for edge case where rounding increases the value to just over 50%",
      "false",
      scottish_TaxYear_2019_2020,
      "SK1185",
      100000,
      "annual",
      -1,
      "/data/2019_2020/2019_Scottish_Max_Tax_Edge_Case_Response.json")
  )

  "LiveTaxCalculatorService calculate tax" should {
    forAll(input) { (testDescription, isStatePensionAge, taxCalcResource, taxCode, grossPayPence, payPeriod, hoursIn, expectedJson) =>
      s"${taxCalcResource.taxYear} $testDescription" in new LiveTaxCalcServiceSuccess {

        val result: TaxCalc = service.buildTaxCalc(isStatePensionAge, taxCalcResource, taxCode, grossPayPence, payPeriod, hoursIn)

        val expected: TaxCalc = taxCalcFromJson(expectedJson)
        print(Json.toJson(result))
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
      val result: Money = service.annualiseGrossPay(12345600, None, "annual")
      result.value shouldBe BigDecimal(123456.00)
    }

    "multiply 12345600 by 12 and convert to its Pound Value if the payPeriod is 'monthly'" in new LiveTaxCalcServiceSuccess {
      val result: Money = service.annualiseGrossPay(12345600, None, "monthly")
      result.value shouldBe BigDecimal(12345600 * 12 / 100)
    }

    "multiply 12345600 by 52 and convert to its Pound Value if the payPeriod is 'weekly'" in new LiveTaxCalcServiceSuccess {
      val result: Money = service.annualiseGrossPay(12345600, None, "weekly")
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
      service.convertToBoolean("true")  shouldBe true
      service.convertToBoolean("TRUE")  shouldBe true
      service.convertToBoolean("false") shouldBe false
      service.convertToBoolean("FALSE") shouldBe false
      intercept[Exception] {
        service.convertToBoolean("notA_Boolean_String")
      }
    }
  }

  "LiveTaxCalculatorService calculateAverageAnnualTaxRate" should {
    "calculate the average annual tax rate" in new LiveTaxCalcServiceSuccess {

      val tbWeek = Option(
        TaxBreakdown(
          "weekly",
          grossPay = BigDecimal(1000.00),
          BigDecimal(0),
          BigDecimal(0),
          BigDecimal(0),
          Option(BigDecimal(0)),
          BigDecimal(0),
          Seq(),
          totalDeductions = BigDecimal(200.00),
          BigDecimal(0)
        ))
      val tbMonth = Option(
        TaxBreakdown(
          "monthly",
          grossPay = BigDecimal(7000.00),
          BigDecimal(0),
          BigDecimal(0),
          BigDecimal(0),
          Option(BigDecimal(0)),
          BigDecimal(0),
          Seq(),
          totalDeductions = BigDecimal(2000.00),
          BigDecimal(0)
        ))
      val tbAnnual = Option(
        TaxBreakdown(
          "annual",
          grossPay = BigDecimal(60000.00),
          BigDecimal(0),
          BigDecimal(0),
          BigDecimal(0),
          Option(BigDecimal(0)),
          BigDecimal(0),
          Seq(),
          totalDeductions = BigDecimal(24000.00),
          BigDecimal(0)
        ))

      service.calculateAverageAnnualTaxRate(tbWeek).value   shouldBe BigDecimal(20.00)
      service.calculateAverageAnnualTaxRate(tbMonth).value  shouldBe BigDecimal(28.57)
      service.calculateAverageAnnualTaxRate(tbAnnual).value shouldBe BigDecimal(40.00)
      service.calculateAverageAnnualTaxRate(None).value     shouldBe BigDecimal(0.00)
    }
  }

  "LiveTaxCalculatorService parseDate" should {
    "parse a Date String yyyy-MM-dd to a LocalDate" in new LiveTaxCalcServiceSuccess {
      LocalDate.of(2017, 3, 8) shouldBe service.parseDate("2017-03-08")
    }
  }
}

object Formats {
  implicit val taxCalcFormatAggregation: OFormat[Aggregation]  = format[Aggregation]
  implicit val taxCalcFormatTaxCategory: OFormat[TaxCategory]  = format[TaxCategory]
  implicit val taxCalcFormatBreakdown:   OFormat[TaxBreakdown] = format[TaxBreakdown]
  implicit val taxCalcFormat:            OFormat[TaxCalc]      = format[TaxCalc]
}
