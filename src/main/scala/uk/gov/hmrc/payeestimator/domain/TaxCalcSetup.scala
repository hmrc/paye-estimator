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

case class Band(band: Int, bandwidth: BigDecimal, rate: BigDecimal, period: PeriodCalc, specialTaxCode: Option[String] = None)

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
  def findTaxBand(specialTaxCode: String) = taxBands.taxBands.find(_.specialTaxCode.contains(specialTaxCode))
}

object TaxCalcResourceBuilder {

  def resources(isScottish: Boolean): Seq[TaxCalcResource] =
    Seq(TaxYear_2017_2018(isScottish), TaxYear_2018_2019(isScottish), TaxYear_2019_2020(isScottish))

  def resourceForDate(date:LocalDate, isScottish: Boolean) : TaxCalcResource = {
    resources(isScottish).find(_.applicableForDate(date)) getOrElse {
      throw new IllegalArgumentException("Unsupported Tax Period")
    }
  }
}

case class TaxYear_2019_2020(isScottish:Boolean = false) extends TaxCalcResource {

  override val emergencyTaxCode: String = "1250L"
  override val startDate: LocalDate = LocalDate.of(2019,4,6)
  override val endDate: LocalDate = LocalDate.of(2020,4,5)

  val taxBands1 = Band(band = 1, bandwidth = BigDecimal(0.00), rate = 10, period = PeriodCalc(periodType = "annual", threshold = 0, cumulativeMaxTax = 0, maxTax = 0))
  val taxBands2 = Band(band = 2, bandwidth = BigDecimal(37500.00), rate = 20, period = PeriodCalc(periodType = "annual", threshold = 37500.00, cumulativeMaxTax = 7500.00, maxTax = 7500.00), specialTaxCode = Some("BR"))
  val taxBands3 = Band(band = 3, bandwidth = BigDecimal(112500.00), rate = 40, period = PeriodCalc(periodType = "annual", threshold = 150000.00, cumulativeMaxTax = 47500, maxTax = 40000.00), specialTaxCode = Some("D0"))
  val taxBands4 = Band(band = 4, bandwidth = BigDecimal(-1),   rate = 45, period = PeriodCalc(periodType = "annual", threshold = -1, cumulativeMaxTax = -1, maxTax = -1), specialTaxCode = Some("D1"))

  val scottishTaxBands1 = Band(band = 1, bandwidth = BigDecimal(0.00),      rate = 10, period = PeriodCalc(periodType = "annual", threshold = 0, cumulativeMaxTax = 0, maxTax = 0))
  val scottishTaxBands2 = Band(band = 2, bandwidth = BigDecimal(2049.00),   rate = 19, period = PeriodCalc(periodType = "annual", threshold = 2049.00, cumulativeMaxTax = 389.00, maxTax = 389.00))
  val scottishTaxBands3 = Band(band = 3, bandwidth = BigDecimal(10395.00),  rate = 20, period = PeriodCalc(periodType = "annual", threshold = 12445.00, cumulativeMaxTax = 2468.00, maxTax = 2079.00), specialTaxCode = Some("BR"))
  val scottishTaxBands4 = Band(band = 4, bandwidth = BigDecimal(18485.00),  rate = 21, period = PeriodCalc(periodType = "annual", threshold = 30930.00, cumulativeMaxTax = 6350.30, maxTax = 3882.30), specialTaxCode = Some("D0"))
  val scottishTaxBands5 = Band(band = 5, bandwidth = BigDecimal(119070.00), rate = 41, period = PeriodCalc(periodType = "annual", threshold = 150000.00, cumulativeMaxTax = 50044.50, maxTax = 43694.20), specialTaxCode = Some("D1"))
  val scottishTaxBands6 = Band(band = 6, bandwidth = BigDecimal(-1),        rate = 46, period = PeriodCalc(periodType = "annual", threshold = -1, cumulativeMaxTax = -1, maxTax = -1), specialTaxCode = Some("D2"))

  val bands: Seq[Band] =
    if(isScottish) Seq(scottishTaxBands1,scottishTaxBands2,scottishTaxBands3,scottishTaxBands4,scottishTaxBands5,scottishTaxBands6)
    else Seq(taxBands1,taxBands2,taxBands3,taxBands4)

  override val taxBands: TaxBands = TaxBands(annualIncomeThreshold = BigDecimal(100000.00), scottishRate = BigDecimal(0), maxRate = BigDecimal(50), taxBands = bands)

  val rateLimit1 = RateLimit("lower", 6136.00)
  val rateLimit2 = RateLimit("upper", 50000.00)

  val threshold1 = RateLimit("primary", 8632.00)
  val threshold2 = RateLimit("secondary", 8632.00)

  val employee1 = RateLimit("1", 0)
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


case class TaxYear_2018_2019(isScottish:Boolean = false) extends TaxCalcResource {

  override val emergencyTaxCode: String = "1185L"
  override val startDate: LocalDate = LocalDate.of(2018,4,6)
  override val endDate: LocalDate = LocalDate.of(2019,4,5)

  val taxBands1 = Band(1, BigDecimal(0.00), 10, PeriodCalc("annual", 0, 0, 0))
  val taxBands2 = Band(band = 2, bandwidth = BigDecimal(34500.00), rate = 20, period = PeriodCalc(periodType = "annual", threshold = 34500.00, cumulativeMaxTax = 6900.00, maxTax = 6900.00), specialTaxCode = Some("BR"))
  val taxBands3 = Band(band = 3, bandwidth = BigDecimal(115500.00), rate = 40, period = PeriodCalc(periodType = "annual", threshold = 150000.00, cumulativeMaxTax = 53100.00, maxTax = 46200.00), specialTaxCode = Some("D0"))
  val taxBands4 = Band(4, BigDecimal(-1),   45, PeriodCalc("annual", -1, -1, -1), Some("D1"))

  val scottishTaxBands1 = Band(1, BigDecimal(0.00),      10, PeriodCalc("annual", 0, 0, 0))
  val scottishTaxBands2 = Band(2, BigDecimal(2000.00),   19, PeriodCalc("annual", 2000.00, 380.00, 380.00))
  val scottishTaxBands3 = Band(3, BigDecimal(10150.00),  20, PeriodCalc("annual", 12150.00, 2410.00, 2030.00), Some("BR"))
  val scottishTaxBands4 = Band(4, BigDecimal(19430.00),  21, PeriodCalc("annual", 31580.00, 6490.30, 4080.30), Some("D0"))
  val scottishTaxBands5 = Band(5, BigDecimal(118420.00), 41, PeriodCalc("annual", 150000.00, 55042.50, 48552.20), Some("D1"))
  val scottishTaxBands6 = Band(6, BigDecimal(-1),        46, PeriodCalc("annual", -1, -1, -1), Some("D2"))

  val bands =
    if(isScottish) Seq(scottishTaxBands1,scottishTaxBands2,scottishTaxBands3,scottishTaxBands4,scottishTaxBands5,scottishTaxBands6)
    else Seq(taxBands1,taxBands2,taxBands3,taxBands4)

  override val taxBands: TaxBands = TaxBands(BigDecimal(100000.00), BigDecimal(0), BigDecimal(50), bands)

  val rateLimit1 = RateLimit("lower", 6032.00)
  val rateLimit2 = RateLimit("upper", 46350.00)

  val threshold1 = RateLimit("primary", 8424.00)
  val threshold2 = RateLimit("secondary", 8424.00)

  val employee1 = RateLimit("1", 0)
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

case class TaxYear_2017_2018(isScottish:Boolean = false) extends TaxCalcResource {

  override val emergencyTaxCode: String = "1150L"
  override val startDate: LocalDate = LocalDate.of(2017,4,6)
  override val endDate: LocalDate = LocalDate.of(2018,4,5)

  val taxBands1 = Band(1, BigDecimal(0.00), 10, PeriodCalc("annual", 0, 0, 0))
  val taxBands2 = Band(band = 2, bandwidth = BigDecimal(33500.00), rate = 20, period = PeriodCalc(periodType = "annual", threshold = 33500.00, cumulativeMaxTax = 6700.00, maxTax = 6700.00), specialTaxCode = Some("BR"))
  val taxBands3 = Band(3, BigDecimal(116500.00), 40, PeriodCalc("annual", 150000.00, 53300.00, 46600.00), Some("D0"))
  val taxBands4 = Band(4, BigDecimal(-1),   45, PeriodCalc("annual", -1, -1, -1), Some("D1"))

  val scottishTaxBands1 = Band(1, BigDecimal(0.00),      10, PeriodCalc("annual", 0, 0, 0))
  val scottishTaxBands2 = Band(2, BigDecimal(31500.00),  20, PeriodCalc("annual", 31500.00, 6300.00, 6300.00), Some("BR"))
  val scottishTaxBands3 = Band(3, BigDecimal(118500.00), 40, PeriodCalc("annual", 150000.00, 53700.00, 47400.00), Some("D0"))
  val scottishTaxBands4 = Band(4, BigDecimal(-1),        45, PeriodCalc("annual", -1, -1, -1), Some("D1"))

  val bands = if(isScottish) Seq(scottishTaxBands1,scottishTaxBands2,scottishTaxBands3,scottishTaxBands4) else Seq(taxBands1,taxBands2,taxBands3,taxBands4)

  override val taxBands: TaxBands = TaxBands(BigDecimal(100000.00), BigDecimal(10), BigDecimal(50), bands)

  val rateLimit1 = RateLimit("lower", 5876.00)
  val rateLimit2 = RateLimit("upper", 45000.00)

  val threshold1 = RateLimit("primary", 8164.00)
  val threshold2 = RateLimit("secondary", 8164.00)

  val employee1 = RateLimit("1", 0)
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