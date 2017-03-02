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

import uk.gov.hmrc.payeestimator.domain.{NICTaxResult, _}

trait NICTaxCalculatorService extends TaxCalculatorHelper {

  def calculateNICTax(isStatePensionAge: Boolean, grossPay: Money, taxCalcResource: TaxCalcResource): NICTaxResult = {
    if (isStatePensionAge) NICTaxResult(BigDecimal(0), Seq(), Seq())
    else {
      val rateLimits = taxCalcResource.nicRateLimits
      val employeeNICResult = calculateEmployeeNIC(grossPay, taxCalcResource)
      NICTaxResult(employeeNICResult.nicBandRate, employeeNICResult.aggregation, calculateEmployerNIC(grossPay, taxCalcResource))
    }
  }

  def calculateEmployeeNIC(grossPay: Money, taxCalcResource: TaxCalcResource): EmployeeNICResult = {
    val rate1 = EmployeeRateCalculator(grossPay, 1, taxCalcResource).calculate().result
    val rate3 = EmployeeRateCalculator(grossPay, 3, taxCalcResource).calculate().result
    val rate4 = EmployeeRateCalculator(grossPay, 4, taxCalcResource).calculate().result
    val result = Seq(Aggregation(rate1.percentage, rate1.amount + rate3.amount), rate4).filter(_.amount > BigDecimal(0))
    val nicBandRate = result.size > 0 match {
      case true => result.last.percentage
      case false => BigDecimal(0)
    }
    EmployeeNICResult(result, nicBandRate)
  }

  def calculateEmployerNIC(grossPay: Money, taxCalcResource: TaxCalcResource): Seq[Aggregation] = {
    val rate2 = EmployerRateCalculator(grossPay, 2, taxCalcResource).calculate().result
    val rate3 = EmployerRateCalculator(grossPay, 3, taxCalcResource).calculate().result
    Seq(Aggregation(rate2.percentage, rate2.amount + rate3.amount))
  }
}

case class EmployeeNICResult(aggregation: Seq[Aggregation], nicBandRate: BigDecimal)

object LiveNICTaxCalculatorService extends NICTaxCalculatorService {
}
