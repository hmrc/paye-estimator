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

package services

import java.time.LocalDate

import domain.{Money, NICRateLimit, NICRateLimits}
import org.scalatest.{Matchers, WordSpecLike}

import scala.math.BigDecimal

class NICTaxCalculatorServiceSpec extends WordSpecLike with Matchers {


  "NICTaxCalculatorService loadNICRateLimits " should {
    "should load up the static taxBands data" in new LiveNICTaxCalcServiceSuccess {
      val result: NICRateLimits = service.loadNICRateLimits()
      result.rateLimits.size shouldBe 2

// TODO...
//      result shouldBe TaxCalculatorTestData.nic_rates_limits.as[NICRateLimits]
    }
  }

  "NICTaxCalculatorService.getNICRateLimits " should {
    "return the correct nic rates and limits for a 2016" in new LiveNICTaxCalcServiceSuccess {

      val date = LocalDate.of(2016, 9, 21)
      val result: NICRateLimit = service.getRateLimits(date)
      result.fromDate shouldBe LocalDate.of(2016,4,5)
    }

    "return the correct nic rates and limits for a 2017" in new LiveNICTaxCalcServiceSuccess {

      val date = LocalDate.of(2017, 8, 22)
      val result: NICRateLimit = service.getRateLimits(date)
      result.fromDate shouldBe LocalDate.of(2017,4,5)
    }
  }

    "NICTaxCalculatorService.calculateEmployeeNIC " should {
      //TODO relook at this when Nick B has updated spreadsheet
//      "should calculate the annual rate at the monthly rate" in new LiveNICTaxCalcServiceSuccess {
//        val rates = service.getRateLimits(LocalDate.now)
//        val result = service.calculateEmployeeNIC(Money(100000.00), "annual", rates)
//        result.size shouldBe 2
//        result.map{
//          aggregation => aggregation.percentage.intValue() match {
//            case 12 => aggregation.amount shouldBe BigDecimal(4191.84)
//            case 2 => aggregation.amount shouldBe BigDecimal(1140.12)
//          }
//        }
////      }
//      "should calculate at the monthly rate" in new LiveNICTaxCalcServiceSuccess {
//        val rates = service.getRateLimits(LocalDate.now)
//        val result = service.calculateEmployeeNIC(Money(8333.33), "monthly", rates)
//        result.size shouldBe 2
//        result.map{
//          aggregation => aggregation.percentage.intValue() match {
//            case 12 => aggregation.amount shouldBe BigDecimal(349.32)
//            case 2 => aggregation.amount shouldBe BigDecimal(95.01)
//          }
//        }
//      }
//      "should calculate at the weekly rate" in new LiveNICTaxCalcServiceSuccess {
//        val rates = service.getRateLimits(LocalDate.now)
//        val result = service.calculateEmployeeNIC(Money(1923.08), "weekly", rates)
//        result.size shouldBe 2
//        result.map{
//          aggregation => aggregation.percentage.intValue() match {
//            case 12 => aggregation.amount shouldBe BigDecimal(80.64)
//            case 2 => aggregation.amount shouldBe BigDecimal(21.92)
//          }
//        }
//      }
  }

  "NICTaxCalculatorService.calculateEmployerNIC " should {
    //TODO relook at this when Nick B has updated spreadsheet
    //    "should calculate the annual rate at the monthly rate" in new LiveNICTaxCalcServiceSuccess {
    //      val rates = service.getRateLimits(LocalDate.now)
    //      val result = service.calculateEmployerNIC(Money(100000.00), "annual", rates)
    //      result.size shouldBe 1
    //      result.map{
    //        aggregation =>
    //          aggregation.percentage shouldBe 13.8
    //          aggregation.amount shouldBe BigDecimal(12680.52)
    //        }
    //      }
    //    }
    "should calculate at the monthly rate" in new LiveNICTaxCalcServiceSuccess {
      val rates = service.getRateLimits(LocalDate.now)
      val result = service.calculateEmployerNIC(Money(8333.33), "monthly", rates)
      result.size shouldBe 1
      result.map {
        aggregation =>
          aggregation.percentage shouldBe 13.8
          aggregation.amount shouldBe BigDecimal(1056.71)
      }
    }
    "should calculate at the weekly rate" in new LiveNICTaxCalcServiceSuccess {
      val rates = service.getRateLimits(LocalDate.now)
      val result = service.calculateEmployerNIC(Money(1923.08), "weekly", rates)
      result.size shouldBe 1
      result.map {
        aggregation =>
          aggregation.percentage shouldBe 13.8
          aggregation.amount shouldBe BigDecimal(243.86)
      }
    }
  }
}
