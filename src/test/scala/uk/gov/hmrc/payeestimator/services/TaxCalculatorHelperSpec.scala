package uk.gov.hmrc.payeestimator.services

import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.Tables.Table
import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain._

class TaxCalculatorHelperSpec extends WordSpecLike with Matchers with TaxYearChanges {

  val TaxYear_2017_2018          = new TaxYear_2017_2018(isScottish = false)
  val TaxYear_2018_2019          = new TaxYear_2018_2019(isScottish = false)
  val TaxYear_2019_2020          = new TaxYear_2019_2020(isScottish = false)
  val Scottish_TaxYear_2019_2020 = new TaxYear_2019_2020(isScottish = true)

  "TaxCalculatorHelper isStandardTaxCode" should {
    "return true if the code matches the format where the first 4 digits are between 0-9999 and the last is L,M,N or T" in new TaxCalculatorHelperSetup {
      helper.isStandardTaxCode("9999L") shouldBe true
      helper.isStandardTaxCode("0N")    shouldBe true
      helper.isStandardTaxCode("100M")  shouldBe true
      helper.isStandardTaxCode("88N")   shouldBe true
      helper.isStandardTaxCode("t999")  shouldBe false

      helper.isStandardTaxCode("C9999L") shouldBe true
    }
  }
  "TaxCalculatorHelper isTaxableCode" should {
    "return true is the taxCode is not NT, BR, D0 or D1 and D2 if Scottish Rates" in new TaxCalculatorHelperSetup {
      helper.isTaxableCode("NT") shouldBe false
      helper.isTaxableCode("BR") shouldBe false
      helper.isTaxableCode("D0") shouldBe false
      helper.isTaxableCode("D1") shouldBe false
      helper.isTaxableCode("D2") shouldBe true
      helper.isTaxableCode("D2", isScottish = true) shouldBe false
      helper.isTaxableCode("9999L") shouldBe true
    }
  }
  "TaxCalculatorHelper isBasicRateTaxCode" should {
    "return true if the taxCode is BR, D0 or D1" in new TaxCalculatorHelperSetup {
      helper.isBasicRateTaxCode("BR") shouldBe true
      helper.isBasicRateTaxCode("D0") shouldBe true
      helper.isBasicRateTaxCode("D1") shouldBe true
      helper.isBasicRateTaxCode("D2") shouldBe false
      helper.isBasicRateTaxCode("D2", isScottish = true) shouldBe true
      helper.isBasicRateTaxCode("NT") shouldBe false
    }
  }

  val basicTaxCodes = Table(
    ("taxCode", "taxCalcResource", "expectedResult"),
    //2017-2018
    ("1150L", TaxYear_2017_2018, true),
    ("1150", TaxYear_2017_2018, false),
    ("1150LL", TaxYear_2017_2018, false),
    //2018-2019
    ("1185L", TaxYear_2018_2019, true),
    ("1185", TaxYear_2018_2019, false),
    ("1185LL", TaxYear_2018_2019, false),
    //2018-2019
    ("1250L", TaxYear_2019_2020, true),
    ("1250", TaxYear_2019_2020, false),
    ("1250LL", TaxYear_2019_2020, false)
  )

  s"TaxCalculatorHelper isMainTaxCode()" should {
    forAll(basicTaxCodes) { (taxCode, taxCalcResource, expectedResult) =>
      s"return $expectedResult for ${taxCalcResource.taxYear} for $taxCode" in new TaxCalculatorHelperSetup {
        helper.isMainTaxCode(taxCode, taxCalcResource) shouldBe expectedResult
      }
    }
  }

