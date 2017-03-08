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

case class TaxBands(annualIncomeThreshold: BigDecimal, scottishRate: BigDecimal, maxRate: BigDecimal, taxBands: Seq[Band])

case class Band(band: Int, bandwidth: BigDecimal, rate: BigDecimal, period: PeriodCalc)

case class PeriodCalc(periodType: String, threshold: BigDecimal, cumulativeMaxTax: BigDecimal, maxTax: BigDecimal)

case class NICRateLimits(earningLimit: Seq[RateLimit], threshold: Seq[RateLimit], employeeRate: Seq[RateLimit], employerRate: Seq[RateLimit])

case class RateLimit(rateLimitType: String, limit: BigDecimal)


trait TaxCalcResource {

  val emergencyTaxCode: String
  val taxBands: TaxBands
  val startDate : LocalDate
  val endDate : LocalDate
  val nicRateLimits: NICRateLimits
  val isScottish: Boolean

  def applicableForDate(date: LocalDate): Boolean = {
    (date.isEqual(startDate)  || date.isAfter(startDate)) &&
      (date.isBefore(endDate) || date.isEqual(endDate))
  }

  lazy val rateType: String = if(isScottish) "Scotland" else ""
  def taxYear = s"$rateType ${startDate.getYear} to ${endDate.getYear}"

  def getPreviousTaxBand(band:Int) = findTaxBand(band - 1)

  def findTaxBand(i: Int) = taxBands.taxBands.find(_.band == i)
}

object TaxCalcResourceBuilder {

  def resources(isScottish: Boolean): Seq[TaxCalcResource] = Seq(TaxYear_2016_2017(), TaxYear_2017_2018(isScottish))

  def resourceForDate(date:LocalDate, isScottish: Boolean) : TaxCalcResource = {
    resources(isScottish).find(_.applicableForDate(date)) getOrElse {
      throw new IllegalArgumentException("Unsupported Tax Period")
    }
  }
}

case class TaxYear_2017_2018(val isScottish:Boolean = false) extends TaxCalcResource {

  override val emergencyTaxCode: String = "1150L"
  override val startDate: LocalDate = LocalDate.of(2017,4,6)
  override val endDate: LocalDate = LocalDate.of(2018,4,5)

  val taxBands1 = Band(1, BigDecimal(0.00), 10, PeriodCalc("annual", 0, 0, 0))
  val taxBands2 = Band(2, BigDecimal(33500.00), 20, PeriodCalc("annual", 33500.00, 6700.00, 6700.00))
  val taxBands3 = Band(3, BigDecimal(116500.00), 40, PeriodCalc("annual", 150000.00, 53300.00, 46600.00))
  val taxBands4 = Band(4, BigDecimal(-1),   45, PeriodCalc("annual", -1, -1, -1))

  val scottishTaxBands1 = Band(1, BigDecimal(0.00),      10, PeriodCalc("annual", 0, 0, 0))
  val scottishTaxBands2 = Band(2, BigDecimal(31500.00),  20, PeriodCalc("annual", 31500.00, 6300.00, 6300.00))
  val scottishTaxBands3 = Band(3, BigDecimal(118500.00), 40, PeriodCalc("annual", 150000.00, 53700.00, 47400.00))
  val scottishTaxBands4 = Band(4, BigDecimal(-1),        45, PeriodCalc("annual", -1, -1, -1))

  val bands = if(isScottish) Seq(scottishTaxBands1,scottishTaxBands2,scottishTaxBands3,scottishTaxBands4) else Seq(taxBands1,taxBands2,taxBands3,taxBands4)

  override val taxBands: TaxBands = TaxBands(BigDecimal(100000.00), BigDecimal(10), BigDecimal(50), bands)

  val rateLimit1 = RateLimit("lower", 5876.00)
  val rateLimit2 = RateLimit("upper", 45000.00)

  val threshold1 = RateLimit("primary", 8164.00)
  val threshold2 = RateLimit("secondary", 8164.00)

  val employee1 = RateLimit("1", 12)
  val employee2 = RateLimit("2", 0)
  val employee3 = RateLimit("3", 12)
  val employee4 = RateLimit("4", 2)

  val employer1 = RateLimit("1", 0)
  val employer2 = RateLimit("2", 13.8)
  val employer3 = RateLimit("3", 13.8)
  val employer4 = RateLimit("4", 0)

  override val nicRateLimits = NICRateLimits(
    Seq(rateLimit1, rateLimit2),
    Seq(threshold1, threshold2),
    Seq(employee1, employee2, employee3, employee4),
    Seq(employer1, employer2, employer3, employer4)
  )
}

case class TaxYear_2016_2017(val isScottish:Boolean = false) extends TaxCalcResource {

  override val emergencyTaxCode: String = "1100L"
  override val startDate: LocalDate = LocalDate.of(2016,4,6)
  override val endDate: LocalDate = LocalDate.of(2017,4,5)

  val taxBands1 = Band(1, BigDecimal(0.00), 10, PeriodCalc( "annual", 0, 0, 0))
  val taxBands2 = Band(2, BigDecimal(3000.00), 20, PeriodCalc( "annual", 32000.00, 6400.00, 6400.00))
  val taxBands3 = Band(3, BigDecimal(118000.00), 40, PeriodCalc("annual", 150000.00, 53600.00, 47200.00))
  val taxBands4 = Band(4, BigDecimal(-1), 45, PeriodCalc( "annual", -1, -1, -1))

  override val taxBands = TaxBands(BigDecimal(100000.00), BigDecimal(10), BigDecimal(50), Seq(taxBands1,taxBands2,taxBands3,taxBands4))


  val rateLimit1 = RateLimit("lower", 5824.00)
  val rateLimit2 = RateLimit("upper", 43000.00)

  val threshold1 = RateLimit("primary", 8060.00)
  val threshold2 = RateLimit("secondary", 8112.00)

  val employee1 = RateLimit("1", 12)
  val employee2 = RateLimit("2", 0)
  val employee3 = RateLimit("3", 12)
  val employee4 = RateLimit("4", 2)

  val employer1 = RateLimit("1", 0)
  val employer2 = RateLimit("2", 13.8)
  val employer3 = RateLimit("3", 13.8)
  val employer4 = RateLimit("4", 0)

  override val nicRateLimits = NICRateLimits(
    Seq(rateLimit1, rateLimit2),
    Seq(threshold1, threshold2),
    Seq(employee1, employee2, employee3, employee4),
    Seq(employer1, employer2, employer3, employer4)
  )
}


