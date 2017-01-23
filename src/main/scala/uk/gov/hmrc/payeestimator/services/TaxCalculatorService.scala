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

import uk.gov.hmrc.payeestimator.domain._

import scala.math.BigDecimal
import scala.math.BigDecimal.RoundingMode
import scala.scalajs.js.annotation.JSExport
import upickle.default._

@JSExport
object OptionFactory {
  @JSExport
  def none(): None.type = None

  @JSExport
  def some(value: Int): Option[Int] = Some(value)
}

trait TaxCalculatorService extends TaxCalculatorHelper {
  val payeTaxCalculatorService: PAYETaxCalculatorService
  val nicTaxCalculatorService: NICTaxCalculatorService

  @JSExport
  def calculateTax(isStatePensionAge: String, taxYear: Int, taxCode: String, grossPayPence: Int, payPeriod: String, hoursIn: Int): String = {
    val taxCalResult = buildTaxCalc(isStatePensionAge, taxYear, taxCode, grossPayPence, payPeriod, hoursIn)
    write(taxCalResult)
  }

  def buildTaxCalc(isStatePensionAge: String, taxYear: Int, taxCode: String,
                   grossPayPence: Int, payPeriod: String, hoursIn: Int): TaxCalc = {
    val hours = if (hoursIn > 0) Some(hoursIn) else None
    val isPensionAge = convertToBoolean(isStatePensionAge)
    val updatedPayPeriod = if (hours.getOrElse(-1) > 0) "annual" else payPeriod

    val grossPay = annualiseGrossPay(grossPayPence, hours, updatedPayPeriod)
    val updatedTaxCode = removeScottishElement(taxCode)
    val payeTax = payeTaxCalculatorService.calculatePAYETax(updatedTaxCode, grossPay)
    val nicTax = nicTaxCalculatorService.calculateNICTax(isPensionAge, grossPay)
    val maxTax = MaxRateCalculator(payeTax.payeTaxAmount, LocalDate.now, grossPay).calculate().result
    val aggregation = PAYEAggregateBuilder(updatedTaxCode, LocalDate.now, payeTax.band, payeTax.payeTaxAmount).build().aggregation

    val nicTaxCategories = NICTaxCategoryBuilder(nicTax).build().taxCategories
    val taxCategories = Seq(TaxCategory(taxType = "incomeTax", payeTax.payeTaxAmount.value, aggregation)) ++ nicTaxCategories
    val totalDeductions = taxCategories.collect(TotalDeductionsFunc(maxTax.value)).foldLeft(BigDecimal(0.0))(_ + _)

    val taxFreePay = grossPay > payeTax.taxablePay match {
      case true => grossPay - (payeTax.taxablePay)
      case false => Money(0)
    }

    val calculatedTaxBreakdown = TaxBreakdown("annual", grossPay.value, taxFreePay.value,
      payeTax.taxablePay.value, payeTax.additionalTaxablePay.value, calculateScottishElement(payeTax.taxablePay, taxCode, LocalDate.now), maxTax.value, taxCategories, totalDeductions,
      (grossPay - totalDeductions).value)

    val taxBreakdown = derivePeriodTaxBreakdowns(LocalDate.now, payeTax.band, taxCode, calculatedTaxBreakdown, payeTax, nicTax, aggregation, isPensionAge)

    val averageAnnualTaxRate = calculateAverageAnnualTaxRate(taxBreakdown.find(_.period == "annual"))

    TaxCalc(isPensionAge, taxCode, getHourlyGrossPay(hours, grossPayPence), hoursIn, averageAnnualTaxRate.value, payeTax.bandRate + nicTax.employeeNICBandRate, getTaxBands(LocalDate.now()).maxRate,payeTax.bandRate, nicTax.employeeNICBandRate, payeTax.isTapered, taxBreakdown)

  }

  def annualiseGrossPay(grossPayPence: Long, hours: Option[Int], payPeriod: String) = {
    val grossPay = hours match {
      case Some(value: Int) => Money(((BigDecimal(grossPayPence)*value)/100)*BigDecimal(52), 2, true)
      case _ => {
        payPeriod match {
          case "weekly"  => Money((BigDecimal(grossPayPence)*BigDecimal(52))/100, 2, true)
          case "monthly" => Money((BigDecimal(grossPayPence)*BigDecimal(12))/100, 2, true)
          case "annual"  => Money(BigDecimal(grossPayPence)/100, 2, true)
        }
      }
    }
    grossPayPence > BigDecimal(999999999) match {
      case true => throw new Exception("Bad Request, amount entered exceeds max allowed amount of £9999999.99")
      case false => grossPay
    }
  }

  def convertToBoolean(isStatePensionAge: String): Boolean = {
    isStatePensionAge.toLowerCase() match {
      case "true" => true
      case "false" => false
      case _ => throw new Exception("Invalid value")
    }
  }

  def calculateAverageAnnualTaxRate(annualTaxBreakdown: Option[TaxBreakdown]): Money = {
    annualTaxBreakdown match {
      case Some(taxBreakdown: TaxBreakdown) => Money((taxBreakdown.totalDeductions / taxBreakdown.grossPay) * BigDecimal(100), 2, true)
      case _ => Money(0, 2, true)
    }
  }

