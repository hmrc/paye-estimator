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

import java.time.LocalDate
import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain.Money

class ExcessPayCalculatorSpec extends WordSpecLike with Matchers {

  "ExcessPayCalculator calculate " should {
    "should calculate the excess pay to be the full taxable pay amount if the applicable tax band is band 1" in new ExcessPayCalculatorSetup {
      override val taxCode: String = "1100T"
      override val date: LocalDate = LocalDate.now
      override val bandId: Int = 1
      override val taxablePay: Money = Money(BigDecimal(60000.00))

      val result = ExcessPayCalculator(taxCode, date, bandId, taxablePay).calculate().result
      result.value shouldBe 60000.00
    }

    "should calculate the excess pay to be the full taxable pay amount if the taxCode is BR1" in new ExcessPayCalculatorFullTaxableAmountSetup {
      override val taxCode = "BR1"
      val result = ExcessPayCalculator(taxCode, date, bandId, taxablePay).calculate().result
      result.value shouldBe 60000.00
    }

    "should calculate the excess pay to be the full taxable pay amount if the taxCode is D0" in new ExcessPayCalculatorFullTaxableAmountSetup {
      override val taxCode = "D0"
      val result = ExcessPayCalculator(taxCode, date, bandId, taxablePay).calculate().result
      result.value shouldBe 60000.00
    }

    "should calculate the excess pay to be the full taxable pay amount if the taxCode is D1" in new ExcessPayCalculatorFullTaxableAmountSetup {
      override val taxCode = "D0"
      val result = ExcessPayCalculator(taxCode, date, bandId, taxablePay).calculate().result
      result.value shouldBe 60000.00
    }

    "should calculate the excess pay to be the full taxable pay amount if an ordinary tax code and Band 2" in new ExcessPayCalculatorFullTaxableAmountSetup {
      override val taxCode = "1100T"
      override val bandId = 2
      val result = ExcessPayCalculator(taxCode, date, bandId, taxablePay).calculate().result
      result.value shouldBe 60000.00
    }

    "should calculate the excess pay to be the full taxable pay amount - previous period threshold if an ordinary tax code and Band 3" in new ExcessPayCalculatorFullTaxableAmountSetup {
      override val taxCode = "1100T"
      override val bandId = 3
      val result = ExcessPayCalculator(taxCode, date, bandId, taxablePay).calculate().result
      result.value shouldBe 28000.00
    }

    "should calculate the excess pay to be the full taxable pay amount - previous period threshold if an ordinary tax code and Band 4" in new ExcessPayCalculatorFullTaxableAmountSetup {
      override val taxCode = "1100T"
      override val bandId = 4
      override val taxablePay = Money(BigDecimal(200000.00))
      val result = ExcessPayCalculator(taxCode, date, bandId, taxablePay).calculate().result
      result.value shouldBe 50000.00
    }
  }
}
