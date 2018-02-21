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

class TaxBandCalculatorSpec extends WordSpecLike with Matchers with TaxYearChanges {
  "for 2017_2018 TaxBandCalculatorSpec.calculate " should {
    val taxYear = new TaxYear_2017_2018(false)

    "return annual taxBand 2 " in {
      val result = TaxBandCalculator("1100T", Money(BigDecimal(23993.32)), taxYear).calculate().result
      result.band shouldBe 2
    }
    "return annual taxBand 3" in {
      val result = TaxBandCalculator("1100T", Money(BigDecimal(58991.00)), taxYear).calculate().result
      result.band shouldBe 3
    }
    "return annual taxBand 4" in {
      val result = TaxBandCalculator("1100T", Money(BigDecimal(188991.00)), taxYear).calculate().result
      result.band shouldBe 4
    }
    "return taxBand 2 for TaxCode BR regardless of the gross pay amount" in {
      val result = TaxBandCalculator("BR", Money(BigDecimal(999999.99)), taxYear).calculate().result
      result.band shouldBe 2
    }
    "return taxBand 3 for TaxCode D0 regardless of the gross pay amount" in {
      val result = TaxBandCalculator("D0", Money(BigDecimal(10.99)), taxYear).calculate().result
      result.band shouldBe 3
    }
    "return taxBand 4 for TaxCode D1 regardless of the gross pay amount" in {
      val result = TaxBandCalculator("D1", Money(BigDecimal(1.00)), taxYear).calculate().result
      result.band shouldBe 4
    }
  }

  "for 2018_2019 TaxBandCalculatorSpec.calculate " should {
    val taxYear = new TaxYear_2018_2019(false)

    "return annual taxBand 2" in {
      val result = TaxBandCalculator("1100T", Money(BigDecimal(23993.32)), taxYear).calculate().result
      result.band shouldBe 2
    }
    "return annual taxBand 3" in {
      val result = TaxBandCalculator("1100T", Money(BigDecimal(58991.00)), taxYear).calculate().result
      result.band shouldBe 3
    }
    "return annual taxBand 4" in {
      val result = TaxBandCalculator("1100T", Money(BigDecimal(188991.00)), taxYear).calculate().result
      result.band shouldBe 4
    }
    "return taxBand 2 for TaxCode BR regardless of the gross pay amount" in {
      val result = TaxBandCalculator("BR", Money(BigDecimal(999999.99)), taxYear).calculate().result
      result.band shouldBe 2
    }
    "return taxBand 3 for TaxCode D0 regardless of the gross pay amount" in {
      val result = TaxBandCalculator("D0", Money(BigDecimal(10.99)), taxYear).calculate().result
      result.band shouldBe 3
    }
    "return taxBand 4 for TaxCode D1 regardless of the gross pay amount" in {
      val result = TaxBandCalculator("D1", Money(BigDecimal(1.00)), taxYear).calculate().result
      result.band shouldBe 4
    }
  }

}