  def calculateScottishElement(payeTaxAmount: Money, taxCode: String, date: LocalDate): BigDecimal = {
    isValidScottishTaxCode(taxCode) match {
      case true => (payeTaxAmount*getTaxBands(date).scottishRate/100).value
      case false => -1
    }
  }

  private def derivePeriodTaxBreakdowns(date: LocalDate, bandId: Int, taxCode: String, taxBreakdown: TaxBreakdown, payeTax: PAYETaxResult, nicTax: NICTaxResult, payeAggregation: Seq[Aggregation], isStatePensionAge: Boolean): Seq[TaxBreakdown] = {
    val grossPay = Money(taxBreakdown.grossPay)
    val taxablePay = payeTax.taxablePay
    val additionalTaxablePay = payeTax.additionalTaxablePay
    Seq(
      taxBreakdown,
      deriveTaxBreakdown(date, bandId, taxCode, grossPay, taxablePay, additionalTaxablePay, 12, "monthly", nicTax, payeAggregation, isStatePensionAge),
      deriveTaxBreakdown(date, bandId, taxCode, grossPay, taxablePay, additionalTaxablePay, 52, "weekly", nicTax, payeAggregation, isStatePensionAge)
    )
  }

  private def deriveTaxBreakdown(date: LocalDate, bandId: Int, taxCode:String, grossPay: Money, taxablePay: Money, additionalTaxablePay: Money, rhs: Int, payPeriod: String, nicTax: NICTaxResult, payeAggregation: Seq[Aggregation], isStatePensionAge: Boolean): TaxBreakdown = {

    val updatedGrossPay = Money(grossPay/BigDecimal(rhs), 2, true)
    val updatedTaxablePay = Money(taxablePay/BigDecimal(rhs), 2, true)
    val updatedAdditionalTaxablePay = Money(additionalTaxablePay/BigDecimal(rhs), 2, true)
    val payeTotal = Money(payeAggregation.foldLeft(BigDecimal(0.0))(_ + _.amount.setScale(2, RoundingMode.HALF_UP)/rhs), 2, true)
    val maxRate = MaxRateCalculator(payeTotal, date, updatedGrossPay).calculate().result
    val employeeNICAggregation = nicTax.employeeNIC.collect(NICAggregationFunc(rhs))

    val employerNICAggregation = nicTax.employerNIC.collect(NICAggregationFunc(rhs))

    val nicTaxCategories = NICTaxCategoryBuilder(NICTaxResult(nicTax.employeeNICBandRate,employeeNICAggregation, employerNICAggregation)).build().taxCategories
    val taxCategories = Seq(TaxCategory(taxType = "incomeTax", payeTotal.value, derivePAYEAggregation(rhs, payeAggregation)))++nicTaxCategories

    val taxFreePay = updatedGrossPay > updatedTaxablePay match {
      case true => Money(updatedGrossPay-(updatedTaxablePay), 2, true)
      case false => Money(0)
    }

    val totalDeductions = Money(taxCategories.collect(TotalDeductionsFunc(maxRate.value)).foldLeft(BigDecimal(0.0))(_ + _), 2, true).value
    TaxBreakdown(payPeriod, updatedGrossPay.value, taxFreePay.value,
      updatedTaxablePay.value, updatedAdditionalTaxablePay.value, calculateScottishElement(updatedTaxablePay, taxCode, date), maxRate.value,
      taxCategories, totalDeductions,(updatedGrossPay - totalDeductions).value)
  }

  private def derivePAYEAggregation(rhs: Int, payeAggregation: Seq[Aggregation]): Seq[Aggregation] = {
    for{
      aggregation <- payeAggregation
    } yield (Aggregation(aggregation.percentage, Money(aggregation.amount / rhs, 2, true).value))
  }

  private def NICAggregationFunc(rhs: Int) : PartialFunction[Aggregation, Aggregation] = {
    case aggregate => Aggregation(aggregate.percentage, Money(aggregate.amount / rhs, 2, true).value)
  }

  private def TotalDeductionsFunc(maxTax: BigDecimal): PartialFunction[TaxCategory, BigDecimal] = {
    case taxCategory if evalMaxTaxCategory(maxTax != -1, taxCategory) => {
      if(maxTax > 0)
        taxCategory.total + maxTax
      else
        taxCategory.total
    }
  }

  private def evalMaxTaxCategory(isMaxTax: Boolean, taxCategory: TaxCategory ) = {
    val validCategory = !taxCategory.taxType.equals("employerNationalInsurance")
    if(isMaxTax)
      validCategory && !taxCategory.taxType.equals("incomeTax")
    else
      validCategory
  }

  private def getHourlyGrossPay(hours: Option[Int], grossPay: BigDecimal): BigDecimal  = {
    hours match {
      case Some(value: Int) => grossPay/100
      case _ => -1
    }
  }
}

@JSExport
object LiveTaxCalculatorService extends TaxCalculatorService {
  override val payeTaxCalculatorService: PAYETaxCalculatorService = LivePAYETaxCalculatorService
  override val nicTaxCalculatorService: NICTaxCalculatorService = LiveNICTaxCalculatorService
}
