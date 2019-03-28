/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.payeestimator.services

import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.Tables.Table
import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain._

class PAYETaxCalculatorServiceSpec extends WordSpecLike with Matchers with TaxYearChanges {

  "PAYETaxCalculatorService.isValidTaxCode " should {
    "return true if pattern matches ([0-9]{1,4}[L-N,l-n,T,t,X,x]{1}){1}" in new LivePAYETaxCalcServiceSuccess {
      service.isValidTaxCode("1000L", taxCalcResource2017) shouldBe true
      service.isValidTaxCode("1000L", taxCalcResource2018) shouldBe true
      service.isValidTaxCode("1000L", taxCalcResource2019) shouldBe true

      service.isValidTaxCode("1000L", taxCalcResource2017Scottish) shouldBe true
      service.isValidTaxCode("1000L", taxCalcResource2018Scottish) shouldBe true
      service.isValidTaxCode("1000L", taxCalcResource2019Scottish) shouldBe true
    }

    "return false for X1919 its pattern doesn't match ([0-9]{1,4}[L-N,l-n,T,t,X,x]{1}){1}" in new LivePAYETaxCalcServiceSuccess {
      service.isValidTaxCode("X1919", taxCalcResource2017) shouldBe false
      service.isValidTaxCode("X1919", taxCalcResource2018) shouldBe false
      service.isValidTaxCode("X1919", taxCalcResource2019) shouldBe false

      service.isValidTaxCode("X1919", taxCalcResource2017Scottish) shouldBe false
      service.isValidTaxCode("X1919", taxCalcResource2018Scottish) shouldBe false
      service.isValidTaxCode("X1919", taxCalcResource2019Scottish) shouldBe false
    }

    "return false for 99999m its pattern doesn't match ([0-9]{1,4}[L-N,l-n,T,t,X,x]{1}){1}" in new LivePAYETaxCalcServiceSuccess {
      service.isValidTaxCode("99999M", taxCalcResource2017) shouldBe false
      service.isValidTaxCode("99999M", taxCalcResource2018) shouldBe false
      service.isValidTaxCode("99999M", taxCalcResource2019) shouldBe false

      service.isValidTaxCode("99999M", taxCalcResource2017Scottish) shouldBe false
      service.isValidTaxCode("99999M", taxCalcResource2018Scottish) shouldBe false
      service.isValidTaxCode("99999M", taxCalcResource2019Scottish) shouldBe false
    }

    "return false for 0X which matched the pattern ([0-9]{1,4}[L-N,l-n,T,t,X,x]{1}){1}" in new LivePAYETaxCalcServiceSuccess {
      service.isValidTaxCode("0X", taxCalcResource2017) shouldBe false
      service.isValidTaxCode("0X", taxCalcResource2018) shouldBe false
      service.isValidTaxCode("0X", taxCalcResource2019) shouldBe false

      service.isValidTaxCode("0X", taxCalcResource2017Scottish) shouldBe false
      service.isValidTaxCode("0X", taxCalcResource2018Scottish) shouldBe false
      service.isValidTaxCode("0X", taxCalcResource2019Scottish) shouldBe false
    }

    val otherCodes = Table(
      ("code", "resource", "valid"),
      ("1250MX", TaxYear_2017_2018(), false),
      ("1250MX", TaxYear_2018_2019(), false),
      ("1250MX", TaxYear_2019_2020(), true),
      ("C1250MX", TaxYear_2019_2020(), true),

      ("BRX", TaxYear_2017_2018(), true),
      ("BRX", TaxYear_2018_2019(), true),
      ("BRX", TaxYear_2019_2020(), true),
      ("CBRX", TaxYear_2019_2020(), true),

      ("D0X", TaxYear_2017_2018(), true),
      ("D0X", TaxYear_2018_2019(), true),
      ("D0X", TaxYear_2019_2020(), true),
      ("CD0X", TaxYear_2019_2020(), true),

      ("K100X", TaxYear_2017_2018(), false),
      ("K100X", TaxYear_2018_2019(), false),
      ("K100X", TaxYear_2019_2020(), true),

      ("S1250MX", TaxYear_2017_2018(true), true),
      ("S1250MX", TaxYear_2018_2019(true), true),
      ("S1250MX", TaxYear_2019_2020(true), true),

      ("SBRX", TaxYear_2017_2018(true), true),
      ("SBRX", TaxYear_2018_2019(true), true),
      ("SBRX", TaxYear_2019_2020(true), true),

      ("SD0X", TaxYear_2017_2018(true), true),
      ("SD0X", TaxYear_2018_2019(true), true),
      ("SD0X", TaxYear_2019_2020(true), true),

      ("SK100X", TaxYear_2017_2018(true), true),
      ("SK100X", TaxYear_2018_2019(true), true),
      ("SK100X", TaxYear_2019_2020(true), true),

      ("S1250M1", TaxYear_2017_2018(true), true),
      ("S1250M1", TaxYear_2018_2019(true), true),
      ("S1250M1", TaxYear_2019_2020(true), true),

      ("S1250W1", TaxYear_2017_2018(true), true),
      ("S1250W1", TaxYear_2018_2019(true), true),
      ("S1250W1", TaxYear_2019_2020(true), true),

      ("S1250 M1", TaxYear_2017_2018(true), true),
      ("S1250 M1", TaxYear_2018_2019(true), true),
      ("S1250 M1", TaxYear_2019_2020(true), true),

      ("S1250 W1", TaxYear_2017_2018(true), true),
      ("S1250 W1", TaxYear_2018_2019(true), true),
      ("S1250 W1", TaxYear_2019_2020(true), true)
    )

    forAll(otherCodes) { (taxCode, resource, expectedResult) =>
      s"Other random tax codes that need to work that were found as part of the 2019-2020 updates testing " +
        s"$taxCode for ${if (resource.isScottish) "Scottish" else "English/Welsh"} ${resource.startDate.getYear} expected it to $expectedResult" in new LivePAYETaxCalcServiceSuccess {
        service.isValidTaxCode(taxCode, resource) shouldBe expectedResult
      }
    }
  }