  val input = Table(
    ("taxCode", "taxCalcResource", "expectedResult"),
    //2017-2018
    ("1150L", TaxYear_2017_2018, true),
    ("11500L", TaxYear_2017_2018, false),
    ("11150L", TaxYear_2017_2018, false),
    ("1150T", TaxYear_2017_2018, false),
    //2018-2019
    ("1185L", TaxYear_2018_2019, true),
    ("11850L", TaxYear_2018_2019, false),
    ("11185L", TaxYear_2018_2019, false),
    ("12500L", TaxYear_2018_2019, false),
    ("12250L", TaxYear_2018_2019, false),
    ("1185T", TaxYear_2018_2019, false),
    //2019-2020
    ("1250L", TaxYear_2019_2020, false),
    ("0X", TaxYear_2019_2020, false),
    ("C1250L", TaxYear_2019_2020, false),
    ("C0X", TaxYear_2019_2020, false),
    ("CS1250L", TaxYear_2019_2020, false),
    ("SC1250L", Scottish_TaxYear_2019_2020, false),
    ("SC1250L", TaxYear_2019_2020, false),
    ("S1250L", Scottish_TaxYear_2019_2020, false),
    ("S0X", Scottish_TaxYear_2019_2020, false),
    ("1250 W1", TaxYear_2019_2020, true),
    ("1250 M1", TaxYear_2019_2020, true),
    ("1250W1", TaxYear_2019_2020, true),
    ("1250M1", TaxYear_2019_2020, true),
    ("1250 X", TaxYear_2019_2020, true),
    ("1250L X", TaxYear_2019_2020, true),
    ("1250X", TaxYear_2019_2020, true),
    ("1250LX", TaxYear_2019_2020, true),
    ("C1250 W1", TaxYear_2019_2020, true),
    ("C1250 M1", TaxYear_2019_2020, true),
    ("C1250W1", TaxYear_2019_2020, true),
    ("C1250M1", TaxYear_2019_2020, true),
    ("C1250 X", TaxYear_2019_2020, true),
    ("C1250L X", TaxYear_2019_2020, true),
    ("C1250X", TaxYear_2019_2020, true),
    ("C1250LX", TaxYear_2019_2020, true),
    ("S1250 W1", Scottish_TaxYear_2019_2020, true),
    ("S1250 M1", Scottish_TaxYear_2019_2020, true),
    ("S1250W1", Scottish_TaxYear_2019_2020, true),
    ("S1250M1", Scottish_TaxYear_2019_2020, true),
    ("S1250 X", Scottish_TaxYear_2019_2020, true),
    ("S1250L X", Scottish_TaxYear_2019_2020, true),
    ("S1250X", Scottish_TaxYear_2019_2020, true),
    ("S1250LX", Scottish_TaxYear_2019_2020, true)
  )

  s"TaxCalculatorHelper isEmergencyTaxCode()" should {
    forAll(input) { (taxCode, taxCalcResource, expectedResult) =>
      s"return $expectedResult for ${taxCalcResource.taxYear} for $taxCode" in new TaxCalculatorHelperSetup {
        helper.isEmergencyTaxCode(taxCode, taxCalcResource) shouldBe expectedResult
      }
    }
  }

