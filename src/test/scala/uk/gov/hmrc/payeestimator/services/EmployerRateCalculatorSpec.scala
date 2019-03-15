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
import uk.gov.hmrc.payeestimator.domain._

import scala.math.BigDecimal

class EmployerRateCalculatorSpec extends WordSpecLike with Matchers with TaxYearChanges {

  val TaxYear_2017_2018 = new TaxYear_2017_2018(isScottish = false)
  val TaxYear_2018_2019 = new TaxYear_2018_2019(isScottish = false)
  val TaxYear_2019_2020 = new TaxYear_2019_2020(isScottish = false)

  val Scottish_TaxYear_2017_2018 = new TaxYear_2017_2018(isScottish = true)
  val Scottish_TaxYear_2018_2019 = new TaxYear_2018_2019(isScottish = true)
  val Scottish_TaxYear_2019_2020 = new TaxYear_2019_2020(isScottish = true)

  val input = Table(
    ("grossPay", "taxCalcResource", "limitId", "expectedAmount", "expectedPercentage"),
    //2017-2018
    (BigDecimal(100000.00), TaxYear_2017_2018, 2, 5083.37, 13.8),
    (BigDecimal(100000.00), TaxYear_2017_2018, 3, 7590.00, 13.8),
    (BigDecimal(100000.00), Scottish_TaxYear_2017_2018, 2, 5083.37, 13.8),
    (BigDecimal(100000.00), Scottish_TaxYear_2017_2018, 3, 7590.00, 13.8),
    //2018-2019
    (BigDecimal(43000.00), TaxYear_2018_2019, 2, 4771.49, 13.8),
    (BigDecimal(43000.00), TaxYear_2018_2019, 3, 0.00, 13.8),
    (BigDecimal(75500.00), TaxYear_2018_2019, 2, 5233.79, 13.8),
    (BigDecimal(75500.00), TaxYear_2018_2019, 3, 4022.70, 13.8),
    (BigDecimal(119000.00), TaxYear_2018_2019, 2, 5233.79, 13.8),
    (BigDecimal(119000.00), TaxYear_2018_2019, 3, 10025.70, 13.8),
    (BigDecimal(160000.00), TaxYear_2018_2019, 2, 5233.79, 13.8),
    (BigDecimal(160000.00), TaxYear_2018_2019, 3, 15683.70, 13.8),
    (BigDecimal(45000.00), TaxYear_2018_2019, 2, 5047.49, 13.8),
    (BigDecimal(45000.00), TaxYear_2018_2019, 3, 0.00, 13.8),
    (BigDecimal(50000.00), TaxYear_2018_2019, 2, 5233.79, 13.8),
    (BigDecimal(50000.00), TaxYear_2018_2019, 3, 503.70, 13.8),
    (BigDecimal(60000.00), TaxYear_2018_2019, 2, 5233.79, 13.8),
    (BigDecimal(60000.00), TaxYear_2018_2019, 3, 1883.70, 13.8),
    (BigDecimal(10000.00), TaxYear_2018_2019, 2, 217.49, 13.8),
    (BigDecimal(10000.00), TaxYear_2018_2019, 3, 0.00, 13.8),
    (BigDecimal(12000.00), TaxYear_2018_2019, 2, 493.49, 13.8),
    (BigDecimal(12000.00), TaxYear_2018_2019, 3, 0.00, 13.8),
    (BigDecimal(43000.00), Scottish_TaxYear_2018_2019, 2, 4771.49, 13.8),
    (BigDecimal(43000.00), Scottish_TaxYear_2018_2019, 3, 0.00, 13.8),
    (BigDecimal(75500.00), Scottish_TaxYear_2018_2019, 2, 5233.79, 13.8),
    (BigDecimal(75500.00), Scottish_TaxYear_2018_2019, 3, 4022.70, 13.8),
    (BigDecimal(119000.00), Scottish_TaxYear_2018_2019, 2, 5233.79, 13.8),
    (BigDecimal(119000.00), Scottish_TaxYear_2018_2019, 3, 10025.70, 13.8),
    (BigDecimal(160000.00), Scottish_TaxYear_2018_2019, 2, 5233.79, 13.8),
    (BigDecimal(160000.00), Scottish_TaxYear_2018_2019, 3, 15683.70, 13.8),
    (BigDecimal(45000.00), Scottish_TaxYear_2018_2019, 2, 5047.49, 13.8),
    (BigDecimal(45000.00), Scottish_TaxYear_2018_2019, 3, 0.00, 13.8),
    (BigDecimal(50000.00), Scottish_TaxYear_2018_2019, 2, 5233.79, 13.8),
    (BigDecimal(50000.00), Scottish_TaxYear_2018_2019, 3, 503.70, 13.8),
    (BigDecimal(60000.00), Scottish_TaxYear_2018_2019, 2, 5233.79, 13.8),
    (BigDecimal(60000.00), Scottish_TaxYear_2018_2019, 3, 1883.70, 13.8),
    (BigDecimal(10000.00), Scottish_TaxYear_2018_2019, 2, 217.49, 13.8),
    (BigDecimal(10000.00), Scottish_TaxYear_2018_2019, 3, 0.00, 13.8),
    (BigDecimal(12000.00), Scottish_TaxYear_2018_2019, 2, 493.49, 13.8),
    (BigDecimal(12000.00), Scottish_TaxYear_2018_2019, 3, 0.00, 13.8),
    //2019-2020
    (BigDecimal(43000.00), TaxYear_2019_2020, 2, 4742.78, 13.8),
    (BigDecimal(43000.00), TaxYear_2019_2020, 3, 0.00, 13.8),
    (BigDecimal(75500.00), TaxYear_2019_2020, 2, 5708.78, 13.8),
    (BigDecimal(75500.00), TaxYear_2019_2020, 3, 3519.00, 13.8),
    (BigDecimal(119000.00), TaxYear_2019_2020, 2, 5708.78, 13.8),
    (BigDecimal(119000.00), TaxYear_2019_2020, 3, 9522.00, 13.8),
    (BigDecimal(160000.00), TaxYear_2019_2020, 2, 5708.78, 13.8),
    (BigDecimal(160000.00), TaxYear_2019_2020, 3, 15180.00, 13.8),
    (BigDecimal(45000.00), TaxYear_2019_2020, 2, 5018.78, 13.8),
    (BigDecimal(45000.00), TaxYear_2019_2020, 3, 0.00, 13.8),
    (BigDecimal(50000.00), TaxYear_2019_2020, 2, 5708.78, 13.8),
    (BigDecimal(50000.00), TaxYear_2019_2020, 3, 0, 13.8),
    (BigDecimal(60000.00), TaxYear_2019_2020, 2, 5708.78, 13.8),
    (BigDecimal(60000.00), TaxYear_2019_2020, 3, 1380.00, 13.8),
    (BigDecimal(10000.00), TaxYear_2019_2020, 2, 188.78, 13.8),
    (BigDecimal(10000.00), TaxYear_2019_2020, 3, 0.00, 13.8),
    (BigDecimal(12000.00), TaxYear_2019_2020, 2, 464.78, 13.8),
    (BigDecimal(12000.00), TaxYear_2019_2020, 3, 0.00, 13.8),
    (BigDecimal(43000.00), Scottish_TaxYear_2019_2020, 2, 4742.78, 13.8),
    (BigDecimal(43000.00), Scottish_TaxYear_2019_2020, 3, 0.00, 13.8),
    (BigDecimal(75500.00), Scottish_TaxYear_2019_2020, 2, 5708.78, 13.8),
    (BigDecimal(75500.00), Scottish_TaxYear_2019_2020, 3, 3519.00, 13.8),
    (BigDecimal(119000.00), Scottish_TaxYear_2019_2020, 2, 5708.78, 13.8),
    (BigDecimal(119000.00), Scottish_TaxYear_2019_2020, 3, 9522.00, 13.8),
    (BigDecimal(160000.00), Scottish_TaxYear_2019_2020, 2, 5708.78, 13.8),
    (BigDecimal(160000.00), Scottish_TaxYear_2019_2020, 3, 15180.00, 13.8),
    (BigDecimal(45000.00), Scottish_TaxYear_2019_2020, 2, 5018.78, 13.8),
    (BigDecimal(45000.00), Scottish_TaxYear_2019_2020, 3, 0.00, 13.8),
    (BigDecimal(50000.00), Scottish_TaxYear_2019_2020, 2, 5708.78, 13.8),
    (BigDecimal(50000.00), Scottish_TaxYear_2019_2020, 3, 0, 13.8),
    (BigDecimal(60000.00), Scottish_TaxYear_2019_2020, 2, 5708.78, 13.8),
    (BigDecimal(60000.00), Scottish_TaxYear_2019_2020, 3, 1380.0, 13.8),
    (BigDecimal(10000.00), Scottish_TaxYear_2019_2020, 2, 188.78, 13.8),
    (BigDecimal(10000.00), Scottish_TaxYear_2019_2020, 3, 0.00, 13.8),
    (BigDecimal(12000.00), Scottish_TaxYear_2019_2020, 2, 464.78, 13.8),
    (BigDecimal(12000.00), Scottish_TaxYear_2019_2020, 3, 0.00, 13.8)
  )

  s"EmployerRateCalculatorSpec.calculate()" should {
    forAll(input) { (grossPay, taxCalcResource, limitId, expectedAmount, expectedPercentage) =>
      s"calculate [$expectedAmount] in ${taxCalcResource.taxYear}, given grossPay[$grossPay], limitId[rate.$limitId]" in {
        val rate = EmployerRateCalculator(Money(grossPay), limitId, taxCalcResource).calculate().result
        rate.amount     shouldBe expectedAmount
        rate.percentage shouldBe expectedPercentage
      }
    }
  }
}
