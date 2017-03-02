package uk.gov.hmrc.payeestimator.services

import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain.{TaxYear_2016_2017, TaxYear_2017_2018}

class TaxCalculatorHelperSpec extends WordSpecLike with Matchers {

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
  "TaxCalculatorHelper isEmergencyTaxCode" should {
    "return true for 2016/17 if the tax code matches 1100L" in new TaxCalculatorHelperSetup {
      helper.isEmergencyTaxCode("1100L", TaxYear_2016_2017) shouldBe true
      helper.isEmergencyTaxCode("11100L", TaxYear_2016_2017) shouldBe false
      helper.isEmergencyTaxCode("11000L", TaxYear_2016_2017) shouldBe false
      helper.isEmergencyTaxCode("1100T", TaxYear_2016_2017) shouldBe false
    }

    "return true for 2017/18 if the tax code matches 1150L" in new TaxCalculatorHelperSetup {
      helper.isEmergencyTaxCode("1150L", TaxYear_2017_2018) shouldBe true
      helper.isEmergencyTaxCode("11100L", TaxYear_2017_2018) shouldBe false
      helper.isEmergencyTaxCode("11000L", TaxYear_2017_2018) shouldBe false
      helper.isEmergencyTaxCode("1100T", TaxYear_2017_2018) shouldBe false
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
