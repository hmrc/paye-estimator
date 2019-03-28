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

case class PAYETaxResult(
  taxablePay:           Money,
  excessPay:            Money,
  finalBandTaxedAmount: Money,
  band:                 Int,
  previousBandMaxTax:   Money,
  bandRate:             BigDecimal,
  isTapered:            Boolean,
  additionalTaxablePay: Money) {
  val payeTaxAmount: Money = if (band > 1) {
    finalBandTaxedAmount + previousBandMaxTax
  } else {
    finalBandTaxedAmount
  }
}

case class NICTaxResult(employeeNICBandRate: BigDecimal, employeeNIC: Seq[Aggregation], employerNIC: Seq[Aggregation])

case class TaxCalc(
  statePensionAge:      Boolean,
  taxCode:              String,
  payPerHour:           BigDecimal,
  hours:                Int,
  averageAnnualTaxRate: BigDecimal,
  rateType:             Option[String],
  marginalTaxRate:      BigDecimal,
  maxTaxRate:           BigDecimal,
  payeBand:             BigDecimal,
  employeeNICBand:      BigDecimal,
  tapered:              Boolean,
  taxBreakdown:         Seq[TaxBreakdown])

case class TaxBreakdown(
  period:               String,
  grossPay:             BigDecimal,
  taxFreePay:           BigDecimal,
  taxablePay:           BigDecimal,
  additionalTaxablePay: BigDecimal,
  scottishElement:      Option[BigDecimal],
  maxTaxAmount:         BigDecimal,
  taxCategories:        Seq[TaxCategory],
  totalDeductions:      BigDecimal,
  takeHomePay:          BigDecimal)

case class TaxCategory(taxType: String, total: BigDecimal, aggregation: Seq[Aggregation])

case class Aggregation(percentage: BigDecimal, amount: BigDecimal)
