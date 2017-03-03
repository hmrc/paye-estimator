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
      service.isValidTaxCode("1000L", taxCalcResource2016) shouldBe true
    }

    "return false for X1919 its pattern doesn't match ([0-9]{1,4}[L-N,l-n,T,t,X,x]{1}){1}" in new LivePAYETaxCalcServiceSuccess {
      service.isValidTaxCode("X1919", taxCalcResource2016) shouldBe false
    }

    "return false for 99999m its pattern doesn't match ([0-9]{1,4}[L-N,l-n,T,t,X,x]{1}){1}" in new LivePAYETaxCalcServiceSuccess {
      service.isValidTaxCode("99999M", taxCalcResource2016) shouldBe false
    }

    "return true for 0X which matched the pattern ([0-9]{1,4}[L-N,l-n,T,t,X,x]{1}){1}" in new LivePAYETaxCalcServiceSuccess {
      service.isValidTaxCode("0X", taxCalcResource2016) shouldBe false
    }
  }

  "PAYETaxCalculatorService.calculatePAYETaxablePay " should {
    "calculate annual taxable pay" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("1100T", Money(35002.32), taxCalcResource2016)

      result.taxablePay.value shouldBe 23993.32
    }
  }

  "PAYETaxCalculatorService.calculatePAYETax " should {
    "Calculate Annual PAYE tax for a gross salary of 35002.32 in tax band 2" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("1100T", Money(35002.32), taxCalcResource2016)
      result.payeTaxAmount.value shouldBe BigDecimal(4798.60)
    }

    "Calculate Annual PAYE tax for a gross salary of 70000.00 in tax band 3" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("1100T", Money(70000.00), taxCalcResource2016)
      result.payeTaxAmount.value shouldBe BigDecimal(17196.40)
    }

    "Calculate Annual PAYE tax for a gross salary of 200000.00 in tax band 4" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("1100T", Money(200000.00), taxCalcResource2016)
      result.payeTaxAmount.value shouldBe BigDecimal(71145.95)
    }
  }
}
