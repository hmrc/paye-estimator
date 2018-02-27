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

import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain.{Money, TaxYearChanges}


class PAYETaxCalculatorServiceSpec extends WordSpecLike with Matchers  with TaxYearChanges{

  "PAYETaxCalculatorService.isValidTaxCode " should {
    "return true if pattern matches ([0-9]{1,4}[L-N,l-n,T,t,X,x]{1}){1}" in new LivePAYETaxCalcServiceSuccess {
      service.isValidTaxCode("1000L", taxCalcResource2017) shouldBe true
      service.isValidTaxCode("1000L", taxCalcResource2018) shouldBe true
    }

    "return false for X1919 its pattern doesn't match ([0-9]{1,4}[L-N,l-n,T,t,X,x]{1}){1}" in new LivePAYETaxCalcServiceSuccess {
      service.isValidTaxCode("X1919", taxCalcResource2017) shouldBe false
      service.isValidTaxCode("X1919", taxCalcResource2018) shouldBe false
    }

    "return false for 99999m its pattern doesn't match ([0-9]{1,4}[L-N,l-n,T,t,X,x]{1}){1}" in new LivePAYETaxCalcServiceSuccess {
      service.isValidTaxCode("99999M", taxCalcResource2017) shouldBe false
      service.isValidTaxCode("99999M", taxCalcResource2018) shouldBe false
    }

    "return true for 0X which matched the pattern ([0-9]{1,4}[L-N,l-n,T,t,X,x]{1}){1}" in new LivePAYETaxCalcServiceSuccess {
      service.isValidTaxCode("0X", taxCalcResource2017) shouldBe false
      service.isValidTaxCode("0X", taxCalcResource2018) shouldBe false
    }
  }

  "PAYETaxCalculatorService.calculatePAYETaxablePay " should {
    "calculate annual taxable pay for 2017" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("1100T", Money(35002.32), taxCalcResource2017)

      result.taxablePay.value shouldBe 23993.32
    }

    "calculate annual taxable pay for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("1185L", Money(44000.00), taxCalcResource2018)

      result.taxablePay.value shouldBe 32141.00
    }
  }

  "PAYETaxCalculatorService.calculatePAYETax " should {
    "Calculate Annual PAYE tax for a gross salary of 35002.32 in tax band 2 for 2017" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("1100T", Money(35002.32), taxCalcResource2017)
      result.payeTaxAmount.value shouldBe BigDecimal(4798.60)
    }

    "Calculate Annual PAYE tax for a gross salary of 44000.00 in tax band 2 for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("1185L", Money(44000.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(6428.20)
    }

    "Calculate Annual PAYE tax for a gross salary of 70000.00 in tax band 3 for 2017" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("1100T", Money(70000.00), taxCalcResource2017)
      result.payeTaxAmount.value shouldBe BigDecimal(16896.40)
    }

    "Calculate Annual PAYE tax for a gross salary of 75500.00 in tax band 3 for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("980L", Money(75500.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(19376.40)
    }

    "Calculate Annual PAYE tax for a gross salary of 200000.00 in tax band 4 for 2017" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("1100T", Money(200000.00), taxCalcResource2017)
      result.payeTaxAmount.value shouldBe BigDecimal(70845.95)
    }

    "Calculate Annual PAYE tax for a gross salary of 200000.00 in tax band 4 for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("1185L", Money(119000.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(39756.40)
    }

    "Calculate Annual PAYE tax for a gross salary of 45000.00 with tax code K100 for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("K100", Money(45000.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(11503.60)
    }

    "Calculate Annual PAYE tax for a gross salary of 45000.00 with No Tax code (NT) for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("NT", Money(50000.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(0.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 60000.00 with No Tax allowance code (0T) for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("0T", Money(60000.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(17100.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 45000.00 with Tax code BR for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("BR", Money(10000.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(2000.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 80000.00 with Tax code D0 for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("D0", Money(80000.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(32000.00)
    }

    "Calculate Annual PAYE tax for a gross salary of 80000.00 with Tax code D1 for 2018" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("D1", Money(80000.00), taxCalcResource2018)
      result.payeTaxAmount.value shouldBe BigDecimal(36000.00)
    }
  }
}
