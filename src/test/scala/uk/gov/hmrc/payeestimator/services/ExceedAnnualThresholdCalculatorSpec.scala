package uk.gov.hmrc.payeestimator.services

import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain._

class ExceedAnnualThresholdCalculatorSpec extends WordSpecLike with Matchers with TaxYearChanges {

  val TaxYear_2017_2018          = new TaxYear_2017_2018(isScottish = false)
  val TaxYear_2018_2019          = new TaxYear_2018_2019(isScottish = false)
  val TaxYear_2019_2020          = new TaxYear_2019_2020(isScottish = false)
  val Scottish_TaxYear_2017_2018 = new TaxYear_2017_2018(isScottish = true)
  val Scottish_TaxYear_2018_2019 = new TaxYear_2018_2019(isScottish = true)
  val Scottish_TaxYear_2019_2020 = new TaxYear_2019_2020(isScottish = true)

  import org.scalatest.prop.TableDrivenPropertyChecks._

  val input = Table(
    ("taxCode", "grossPay", "taxCalcResource", "success", "isTapered", "result", "isScottish"),
    //2017-2018
    ("11L", Money(123000), TaxYear_2017_2018, true, false, "11L", false),
    ("1150L", Money(110000), TaxYear_2017_2018, true, true, "650.00L", false),
    ("1150L", Money(123000), TaxYear_2017_2018, true, true, "ZERO", false),
    ("1150L", Money(100000), TaxYear_2017_2018, true, false, "1150L", false),
    ("11L", Money(123000), Scottish_TaxYear_2017_2018, true, false, "11L", true),
    ("1150L", Money(110000), Scottish_TaxYear_2017_2018, true, true, "650.00L", true),
    ("1150L", Money(123000), Scottish_TaxYear_2017_2018, true, true, "ZERO", true),
    ("1150L", Money(100000), Scottish_TaxYear_2017_2018, true, false, "1150L", true),
    //2018-2019
    ("11L", Money(123000), TaxYear_2018_2019, true, false, "11L", false),
    ("1185L", Money(110000), TaxYear_2018_2019, true, true, "685.00L", false),
    ("1185L", Money(123700), TaxYear_2018_2019, true, true, "ZERO", false),
    ("1185L", Money(100000), TaxYear_2018_2019, true, false, "1185L", false),
    ("11L", Money(123000), Scottish_TaxYear_2018_2019, true, false, "11L", true),
    ("1185L", Money(110000), Scottish_TaxYear_2018_2019, true, true, "685.00L", true),
    ("1185L", Money(123700), Scottish_TaxYear_2018_2019, true, true, "ZERO", true),
    ("1185L", Money(100000), Scottish_TaxYear_2018_2019, true, false, "1185L", true),
    //2019 - 2020
    ("11L", Money(123000), TaxYear_2019_2020, true, false, "11L", false),
    ("1250L", Money(110000), TaxYear_2019_2020, true, true, "750.00L", false),
    ("1250L", Money(126000), TaxYear_2019_2020, true, true, "ZERO", false),
    ("1250L", Money(100000), TaxYear_2019_2020, true, false, "1250L", false),
    ("11L", Money(123000), Scottish_TaxYear_2019_2020, true, false, "11L", true),
    ("1250L", Money(110000), Scottish_TaxYear_2019_2020, true, true, "750.00L", true),
    ("1250L", Money(126000), Scottish_TaxYear_2019_2020, true, true, "ZERO", true),
    ("1250L", Money(100000), Scottish_TaxYear_2019_2020, true, false, "1250L", true)
  )

  forAll(input) { (taxCode, grossPay, taxCalcResource, success, isTapered, answer, isScottish) =>
    val append = if (isScottish) " for Scottish Rates" else ""
    s"ExceedAnnualThresholdCalculator calculate(taxCode[$taxCode], grossPay[${grossPay.value}], taxYear[${taxCalcResource.taxYear}]) $append" should {
      s"return success[$success], isTapered[$isTapered], answer[$answer]" in {

        val result = AnnualTaperingDeductionCalculator(taxCode, grossPay, taxCalcResource).calculate()
        result.success   shouldBe success
        result.isTapered shouldBe isTapered
        result.result    shouldBe answer
      }
    }
  }
}
