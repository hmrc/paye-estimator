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
import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain.{Money, TaxYear_2016_2017}

class ExcessPayCalculatorSpec extends WordSpecLike with Matchers {

  val input = Table(
    ("taxCode",  "taxCalcResource", "bandId", "taxablePay", "taxYear", "expectedResult"),
    ("1100T",  TaxYear_2016_2017, 1, Money(60000.00), "2016/17", Money(60000.00)),
    ("BR1",    TaxYear_2016_2017, 1, Money(60000.00), "2016/17", Money(60000.00)),
    ("D0",     TaxYear_2016_2017, 1, Money(60000.00), "2016/17", Money(60000.00)),
    ("D1",     TaxYear_2016_2017, 1, Money(60000.00), "2016/17", Money(60000.00)),
    ("1100T",  TaxYear_2016_2017, 2, Money(60000.00), "2016/17", Money(60000.00)),
    ("1100T",  TaxYear_2016_2017, 3, Money(60000.00), "2016/17", Money(28000.00)),
    ("1100T",  TaxYear_2016_2017, 4, Money(200000.00), "2016/17", Money(50000.00))
  )

  "ExcessPayCalculator calculate()" should {
    forAll(input) {
      (taxCode,  taxCalcResource, bandId, taxablePay, taxYear, expectedResult) =>

        s"In $taxYear for $taxCode calculate the excess pay to be ${expectedResult.value} " +
          s"for a grossPay amount of ${taxablePay.value} when the applicable tax band is $bandId" in {

          val result = ExcessPayCalculator(taxCode, bandId, taxablePay, taxCalcResource).calculate().result
          result.value shouldBe expectedResult.value
        }
    }
  }
}
