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

package uk.gov.hmrc.payeestimator.domain

import java.time.LocalDate


case class TaxYearBands(taxYearBands: Seq[TaxBands])

case class TaxBands(fromDate: LocalDate, annualIncomeThreshold: BigDecimal, scottishRate: BigDecimal,taxBands: Seq[TaxBand])

case class TaxBand(band: Int, bandwidth: BigDecimal, rate: BigDecimal, periods: Seq[PeriodCalc])

case class PeriodCalc(periodType: String, threshold: BigDecimal, cumulativeMaxTax: BigDecimal, maxTax: BigDecimal)

case class NICRateLimits(rateLimits: Seq[NICRateLimit])

case class NICRateLimit(fromDate: LocalDate, earningLimit: Seq[RateLimit], threshold: Seq[RateLimit], employeeRate: Seq[RateLimit], employerRate: Seq[RateLimit])

case class RateLimit(rateLimitType: String, weekly: BigDecimal, monthly: BigDecimal, annual: BigDecimal)

object TaxCalcResources {

  val periods1 = Seq(
    PeriodCalc( "annual", 0, 0, 0),
    PeriodCalc( "monthly", 0, 0, 0),
    PeriodCalc( "weekly", 0, 0, 0))
  val taxBands1 = TaxBand(1, BigDecimal(0.00), 10, periods1)

  val periods2 = Seq(
    PeriodCalc( "annual", 32000.00, 6400.00, 6400.00),
    PeriodCalc( "monthly", 2666.6666, 533.3333, 533.3333),
    PeriodCalc( "weekly", 615.3846, 123.0769, 123.0769))
  val taxBands2 = TaxBand(2, BigDecimal(3000.00), 20, periods2)

  val periods3 = Seq(
    PeriodCalc("annual", 150000.00, 53600.00, 47200.00),
    PeriodCalc( "monthly", 12500.00, 4466.6666, 3933.3333),
    PeriodCalc( "weekly", 2884.6153, 1030.7692, 907.6923))
  val taxBands3 = TaxBand(3, BigDecimal(118000.00), 40, periods3)

  val periods4 = Seq(
    PeriodCalc( "annual", -1, -1, -1),
    PeriodCalc( "monthly", -1, -1, -1),
    PeriodCalc( "weekly", -1, -1, -1))
  val taxBands4 = TaxBand(4, BigDecimal(-1), 45, periods4)

  val taxYearBands = TaxYearBands( Seq(
    TaxBands(LocalDate.of(2016,4,5), BigDecimal(100000.00), BigDecimal(10),
      Seq(taxBands1,taxBands2,taxBands3,taxBands4))
  ))

  val rateLimit1 = RateLimit("lower", 112.00, 486.00, 5824.00)
  val rateLimit2 = RateLimit("upper", 827.00, 3583.00, 43000.00)

  val threshold1 = RateLimit("primary", 155.00, 672.00, 8060.00)
  val threshold2 = RateLimit("secondary", 156.00, 676.00, 8112.00)

  val employee1 = RateLimit("1", 12, 12, 12)
  val employee2 = RateLimit("2", 0, 0, 0)
  val employee3 = RateLimit("3", 12, 12, 12)
  val employee4 = RateLimit("4", 2, 2, 2)

  val employer1 = RateLimit("1", 0, 0, 0)
  val employer2 = RateLimit("2", 13.8, 13.8, 13.8)
  val employer3 = RateLimit("3", 13.8, 13.8, 13.8)
  val employer4 = RateLimit("4", 0, 0, 0)


  val nICRateLimit1 = NICRateLimit(LocalDate.of(2016,4,5),
    Seq(rateLimit1, rateLimit2),
    Seq(threshold1, threshold2),
    Seq(employee1, employee2, employee3, employee4),
    Seq(employer1, employer2, employer3, employer4)
  )

  val nicRateLimits = NICRateLimits(Seq(nICRateLimit1))

}


