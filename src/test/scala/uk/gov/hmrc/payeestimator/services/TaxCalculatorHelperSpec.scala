package uk.gov.hmrc.payeestimator.services

import org.scalatest.prop.Tables.Table
import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain._
import org.scalatest.prop.TableDrivenPropertyChecks._

class TaxCalculatorHelperSpec extends WordSpecLike with Matchers with TaxYearChanges{

  val TaxYear_2017_2018 = new TaxYear_2017_2018(false)
  val TaxYear_2018_2019 = new TaxYear_2018_2019(false)

  "TaxCalculatorHelper isStandardTaxCode" should {
    "return true if the code matches the format where the first 4 digits are between 0-9999 and the last is L,M,N or T" in new TaxCalculatorHelperSetup {
      helper.isStandardTaxCode("9999L") shouldBe true
      helper.isStandardTaxCode("0N") shouldBe true
      helper.isStandardTaxCode("100M") shouldBe true
      helper.isStandardTaxCode("88N") shouldBe true
      helper.isStandardTaxCode("t999") shouldBe false
    }
  }
  "TaxCalculatorHelper isTaxableCode" should {
    "return true is the taxCode is not NT, BR, D0 or D1" in new TaxCalculatorHelperSetup {
      helper.isTaxableCode("NT") shouldBe false
      helper.isTaxableCode("BR") shouldBe false
      helper.isTaxableCode("D0") shouldBe false
      helper.isTaxableCode("D1") shouldBe false
      helper.isTaxableCode("9999L") shouldBe true
    }
  }
  "TaxCalculatorHelper isBasicRateTaxCode" should {
    "return true if the taxCode is BR, D0 or D1" in new TaxCalculatorHelperSetup {
      helper.isBasicRateTaxCode("BR") shouldBe true
      helper.isBasicRateTaxCode("D0") shouldBe true
      helper.isBasicRateTaxCode("D1") shouldBe true
      helper.isBasicRateTaxCode("NT") shouldBe false
    }
  }

  val input = Table(
    ("taxCode",  "taxCalcResource", "expectedResult", "actualEmergencyTaxCode"),
    ("1150L" , TaxYear_2017_2018, true,  "1150L"),
    ("11500L", TaxYear_2017_2018, false, "1150L"),
    ("11150L", TaxYear_2017_2018, false, "1150L"),
    ("1150T" , TaxYear_2017_2018, false, "1150L"),
    ("1185L" , TaxYear_2018_2019, true,  "1185L"),
    ("11850L", TaxYear_2018_2019, false, "1185L"),
    ("11185L", TaxYear_2018_2019, false, "1185L"),
    ("1185T" , TaxYear_2018_2019, false, "1185L")
  )

  s"TaxCalculatorHelper isEmergencyTaxCode()" should {
    forAll(input) {
      (taxCode,  taxCalcResource, expectedResult, actualEmergencyTaxCode) =>
      s"return $expectedResult for ${taxCalcResource.taxYear} for $taxCode if the tax code matches $actualEmergencyTaxCode" in new TaxCalculatorHelperSetup {
        helper.isEmergencyTaxCode(taxCode, taxCalcResource) shouldBe expectedResult
      }
    }
  }

  "TaxCalculatorHelper isAdjustedTaxCode" should {
    "return true if the tax code contains a decimal place with an appended L" in new TaxCalculatorHelperSetup {
      helper.isAdjustedTaxCode("1234.98L") shouldBe true
      helper.isAdjustedTaxCode("1234.98T") shouldBe false
      helper.isAdjustedTaxCode("1234L") shouldBe false
    }
  }
  "TaxCalculatorHelper isValidScottishTaxCode" should {
    "return true if the first character of the tax code is 'S' and the subsequent characters are valid Standard Rate, Basic Rate and Untaxed Income tax codes." in new TaxCalculatorHelperSetup {
      helper.isValidScottishTaxCode("SBR") shouldBe true
      helper.isValidScottishTaxCode("SD0") shouldBe true
      helper.isValidScottishTaxCode("SD1") shouldBe true
      helper.isValidScottishTaxCode("SK20") shouldBe true
      helper.isValidScottishTaxCode("S9999L") shouldBe true
      helper.isValidScottishTaxCode("S0N") shouldBe true
      helper.isValidScottishTaxCode("SNT") shouldBe false
      helper.isValidScottishTaxCode("ST999") shouldBe false
    }
  }
  "TaxCalculatorHelper isUnTaxedIncomeTaxCode" should {
    "return true if the tax code begins with SK or K and the last digits are between 0-9999" in new TaxCalculatorHelperSetup {
      helper.isUnTaxedIncomeTaxCode("K100") shouldBe true
      helper.isUnTaxedIncomeTaxCode("SK100") shouldBe true
      helper.isUnTaxedIncomeTaxCode("KS100") shouldBe false
      helper.isUnTaxedIncomeTaxCode("S100") shouldBe false

    }
  }
  "TaxCalculatorHelper removeScottishElement" should {
    "removes the leading 'S' from the taxCode if it is a valid Scottish TaxCode" in new TaxCalculatorHelperSetup {
      helper.removeScottishElement("S7893N") shouldBe "7893N"
      helper.removeScottishElement("SBR") shouldBe "BR"
      helper.removeScottishElement("SNT") shouldBe "SNT"
      helper.removeScottishElement("ST9009") shouldBe "ST9009"
    }
  }
}
