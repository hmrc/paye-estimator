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

class EmployeeRateCalculatorSpec extends WordSpecLike with Matchers {


  "EmployeeRateCalculator.calculate " should {
    "should calculate the annual rates correctly" in {
      val rate1 = EmployeeRateCalculator( Money(BigDecimal(100000.00)), 1, TaxYear_2016_2017).calculate().result
      val rate3 = EmployeeRateCalculator( Money(BigDecimal(100000.00)), 3, TaxYear_2016_2017).calculate().result
      val rate4 = EmployeeRateCalculator( Money(BigDecimal(100000.00)), 4, TaxYear_2016_2017).calculate().result

      rate1.amount shouldBe 6.24
      rate1.percentage shouldBe 12
      rate3.amount shouldBe 4186.56
      rate3.percentage shouldBe 12
      rate4.amount shouldBe 1140.00
      rate4.percentage shouldBe 2
    }
  }
}
