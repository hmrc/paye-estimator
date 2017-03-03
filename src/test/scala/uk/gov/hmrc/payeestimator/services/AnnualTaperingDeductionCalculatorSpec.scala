package uk.gov.hmrc.payeestimator.services

import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain.{Money, TaxCalcResource, TaxYear_2016_2017, TaxYear_2017_2018}

class AnnualTaperingDeductionCalculatorSpec extends WordSpecLike with Matchers {

  import org.scalatest.prop.TableDrivenPropertyChecks._

  val input = Table(
    ("taxCode", "grossPay", "taxCalcResource", "success", "isTapered", "result"),
    ("11L", Money(100000), TaxYear_2016_2017, true, false,  "11L"    ),
    ("1100L", Money(120000), TaxYear_2016_2017, true, true, "100.00L"),
    ("1100L", Money(440000), TaxYear_2016_2017, true, true, "ZERO"   ),
    ("1100L", Money(100000), TaxYear_2016_2017, true, false, "1100L" ),
    ("11L", Money(123000), TaxYear_2017_2018, true, false, "11L"     ),
    ("1150L", Money(110000), TaxYear_2017_2018, true, true, "650.00L"),
    ("1150L", Money(123000), TaxYear_2017_2018, true, true, "ZERO"   ),
    ("1150L", Money(100000), TaxYear_2017_2018, true, false, "1150L" )
  )

  forAll(input) {

    (taxCode, grossPay, taxCalcResource, success, isTapered, answer) =>

      s"AnnualTaperingDeductionCalculator calculate(taxCode[$taxCode], grossPay[${grossPay.value}], taxYear[${taxCalcResource.taxYear}])" should {

        s"return success[$success], isTapered[$isTapered], answer[$answer]" in {

          val result = AnnualTaperingDeductionCalculator(taxCode, grossPay, taxCalcResource).calculate()
          result.success shouldBe success
          result.isTapered shouldBe isTapered
          result.result shouldBe answer
        }
      }
  }
}
