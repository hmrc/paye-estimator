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

import scala.math.BigDecimal

class EmployerRateCalculatorSpec extends WordSpecLike with Matchers {

  "EmployerRateCalculatorSpec.calculate " should {
    "should calculate the annual rate" in {
      val rate2 = EmployerRateCalculator( Money(BigDecimal(100000.00)), 2, TaxYear_2016_2017).calculate().result
      val rate3 = EmployerRateCalculator( Money(BigDecimal(100000.00)), 3, TaxYear_2016_2017).calculate().result

      rate2.amount shouldBe 4814.54
      rate2.percentage shouldBe 13.8
      rate3.amount shouldBe 7866.00
      rate3.percentage shouldBe 13.8
    }
  }
}
