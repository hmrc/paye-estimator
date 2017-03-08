package uk.gov.hmrc.payeestimator.services

import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain._

class AnnualTaperingDeductionCalculatorSpec extends WordSpecLike with Matchers with TaxYearChanges {

  val TaxYear_2016_2017 = new TaxYear_2016_2017(false)
  val TaxYear_2017_2018 = new TaxYear_2017_2018(false)
  val Scottish_TaxYear_2017_2018 = new TaxYear_2017_2018(true)

  import org.scalatest.prop.TableDrivenPropertyChecks._

  val input = Table(
    ("taxCode", "grossPay", "taxCalcResource", "success", "isTapered", "result", "isScottish"),
    ("11L"   , Money(100000), TaxYear_2016_2017         , true, false, "11L"    , false),
    ("1100L" , Money(120000), TaxYear_2016_2017         , true, true , "100.00L", false),
    ("1100L" , Money(440000), TaxYear_2016_2017         , true, true , "ZERO"   , false),
    ("1100L" , Money(100000), TaxYear_2016_2017         , true, false, "1100L"  , false),
    ("11L"   , Money(123000), TaxYear_2017_2018         , true, false, "11L"    , false),
    ("1150L" , Money(110000), TaxYear_2017_2018         , true, true , "650.00L", false),
    ("1150L" , Money(123000), TaxYear_2017_2018         , true, true , "ZERO"   , false),
    ("1150L" , Money(100000), TaxYear_2017_2018         , true, false, "1150L"  , false),
    ("11L"   , Money(123000), Scottish_TaxYear_2017_2018, true, false, "11L"    , true),
    ("1150L" , Money(110000), Scottish_TaxYear_2017_2018, true, true , "650.00L", true),
    ("1150L" , Money(123000), Scottish_TaxYear_2017_2018, true, true , "ZERO"   , true),
    ("1150L" , Money(100000), Scottish_TaxYear_2017_2018, true, false, "1150L"  , true)
  )

  forAll(input) {

    (taxCode, grossPay, taxCalcResource, success, isTapered, answer, isScottish) =>

      val append = if(isScottish) " for Scottish Rates" else ""
      s"AnnualTaperingDeductionCalculator calculate(taxCode[$taxCode], grossPay[${grossPay.value}], taxYear[${taxCalcResource.taxYear}]) $append" should {
        s"return success[$success], isTapered[$isTapered], answer[$answer]" in {

          val result = AnnualTaperingDeductionCalculator(taxCode, grossPay, taxCalcResource).calculate()
          result.success shouldBe success
          result.isTapered shouldBe isTapered
          result.result shouldBe answer
        }
      }
  }
}
