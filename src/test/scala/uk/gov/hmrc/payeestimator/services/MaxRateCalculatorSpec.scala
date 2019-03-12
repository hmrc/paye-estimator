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

class MaxRateCalculatorSpec extends WordSpecLike with Matchers  with TaxYearChanges {

  val TaxYear_2017_2018 = new TaxYear_2017_2018(false)
  val TaxYear_2018_2019 = new TaxYear_2018_2019(false)
  val TaxYear_2019_2020 = new TaxYear_2019_2020(false)

  import org.scalatest.prop.TableDrivenPropertyChecks._

  val input = Table(
    ("payeAmount", "grossPay", "taxCalcResource", "expectedResult"),
    (BigDecimal(8000.00), BigDecimal(10000.00), TaxYear_2017_2018, BigDecimal(5000.00)),
    (BigDecimal(8000.00), BigDecimal(20000.00), TaxYear_2017_2018, BigDecimal(-1)),
    (BigDecimal(8000.00), BigDecimal(10000.00), TaxYear_2018_2019, BigDecimal(5000.00)),
    (BigDecimal(8000.00), BigDecimal(20000.00), TaxYear_2018_2019, BigDecimal(-1)),
    (BigDecimal(8000.00), BigDecimal(10000.00), TaxYear_2019_2020, BigDecimal(5000.00)),
    (BigDecimal(8000.00), BigDecimal(20000.00), TaxYear_2019_2020, BigDecimal(-1))
  )

  s"MaxRateCalculator calculate() " should {
    forAll(input) {
      (payeAmount, grossPay, taxCalcResource, expectedResult) =>
        s"should return $expectedResult for taxYear[${taxCalcResource.taxYear}], when paye[$payeAmount] and grossYear[$grossPay] " in {
          val result = MaxRateCalculator(Money(payeAmount), Money(grossPay), taxCalcResource).calculate().result
          result.value shouldBe expectedResult
        }
    }
  }
}
