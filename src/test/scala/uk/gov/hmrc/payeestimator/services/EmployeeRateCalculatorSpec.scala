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

import org.scalatest.prop.TableDrivenPropertyChecks.forAll
import org.scalatest.prop.Tables.Table
import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain.{Money, TaxYear_2016_2017, TaxYear_2017_2018}

import scala.math.BigDecimal

class EmployeeRateCalculatorSpec extends WordSpecLike with Matchers {

  val input = Table(
    ("grossPay", "taxCalcResource", "limitId", "expectedAmount", "expectedPercentage"),
      (BigDecimal(100000.00), TaxYear_2016_2017, 1, 6.24,    12),
      (BigDecimal(100000.00), TaxYear_2016_2017, 3, 4186.56, 12),
      (BigDecimal(100000.00), TaxYear_2016_2017, 4, 1140.00, 2),
      (BigDecimal(100000.00), TaxYear_2017_2018, 1, 6.24,    12),
      (BigDecimal(100000.00), TaxYear_2017_2018, 3, 4186.56, 12),
      (BigDecimal(100000.00), TaxYear_2017_2018, 4, 1140.00, 2)

  )

  s"EmployeeRateCalculator.calculate()" should {
    forAll(input) {

      (grossPay, taxCalcResource, limitId, expectedAmount, expectedPercentage) =>

        s"calculate [$expectedAmount] in ${taxCalcResource.taxYear}, given grossPay[$grossPay], limitId[rate.$limitId]" in {
          val rate = EmployeeRateCalculator( Money(grossPay), limitId, taxCalcResource).calculate().result
          rate.amount shouldBe expectedAmount
          rate.percentage shouldBe expectedPercentage
        }
    }
  }
}
