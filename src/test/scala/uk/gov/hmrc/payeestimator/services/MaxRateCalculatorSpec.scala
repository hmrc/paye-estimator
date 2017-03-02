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
import uk.gov.hmrc.payeestimator.domain.{Money, TaxYear_2016_2017}

class MaxRateCalculatorSpec extends WordSpecLike with Matchers {

  "MaxRateCalculator calculate " should {
    "should calculate the max rate to be applied for PAYE, max rate applies" in  {
      val result = MaxRateCalculator(Money(BigDecimal(8000.00)), Money(BigDecimal(10000.00)), TaxYear_2016_2017).calculate().result
      result.value shouldBe 5000.00
    }

    "should calculate the max rate to be applied for PAYE, NO max rate applies here" in  {
      val result = MaxRateCalculator(Money(BigDecimal(8000.00)), Money(BigDecimal(20000.00)), TaxYear_2016_2017).calculate().result
      result.value shouldBe -1
    }
  }
}
