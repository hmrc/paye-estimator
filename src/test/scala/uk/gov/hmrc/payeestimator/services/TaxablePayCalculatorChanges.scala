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
import uk.gov.hmrc.payeestimator.domain._

class TaxablePayCalculatorChanges extends WordSpecLike with Matchers with TaxYearChanges {

  "TaxablePayCalculator calculate " should {
    "calculate annual taxable pay for 2017_2018" in {
      val result = TaxablePayCalculator("1100T", Money(BigDecimal(35002.32)), TaxYear_2017_2018()).calculate().result
      result.value shouldBe 23993.32
    }

    "calculate annual taxable pay for 2018_2019" in {
      val result = TaxablePayCalculator("1100T", Money(BigDecimal(35002.32)), TaxYear_2018_2019()).calculate().result
      result.value shouldBe 23993.32
    }

    "calculate annual taxable pay for 2018_2019 Scotland" in {
      val result = TaxablePayCalculator("1100T", Money(BigDecimal(35002.32)), TaxYear_2018_2019(true)).calculate().result
      result.value shouldBe 23993.32
    }

    "calculate annual taxable pay for 2019_2020" in {
      val result = TaxablePayCalculator("1250L", Money(BigDecimal(35002.32)), TaxYear_2019_2020()).calculate().result
      result.value shouldBe 22493.32
    }

    "calculate annual taxable pay for 2019_2020 Scotland" in {
      val result = TaxablePayCalculator("1250L", Money(BigDecimal(35002.32)), TaxYear_2019_2020(true)).calculate().result
      result.value shouldBe 22493.32
    }

  }
}