  "TaxCalculatorHelper isAdjustedTaxCode" should {
    "return true if the tax code contains a decimal place with an appended L" in new TaxCalculatorHelperSetup {
      helper.isAdjustedTaxCode("1234.98L")  shouldBe true
      helper.isAdjustedTaxCode("1234.98T")  shouldBe false
      helper.isAdjustedTaxCode("1234L")     shouldBe false
      helper.isAdjustedTaxCode("C1234.98L") shouldBe true
      helper.isAdjustedTaxCode("C1234.98T") shouldBe false
      helper.isAdjustedTaxCode("C1234L")    shouldBe false
    }
  }
  "TaxCalculatorHelper isValidScottishTaxCode" should {
    "return true if the first character of the tax code is 'S' and the subsequent characters are valid Standard Rate, Basic Rate and Untaxed Income tax codes." in new TaxCalculatorHelperSetup {
      helper.isValidScottishTaxCode("SBR")    shouldBe true
      helper.isValidScottishTaxCode("SD0")    shouldBe true
      helper.isValidScottishTaxCode("SD1")    shouldBe true
      helper.isValidScottishTaxCode("SK20")   shouldBe true
      helper.isValidScottishTaxCode("S9999L") shouldBe true
      helper.isValidScottishTaxCode("S0N")    shouldBe true
      helper.isValidScottishTaxCode("SNT")    shouldBe false
      helper.isValidScottishTaxCode("ST999")  shouldBe false
      helper.isValidScottishTaxCode("S1250")  shouldBe false
      helper.isValidScottishTaxCode("S1250X")  shouldBe true
      helper.isValidScottishTaxCode("S1250 X")  shouldBe true
      helper.isValidScottishTaxCode("S1250LX")  shouldBe true
      helper.isValidScottishTaxCode("S1250MX")  shouldBe true
      helper.isValidScottishTaxCode("S1250NX")  shouldBe true
      helper.isValidScottishTaxCode("S1250 MX")  shouldBe true
      helper.isValidScottishTaxCode("S1250 NX")  shouldBe true
      helper.isValidScottishTaxCode("S1250 M1")  shouldBe true
      helper.isValidScottishTaxCode("S1250 W1")  shouldBe true
      helper.isValidScottishTaxCode("S1250M1")  shouldBe true
      helper.isValidScottishTaxCode("S1250W1")  shouldBe true
      helper.isValidScottishTaxCode("S1250 Z")  shouldBe false
      helper.isValidScottishTaxCode("S1250Z")  shouldBe false
    }
  }
  "TaxCalculatorHelper isUnTaxedIncomeTaxCode" should {
    "return true if the tax code begins with SK or K and the last digits are between 0-9999" in new TaxCalculatorHelperSetup {
      helper.isUnTaxedIncomeTaxCode("K100")   shouldBe true
      helper.isUnTaxedIncomeTaxCode("SK100")  shouldBe true
      helper.isUnTaxedIncomeTaxCode("CK100")  shouldBe true
      helper.isUnTaxedIncomeTaxCode("CSK100") shouldBe false
      helper.isUnTaxedIncomeTaxCode("SCK100") shouldBe false
      helper.isUnTaxedIncomeTaxCode("KS100")  shouldBe false
      helper.isUnTaxedIncomeTaxCode("S100")   shouldBe false
      helper.isUnTaxedIncomeTaxCode("C100")   shouldBe false
    }
  }
  "TaxCalculatorHelper removeCountryElement" should {
    "removes the leading 'S' or 'C from the taxCode if it is a valid Scottish or Welsh TaxCode" in new TaxCalculatorHelperSetup {
      helper.removeCountryElementFromTaxCode("S7893N") shouldBe "7893N"
      helper.removeCountryElementFromTaxCode("SBR")    shouldBe "BR"
      helper.removeCountryElementFromTaxCode("SNT")    shouldBe "SNT"
      helper.removeCountryElementFromTaxCode("C7893N") shouldBe "7893N"
      helper.removeCountryElementFromTaxCode("CBR")    shouldBe "BR"
      helper.removeCountryElementFromTaxCode("7893N")  shouldBe "7893N"
      helper.removeCountryElementFromTaxCode("BR")     shouldBe "BR"
      helper.removeCountryElementFromTaxCode("NT")     shouldBe "NT"
      helper.removeCountryElementFromTaxCode("C1250 W1") shouldBe "1250 W1"
      helper.removeCountryElementFromTaxCode("C1250 M1") shouldBe "1250 M1"
      helper.removeCountryElementFromTaxCode("C1250M1") shouldBe "1250M1"
      helper.removeCountryElementFromTaxCode("C1250W1") shouldBe "1250W1"
      helper.removeCountryElementFromTaxCode("C1250L X") shouldBe "1250L X"
      helper.removeCountryElementFromTaxCode("C1250 X") shouldBe "1250 X"
      helper.removeCountryElementFromTaxCode("C1250LX") shouldBe "1250LX"
      helper.removeCountryElementFromTaxCode("C1250X") shouldBe "1250X"
    }
  }

  val split = Table(
    ("taxCode", "taxCalcResource", "expectedResult"),
    //2017-2018
    ("1150L", TaxYear_2017_2018, "1150"),
    ("1150", TaxYear_2017_2018, "1150"),
    //2018-2019
    ("1185L", TaxYear_2018_2019, "1185"),
    ("1185", TaxYear_2018_2019, "1185"),
    //2018-2019
    ("1250L", TaxYear_2019_2020, "1250"),
    ("1250", TaxYear_2019_2020, "1250"),
    //Emergency Tax
    ("1250 W1", TaxYear_2019_2020, "1250"),
    ("1250 M1", TaxYear_2019_2020, "1250"),
    ("1250 X", TaxYear_2019_2020, "1250"),
    ("1250W1", TaxYear_2019_2020, "1250"),
    ("1250M1", TaxYear_2019_2020, "1250"),
    ("1250X", TaxYear_2019_2020, "1250"),
    ("1250LX", TaxYear_2019_2020, "1250"),
    ("1250L X", TaxYear_2019_2020, "1250"),

    ("1500M X", TaxYear_2019_2020, "1500"),
    ("1500MX", TaxYear_2019_2020, "1500"),
    ("1500N X", TaxYear_2019_2020, "1500"),
    ("1500NX", TaxYear_2019_2020, "1500")
  )

  "TaxCalculatorHelper splitTaxCode" should {
    forAll(split) { (taxCode, taxCalcResource, expectedResult) =>
      s"Take $taxCode and returns $expectedResult" in new TaxCalculatorHelperSetup {
        helper.splitTaxCode(taxCode, taxCalcResource) shouldBe expectedResult
      }
    }
  }
}
