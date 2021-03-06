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

class EmployeeRateCalculatorSpec extends WordSpecLike with Matchers with TaxYearChanges {

  val TaxYear_2017_2018          = new TaxYear_2017_2018(isScottish = false)
  val TaxYear_2018_2019          = new TaxYear_2018_2019(isScottish = false)
  val TaxYear_2019_2020          = new TaxYear_2019_2020(isScottish = false)
  val Scottish_TaxYear_2017_2018 = new TaxYear_2017_2018(isScottish = true)
  val Scottish_TaxYear_2018_2019 = new TaxYear_2018_2019(isScottish = true)
  val Scottish_TaxYear_2019_2020 = new TaxYear_2019_2020(isScottish = true)

  val input = Table(
    ("grossPay", "taxCalcResource", "limitId", "expectedAmount", "expectedPercentage"),
    //2017-2018
    (BigDecimal(100000.00), TaxYear_2017_2018, 1, 0.00, 0),
    (BigDecimal(100000.00), TaxYear_2017_2018, 3, 4420.32, 12),
    (BigDecimal(100000.00), TaxYear_2017_2018, 4, 1100.00, 2),
    (BigDecimal(100000.00), Scottish_TaxYear_2017_2018, 1, 0.00, 0),
    (BigDecimal(100000.00), Scottish_TaxYear_2017_2018, 3, 4420.32, 12),
    (BigDecimal(100000.00), Scottish_TaxYear_2017_2018, 4, 1100.00, 2),
    //2018-2019
    (BigDecimal(43000.00), TaxYear_2018_2019, 3, 4149.12, 12),
    (BigDecimal(43000.00), TaxYear_2018_2019, 4, 0.00, 2),
    (BigDecimal(75500.00), TaxYear_2018_2019, 3, 4551.12, 12),
    (BigDecimal(75500.00), TaxYear_2018_2019, 4, 583.00, 2),
    (BigDecimal(119000.00), TaxYear_2018_2019, 3, 4551.12, 12),
    (BigDecimal(119000.00), TaxYear_2018_2019, 4, 1453.00, 2),
    (BigDecimal(160000.00), TaxYear_2018_2019, 3, 4551.12, 12),
    (BigDecimal(160000.00), TaxYear_2018_2019, 4, 2273.00, 2),
    (BigDecimal(45000.00), TaxYear_2018_2019, 3, 4389.12, 12),
    (BigDecimal(45000.00), TaxYear_2018_2019, 4, 0.00, 2),
    (BigDecimal(50000.00), TaxYear_2018_2019, 3, 4551.12, 12),
    (BigDecimal(50000.00), TaxYear_2018_2019, 4, 73.00, 2),
    (BigDecimal(60000.00), TaxYear_2018_2019, 3, 4551.12, 12),
    (BigDecimal(60000.00), TaxYear_2018_2019, 4, 273.00, 2),
    (BigDecimal(10000.00), TaxYear_2018_2019, 3, 189.12, 12),
    (BigDecimal(10000.00), TaxYear_2018_2019, 4, 0.00, 2),
    (BigDecimal(12000.00), TaxYear_2018_2019, 3, 429.12, 12),
    (BigDecimal(12000.00), TaxYear_2018_2019, 4, 0.00, 2),
    (BigDecimal(43000.00), Scottish_TaxYear_2018_2019, 3, 4149.12, 12),
    (BigDecimal(43000.00), Scottish_TaxYear_2018_2019, 4, 0.00, 2),
    (BigDecimal(75500.00), Scottish_TaxYear_2018_2019, 3, 4551.12, 12),
    (BigDecimal(75500.00), Scottish_TaxYear_2018_2019, 4, 583.00, 2),
    (BigDecimal(119000.00), Scottish_TaxYear_2018_2019, 3, 4551.12, 12),
    (BigDecimal(119000.00), Scottish_TaxYear_2018_2019, 4, 1453.00, 2),
    (BigDecimal(160000.00), Scottish_TaxYear_2018_2019, 3, 4551.12, 12),
    (BigDecimal(160000.00), Scottish_TaxYear_2018_2019, 4, 2273.00, 2),
    (BigDecimal(45000.00), Scottish_TaxYear_2018_2019, 3, 4389.12, 12),
    (BigDecimal(45000.00), Scottish_TaxYear_2018_2019, 4, 0.00, 2),
    (BigDecimal(50000.00), Scottish_TaxYear_2018_2019, 3, 4551.12, 12),
    (BigDecimal(50000.00), Scottish_TaxYear_2018_2019, 4, 73.00, 2),
    (BigDecimal(60000.00), Scottish_TaxYear_2018_2019, 3, 4551.12, 12),
    (BigDecimal(60000.00), Scottish_TaxYear_2018_2019, 4, 273.00, 2),
    (BigDecimal(10000.00), Scottish_TaxYear_2018_2019, 3, 189.12, 12),
    (BigDecimal(10000.00), Scottish_TaxYear_2018_2019, 4, 0.00, 2),
    (BigDecimal(12000.00), Scottish_TaxYear_2018_2019, 3, 429.12, 12),
    (BigDecimal(12000.00), Scottish_TaxYear_2018_2019, 4, 0.00, 2),
    //2019-2020
    (BigDecimal(43000.00), TaxYear_2019_2020, 3, 4124.16, 12),
    (BigDecimal(43000.00), TaxYear_2019_2020, 4, 0.00, 2),
    (BigDecimal(75500.00), TaxYear_2019_2020, 3, 4964.16, 12),
    (BigDecimal(75500.00), TaxYear_2019_2020, 4, 510.00, 2),
    (BigDecimal(119000.00), TaxYear_2019_2020, 3, 4964.16, 12),
    (BigDecimal(119000.00), TaxYear_2019_2020, 4, 1380.00, 2),
    (BigDecimal(160000.00), TaxYear_2019_2020, 3, 4964.16, 12),
    (BigDecimal(160000.00), TaxYear_2019_2020, 4, 2200.00, 2),
    (BigDecimal(45000.00), TaxYear_2019_2020, 3, 4364.16, 12),
    (BigDecimal(45000.00), TaxYear_2019_2020, 4, 0.00, 2),
    (BigDecimal(50000.00), TaxYear_2019_2020, 3, 4964.16, 12),
    (BigDecimal(50000.00), TaxYear_2019_2020, 4, 0.00, 2),
    (BigDecimal(60000.00), TaxYear_2019_2020, 3, 4964.16, 12),
    (BigDecimal(60000.00), TaxYear_2019_2020, 4, 200.00, 2),
    (BigDecimal(10000.00), TaxYear_2019_2020, 3, 164.16, 12),
    (BigDecimal(10000.00), TaxYear_2019_2020, 4, 0.00, 2),
    (BigDecimal(12000.00), TaxYear_2019_2020, 3, 404.16, 12),
    (BigDecimal(12000.00), TaxYear_2019_2020, 4, 0.00, 2),
    (BigDecimal(43000.00), Scottish_TaxYear_2019_2020, 3, 4124.16, 12),
    (BigDecimal(43000.00), Scottish_TaxYear_2019_2020, 4, 0.00, 2),
    (BigDecimal(75500.00), Scottish_TaxYear_2019_2020, 3, 4964.16, 12),
    (BigDecimal(75500.00), Scottish_TaxYear_2019_2020, 4, 510.00, 2),
    (BigDecimal(119000.00), Scottish_TaxYear_2019_2020, 3, 4964.16, 12),
    (BigDecimal(119000.00), Scottish_TaxYear_2019_2020, 4, 1380.00, 2),
    (BigDecimal(160000.00), Scottish_TaxYear_2019_2020, 3, 4964.16, 12),
    (BigDecimal(160000.00), Scottish_TaxYear_2019_2020, 4, 2200.00, 2),
    (BigDecimal(45000.00), Scottish_TaxYear_2019_2020, 3, 4364.16, 12),
    (BigDecimal(45000.00), Scottish_TaxYear_2019_2020, 4, 0.00, 2),
    (BigDecimal(50000.00), Scottish_TaxYear_2019_2020, 3, 4964.16, 12),
    (BigDecimal(50000.00), Scottish_TaxYear_2019_2020, 4, 0.00, 2),
    (BigDecimal(60000.00), Scottish_TaxYear_2019_2020, 3, 4964.16, 12),
    (BigDecimal(60000.00), Scottish_TaxYear_2019_2020, 4, 200.00, 2),
    (BigDecimal(10000.00), Scottish_TaxYear_2019_2020, 3, 164.16, 12),
    (BigDecimal(10000.00), Scottish_TaxYear_2019_2020, 4, 0.00, 2),
    (BigDecimal(12000.00), Scottish_TaxYear_2019_2020, 3, 404.16, 12),
    (BigDecimal(12000.00), Scottish_TaxYear_2019_2020, 4, 0.00, 2)
  )

  s"EmployeeRateCalculator.calculate()" should {
    forAll(input) { (grossPay, taxCalcResource, limitId, expectedAmount, expectedPercentage) =>
      s"calculate [$expectedAmount] in ${taxCalcResource.taxYear}, given grossPay[$grossPay], limitId[rate.$limitId]" in {
        val rate = EmployeeRateCalculator(Money(grossPay), limitId, taxCalcResource).calculate().result
        rate.amount     shouldBe expectedAmount
        rate.percentage shouldBe expectedPercentage
      }
    }
  }
}