  "PAYETaxCalculatorService.calculatePAYETaxablePay " should {
    "calculate annual taxable pay for 2017" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("1100T", Money(35002.32), taxCalcResource2017)

      result.taxablePay.value shouldBe 23993.32
    }

    "calculate annual taxable pay for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("1185L", Money(44000.00), taxCalcResource2018)

      result.taxablePay.value shouldBe 32141.00
    }

    "calculate annual taxable pay for 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("1250L", Money(44000.00), taxCalcResource2018)

      result.taxablePay.value shouldBe 31491.0
    }
  }

  "PAYETaxCalculatorService.calculatePAYETax " should {
    "Calculate Annual PAYE tax for a gross salary of 35002.32 in tax band 2 for 2017" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("1100T", Money(35002.32), taxCalcResource2017)
      result.payeTaxAmount.value shouldBe BigDecimal(4798.60)
    }

    "Calculate Annual PAYE tax for a gross salary of 44000.00 in tax band 2 for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("1185L", Money(44000.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(6428.20)
    }

    "Calculate Annual PAYE tax for a gross salary of 44000.00 in tax band 2 for 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("1250L", Money(44000.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(6298.20)
    }

    "Calculate Annual PAYE tax for a gross salary of 70000.00 in tax band 3 for 2017" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("1100T", Money(70000.00), taxCalcResource2017)
      result.payeTaxAmount.value shouldBe BigDecimal(16896.40)
    }

    "Calculate Annual PAYE tax for a gross salary of 75500.00 in tax band 3 for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("980L", Money(75500.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(19376.40)
    }

    "Calculate Annual PAYE tax for a gross salary of 200000.00 in tax band 4 for 2017" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("1100L", Money(200000.00), taxCalcResource2017)
      result.payeTaxAmount.value shouldBe BigDecimal(70845.95)
    }

    "Calculate Annual PAYE tax for a gross salary of 200000.00 in tax band 4 for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("1185L", Money(200000.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(75600.0)
    }

    "Calculate Annual PAYE tax for a gross salary of 200000.00 in tax band 4 for 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("1250L", Money(200000.00), taxCalcResource2019)
      result.payeTaxAmount.value shouldBe BigDecimal(75000.0)
    }
    "Calculate Annual PAYE tax for a gross salary of 1190000.00 in tax band 4 for 2017" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("1100L", Money(119000.00), taxCalcResource2017)
      result.payeTaxAmount.value shouldBe BigDecimal(36496.40)
    }

    "Calculate Annual PAYE tax for a gross salary of 1190000.00 in tax band 4 for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("1185L", Money(119000.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(39756.40)
    }

    "Calculate Annual PAYE tax for a gross salary of 1190000.00 in tax band 4 for 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("1250L", Money(119000.00), taxCalcResource2019)
      result.payeTaxAmount.value shouldBe BigDecimal(38896.40)
    }

    "Calculate Annual PAYE tax for a gross salary of 45000.00 with tax code K100 for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("K100", Money(45000.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(11503.60)
    }

    "Calculate Annual PAYE tax for a gross salary of 45000.00 with No Tax code (NT) for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("NT", Money(50000.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(0.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 60000.00 with No Tax allowance code (0T) for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("0T", Money(60000.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(17100.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 45000.00 with Tax code BR for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("BR", Money(10000.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(2000.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 80000.00 with Tax code D0 for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("D0", Money(80000.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(32000.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 80000.00 with Tax code D1 for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("D1", Money(80000.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(36000.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 43000.00 in Scottish tax band 4 for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("1185L", Money(43000.00), taxCalcResource2018Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(6398.11)
    }

    "Calculate Annual PAYE tax for a gross salary of 75500.00 in Scottish tax band 5 for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("980L", Money(75500.00), taxCalcResource2018Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(20475.81)
    }

    "Calculate Annual PAYE tax for a gross salary of 119000.00 in Scottish tax band 5 for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("1185L", Money(119000.00), taxCalcResource2018Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(41365.31)
    }

    "Calculate Annual PAYE tax for a gross salary of 160000.00 in Scottish tax band 6 for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("1185L", Money(160000.00), taxCalcResource2018Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(59642.50)
    }

    "Calculate Annual PAYE tax for a gross salary of 45000.00 K code (untaxed income) Scottish for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("K100", Money(45000.00), taxCalcResource2018Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(12406.19)
    }

    "Calculate Annual PAYE tax for a gross salary of 50000.00 in Scottish No Tax (NT) code 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("NT", Money(50000.00), taxCalcResource2018Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(0.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 60000.00 in Scottish 0T (no personal allowance) code 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("0T", Money(60000.00), taxCalcResource2018Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(18142.50)
    }

    "Calculate Annual PAYE tax for a gross salary of 10000.00 in Scottish BR (basic rate) code 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("BR", Money(10000.00), taxCalcResource2018Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(2000.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 12000.00 in Scottish D0 (basic rate) code 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("D0", Money(12000.00), taxCalcResource2018Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(2520.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 12000.00 in Scottish D1 (basic rate) code 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("D1", Money(12000.00), taxCalcResource2018Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(4920.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 12000.00 in Scottish D2 (basic rate) code 2018" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("D2", Money(12000.00), taxCalcResource2018Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(5520.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 45000.00 with tax code K100 for 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("K100", Money(45000.00), taxCalcResource2019)
      result.payeTaxAmount.value shouldBe BigDecimal(10903.60)
    }

    "Calculate Annual PAYE tax for a gross salary of 45000.00 with No Tax code (NT) for 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("NT", Money(50000.00), taxCalcResource2019)
      result.payeTaxAmount.value shouldBe BigDecimal(0.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 60000.00 with No Tax allowance code (0T) for 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("0T", Money(60000.00), taxCalcResource2019)
      result.payeTaxAmount.value shouldBe BigDecimal(16500.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 45000.00 with Tax code BR for 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("BR", Money(10000.00), taxCalcResource2019)
      result.payeTaxAmount.value shouldBe BigDecimal(2000.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 80000.00 with Tax code D0 for 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("D0", Money(80000.00), taxCalcResource2019)
      result.payeTaxAmount.value shouldBe BigDecimal(32000.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 80000.00 with Tax code D1 for 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("D1", Money(80000.00), taxCalcResource2019)
      result.payeTaxAmount.value shouldBe BigDecimal(36000.00)
    }

    // No need to test country prefixes as these have been stripped out by this point
    val emergencyTax = Table(
      ("taxCode", "Earnings", "totalPayeAmount", "taxResource"),
      ("1250 W1", 20000.00, 1498.20, TaxYear_2019_2020()),
      ("1250W1", 20000.00, 1498.20, TaxYear_2019_2020()),
      ("1250 M1", 20000.00, 1498.20, TaxYear_2019_2020()),
      ("1250M1", 20000.00, 1498.20, TaxYear_2019_2020()),
      ("1250L X", 20000.00, 1498.20, TaxYear_2019_2020()),
      ("1250LX", 20000.00, 1498.20, TaxYear_2019_2020()),
      ("1250 X", 20000.00, 1498.20, TaxYear_2019_2020()),
      ("1250X", 20000.00, 1498.20, TaxYear_2019_2020()),
      ("1250 W1", 20000.00, 1477.71, TaxYear_2019_2020(true)),
      ("1250W1", 20000.00, 1477.71, TaxYear_2019_2020(true)),
      ("1250 M1", 20000.00, 1477.71, TaxYear_2019_2020(true)),
      ("1250M1", 20000.00, 1477.71, TaxYear_2019_2020(true)),
      ("1250L X", 20000.00, 1477.71, TaxYear_2019_2020(true)),
      ("1250LX", 20000.00, 1477.71, TaxYear_2019_2020(true)),
      ("1250 X", 20000.00, 1477.71, TaxYear_2019_2020(true)),
      ("1250X", 20000.00, 1477.71, TaxYear_2019_2020(true)),
      ("1500MX", 20000.00, 998.20, TaxYear_2019_2020()),
      ("1500NX", 20000.00, 998.20, TaxYear_2019_2020()),
      ("BRX", 20000.00, 4000.00, TaxYear_2019_2020()),
      ("D0X", 20000.00, 8000.00, TaxYear_2019_2020()),
      ("K100X", 20000.00, 3798.20, TaxYear_2019_2020()),
      ("BRX", 20000.00, 4000.00, TaxYear_2019_2020(true)),
      ("D0X", 20000.00, 4200.00, TaxYear_2019_2020(true)),
      ("K100X", 20000.00, 3843.18, TaxYear_2019_2020(true)),
      ("1500MX", 20000.00, 977.71, TaxYear_2019_2020(true)),
      ("1500NX", 20000.00, 977.71, TaxYear_2019_2020(true))
    )

    s"Emergency Tax Calc" should {
      forAll(emergencyTax) { (taxCode, earnings, totalPayeAmount, taxResource) =>
        s"return $totalPayeAmount for ${if (taxResource.isScottish) "Scottish" else "English/Welsh"} for $taxCode" in new LivePAYETaxCalcServiceSuccess {
          val result: PAYETaxResult = service.calculatePAYETax(taxCode, Money(earnings), taxResource)
          result.payeTaxAmount.value shouldBe BigDecimal(totalPayeAmount)
        }
      }
    }

    "Calculate Annual PAYE tax for a gross salary of 75500.00 in Scottish tax band 5 for 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("980L", Money(75500.00), taxCalcResource2019Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(20602.38)
    }

    "Calculate Annual PAYE tax for a gross salary of 119000.00 in Scottish tax band 5 for 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("1250L", Money(119000.00), taxCalcResource2019Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(41225.38)
    }

    "Calculate Annual PAYE tax for a gross salary of 160000.00 in Scottish tax band 6 for 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("1250L", Money(160000.00), taxCalcResource2019Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(59769.07)
    }

    "Calculate Annual PAYE tax for a gross salary of 45000.00 K code (untaxed income) Scottish for 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("K100", Money(45000.00), taxCalcResource2019Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(12532.76)
    }

    "Calculate Annual PAYE tax for a gross salary of 50000.00 in Scottish No Tax (NT) code 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("NT", Money(50000.00), taxCalcResource2019Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(0.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 60000.00 in Scottish 0T (no personal allowance) code 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("0T", Money(60000.00), taxCalcResource2019Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(18269.07)
    }

    "Calculate Annual PAYE tax for a gross salary of 10000.00 in Scottish BR (basic rate) code 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("BR", Money(10000.00), taxCalcResource2019Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(2000.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 12000.00 in Scottish D0 (basic rate) code 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("D0", Money(12000.00), taxCalcResource2019Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(2520.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 12000.00 in Scottish D1 (basic rate) code 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("D1", Money(12000.00), taxCalcResource2019Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(4920.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 12000.00 in Scottish D2 (basic rate) code 2019" in new LivePAYETaxCalcServiceSuccess {
      val result: PAYETaxResult = service.calculatePAYETax("D2", Money(12000.00), taxCalcResource2019Scottish)
      result.payeTaxAmount.value shouldBe BigDecimal(5520.00)
    }

  }
}
