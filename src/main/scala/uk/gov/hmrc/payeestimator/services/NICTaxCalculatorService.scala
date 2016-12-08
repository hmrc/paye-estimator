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

import java.time.LocalDate
import uk.gov.hmrc.payeestimator.domain.{NICRateLimit, Aggregation, NICTaxResult, Money}

trait NICTaxCalculatorService extends TaxCalculatorHelper {

  def calculateNICTax(isStatePensionAge: Boolean, grossPay: Money): NICTaxResult = {
    isStatePensionAge match {
      case false => {
        val rateLimits = getRateLimits(LocalDate.now)
        val employeeNICResult = calculateEmployeeNIC(grossPay, rateLimits)
        NICTaxResult(employeeNICResult.nicBandRate, employeeNICResult.aggregation,calculateEmployerNIC(grossPay, rateLimits))
      }
      case true => NICTaxResult(BigDecimal(0),Seq(), Seq())
    }
  }

  def calculateEmployeeNIC(grossPay: Money, nicRateLimit: NICRateLimit): EmployeeNICResult = {
    val rate1 = EmployeeRateCalculator(LocalDate.now, grossPay, 1).calculate().result
    val rate3 = EmployeeRateCalculator(LocalDate.now, grossPay, 3).calculate().result
    val rate4 = EmployeeRateCalculator(LocalDate.now, grossPay, 4).calculate().result
    val result = Seq(Aggregation(rate1.percentage, rate1.amount + rate3.amount), rate4).filter(_.amount > BigDecimal(0))
    val nicBandRate = result.size > 0 match {
      case true => result.last.percentage
      case false => BigDecimal(0)
    }
    EmployeeNICResult(result,nicBandRate)
  }

  def calculateEmployerNIC(grossPay: Money, nicRateLimit: NICRateLimit): Seq[Aggregation] = {
    val rate2 = EmployerRateCalculator(LocalDate.now, grossPay, 2).calculate().result
    val rate3 = EmployerRateCalculator(LocalDate.now, grossPay, 3).calculate().result
    Seq(Aggregation(rate2.percentage, rate2.amount + rate3.amount))
  }
}

case class EmployeeNICResult(aggregation: Seq[Aggregation], nicBandRate: BigDecimal)

object LiveNICTaxCalculatorService extends NICTaxCalculatorService {
}
