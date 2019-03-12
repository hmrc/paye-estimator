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
import uk.gov.hmrc.payeestimator.domain._

class ExcessPayCalculatorSpec extends WordSpecLike with Matchers with TaxYearChanges {

  val TaxYear_2017_2018         = new TaxYear_2017_2018(isScottish = false)
  val TaxYear_2018_2019         = new TaxYear_2018_2019(isScottish = false)
  val TaxYear_2019_2020         = new TaxYear_2019_2020(isScottish = false)
  val scottishTaxYear_2017_2018 = new TaxYear_2017_2018(isScottish = true)
  val scottishTaxYear_2018_2019 = new TaxYear_2018_2019(isScottish = true)
  val scottishTaxYear_2019_2020 = new TaxYear_2019_2020(isScottish = true)

  val input = Table(
    ("taxCode", "taxCalcResource", "bandId", "taxablePay", "expectedResult"),
    //2017-2018
    ("1100T", TaxYear_2017_2018, 1, Money(60000.00), Money(60000.00)),
    ("BR1", TaxYear_2017_2018, 1, Money(60000.00), Money(60000.00)),
    ("D0", TaxYear_2017_2018, 1, Money(60000.00), Money(60000.00)),
    ("D1", TaxYear_2017_2018, 1, Money(60000.00), Money(60000.00)),
    ("1100T", TaxYear_2017_2018, 2, Money(60000.00), Money(60000.00)),
    ("1100T", TaxYear_2017_2018, 3, Money(60000.00), Money(26500.00)),
    ("1100T", TaxYear_2017_2018, 4, Money(200000.00), Money(50000.00)),
    ("1100T", scottishTaxYear_2017_2018, 1, Money(60000.00), Money(60000.00)),
    ("BR1", scottishTaxYear_2017_2018, 1, Money(60000.00), Money(60000.00)),
    ("D0", scottishTaxYear_2017_2018, 1, Money(60000.00), Money(60000.00)),
    ("D1", scottishTaxYear_2017_2018, 1, Money(60000.00), Money(60000.00)),
    ("1100T", scottishTaxYear_2017_2018, 2, Money(60000.00), Money(60000.00)),
    ("1100T", scottishTaxYear_2017_2018, 3, Money(60000.00), Money(28500.00)),
    ("1100T", scottishTaxYear_2017_2018, 4, Money(200000.00), Money(50000.00)),
    //2018-2019
    ("1100T", TaxYear_2018_2019, 1, Money(60000.00), Money(60000.00)),
    ("BR1", TaxYear_2018_2019, 1, Money(60000.00), Money(60000.00)),
    ("D0", TaxYear_2018_2019, 1, Money(60000.00), Money(60000.00)),
    ("D1", TaxYear_2018_2019, 1, Money(60000.00), Money(60000.00)),
    ("1100T", TaxYear_2018_2019, 2, Money(60000.00), Money(60000.00)),
    ("1100T", TaxYear_2018_2019, 3, Money(60000.00), Money(25500.00)),
    ("1100T", TaxYear_2018_2019, 4, Money(200000.00), Money(50000.00)),
    ("1100T", scottishTaxYear_2018_2019, 1, Money(60000.00), Money(60000.00)),
    ("BR1", scottishTaxYear_2018_2019, 1, Money(60000.00), Money(60000.00)),
    ("D0", scottishTaxYear_2018_2019, 1, Money(60000.00), Money(60000.00)),
    ("D1", scottishTaxYear_2018_2019, 1, Money(60000.00), Money(60000.00)),
    ("1100T", scottishTaxYear_2018_2019, 2, Money(60000.00), Money(60000.00)),
    ("1100T", scottishTaxYear_2018_2019, 3, Money(60000.00), Money(58000.00)),
    ("1100T", scottishTaxYear_2018_2019, 4, Money(60000.00), Money(47850.00)),
    ("1100T", scottishTaxYear_2018_2019, 5, Money(60000.00), Money(28420.00)),
    ("1100T", scottishTaxYear_2018_2019, 6, Money(160000.00), Money(10000.00)),
    //2019-2020
    ("1100T", TaxYear_2019_2020, 1, Money(60000.00), Money(60000.00)),
    ("BR1", TaxYear_2019_2020, 1, Money(60000.00), Money(60000.00)),
    ("D0", TaxYear_2019_2020, 1, Money(60000.00), Money(60000.00)),
    ("D1", TaxYear_2019_2020, 1, Money(60000.00), Money(60000.00)),
    ("1100T", TaxYear_2019_2020, 2, Money(60000.00), Money(60000.00)),
    ("1100T", TaxYear_2019_2020, 3, Money(60000.00), Money(22500.00)),
    ("1100T", TaxYear_2019_2020, 4, Money(200000.00), Money(50000.00)),
    ("1100T", scottishTaxYear_2019_2020, 1, Money(60000.00), Money(60000.00)),
    ("BR1", scottishTaxYear_2019_2020, 1, Money(60000.00), Money(60000.00)),
    ("D0", scottishTaxYear_2019_2020, 1, Money(60000.00), Money(60000.00)),
    ("D1", scottishTaxYear_2019_2020, 1, Money(60000.00), Money(60000.00)),
    ("1100T", scottishTaxYear_2019_2020, 2, Money(60000.00), Money(60000.00)),
    ("1100T", scottishTaxYear_2019_2020, 3, Money(60000.00), Money(57951.00)),
    ("1100T", scottishTaxYear_2019_2020, 4, Money(60000.00), Money(47555.00)),
    ("1100T", scottishTaxYear_2019_2020, 5, Money(60000.00), Money(29070.00)),
    ("1100T", scottishTaxYear_2019_2020, 6, Money(160000.00), Money(10000.00))
  )

  "ExcessPayCalculator calculate()" should {

    forAll(input) { (taxCode, taxCalcResource, bandId, taxablePay, expectedResult) =>
      s"${taxCalcResource.taxYear} for $taxCode calculate the excess pay to be ${expectedResult.value} " +
        s"for a grossPay amount of ${taxablePay.value} when the applicable tax band is $bandId" in {

        val result = ExcessPayCalculator(taxCode, bandId, taxablePay, taxCalcResource).calculate().result
        result.value shouldBe expectedResult.value
      }
    }
  }
}
