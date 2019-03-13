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
import upickle.default._

import scala.math.BigDecimal
import scala.math.BigDecimal.RoundingMode
import scala.scalajs.js.annotation.JSExport

@JSExport
object OptionFactory {
  @JSExport
  def none(): None.type = None

  @JSExport
  def some(value: Int): Option[Int] = Some(value)
}

trait TaxCalculatorService extends TaxCalculatorHelper {
  val payeTaxCalculatorService: PAYETaxCalculatorService
  val nicTaxCalculatorService:  NICTaxCalculatorService

  @JSExport
  def calculateTax(isStatePensionAge: String, date: LocalDate, taxCode: String, grossPayPence: Int, payPeriod: String, hoursIn: Int): String = {
    val taxCalcResource = TaxCalcResourceBuilder.resourceForDate(date, isValidScottishTaxCode(taxCode))
    val taxCalResult    = buildTaxCalc(
      isStatePensionAge = isStatePensionAge,
      taxCalcResource   = taxCalcResource,
      taxCode           = taxCode,
      grossPayPence     = grossPayPence,
      payPeriod         = payPeriod,
      hoursIn           = hoursIn)
    write(taxCalResult)
  }

  @JSExport
  def calculateTax(isStatePensionAge: String, date: String, taxCode: String, grossPayPence: Int, payPeriod: String, hoursIn: Int): String = {
    val taxCalcResource = TaxCalcResourceBuilder.resourceForDate(parseDate(date), isValidScottishTaxCode(taxCode))
    val taxCalResult    = buildTaxCalc(
      isStatePensionAge = isStatePensionAge,
      taxCalcResource   = taxCalcResource,
      taxCode           = taxCode,
      grossPayPence     = grossPayPence,
      payPeriod         = payPeriod,
      hoursIn           = hoursIn)
    write(taxCalResult)
  }

  def buildTaxCalc(
    isStatePensionAge: String,
    taxCalcResource:   TaxCalcResource,
    taxCode:           String,
    grossPayPence:     Int,
    payPeriod:         String,
    hoursIn:           Int): TaxCalc = {
    val hours            = if (hoursIn > 0) Some(hoursIn) else None
    val isPensionAge     = convertToBoolean(isStatePensionAge)
    val updatedPayPeriod = if (hours.getOrElse(-1) > 0) "annual" else payPeriod

    val rateType       = if (taxCalcResource.isScottish) Some("SCOTLAND") else None
    val grossPay       = annualiseGrossPay(grossPayPence, hours, updatedPayPeriod)
    val updatedTaxCode = removeScottishElement(taxCode).toUpperCase()
    val payeTax        = payeTaxCalculatorService.calculatePAYETax(updatedTaxCode, grossPay, taxCalcResource)
    val nicTax         = nicTaxCalculatorService.calculateNICTax(isPensionAge, grossPay, taxCalcResource)
    val maxTax         = MaxRateCalculator(payeTax.payeTaxAmount, grossPay, taxCalcResource).calculate().result
    val aggregation    = PAYEAggregateBuilder(updatedTaxCode, payeTax.band, payeTax.payeTaxAmount, taxCalcResource).build().aggregation

    val nicTaxCategories = NICTaxCategoryBuilder(nicTax).build().taxCategories
    val taxCategories    = Seq(TaxCategory(taxType = IncomeTax, payeTax.payeTaxAmount.value, aggregation)) ++ nicTaxCategories
    val totalDeductions  = taxCategories.collect(TotalDeductionsFunc(maxTax.value)).foldLeft(BigDecimal(0.0))(_ + _)

    val taxFreePay = if (grossPay > payeTax.taxablePay) {
    grossPay - payeTax.taxablePay
  } else {
    Money(0)
  }

    val calculatedTaxBreakdown = TaxBreakdown(
      period = "annual",
      grossPay = grossPay.value,
      taxFreePay = taxFreePay.value,
      taxablePay = payeTax.taxablePay.value,
      additionalTaxablePay = payeTax.additionalTaxablePay.value,
      scottishElement = calculateScottishElement(payeTax.taxablePay, taxCode.toUpperCase(), taxCalcResource),
      maxTaxAmount = maxTax.value,
      taxCategories = taxCategories,
      totalDeductions = totalDeductions,
      takeHomePay = (grossPay - totalDeductions).value
    )

    val taxBreakdown = derivePeriodTaxBreakdowns(
      bandId = payeTax.band,
      taxCode = taxCode.toUpperCase(),
      taxBreakdown = calculatedTaxBreakdown,
      payeTax = payeTax,
      nicTax = nicTax,
      payeAggregation = aggregation,
      isStatePensionAge = isPensionAge,
      taxCalcResource = taxCalcResource)

    val averageAnnualTaxRate = calculateAverageAnnualTaxRate(taxBreakdown.find(_.period == "annual"))

    TaxCalc(
      statePensionAge = isPensionAge,
      taxCode = taxCode.toUpperCase(),
      payPerHour = getHourlyGrossPay(hours, grossPayPence),
      hours = hoursIn,
      averageAnnualTaxRate = averageAnnualTaxRate.value,
      rateType = rateType,
      marginalTaxRate = payeTax.bandRate + nicTax.employeeNICBandRate,
      maxTaxRate = taxCalcResource.taxBands.maxRate,
      payeBand = payeTax.bandRate,
      employeeNICBand = nicTax.employeeNICBandRate,
      tapered = payeTax.isTapered,
      taxBreakdown = taxBreakdown
    )

  }

  def annualiseGrossPay(grossPayPence: Long, hours: Option[Int], payPeriod: String): Money = {
    val grossPay = hours match {
      case Some(value: Int) => Money(((BigDecimal(grossPayPence) * value) / 100) * BigDecimal(52), 2, roundingUp = true)
      case _ =>
        payPeriod match {
          case "weekly"  => Money((BigDecimal(grossPayPence) * BigDecimal(52)) / 100, 2, roundingUp = true)
          case "monthly" => Money((BigDecimal(grossPayPence) * BigDecimal(12)) / 100, 2, roundingUp = true)
          case "annual"  => Money(BigDecimal(grossPayPence) / 100, 2, roundingUp = true)
          case other => throw new RuntimeException(s"payPeriod MatchError for object: $other")
        }
    }
    if (grossPayPence > BigDecimal(999999999)) {
    throw new Exception("Bad Request, amount entered exceeds max allowed amount of £9999999.99")
  } else {
    grossPay
  }
  }

  def convertToBoolean(isStatePensionAge: String): Boolean =
    isStatePensionAge.toLowerCase() match {
      case "true"  => true
      case "false" => false
      case _       => throw new Exception("Invalid value")
    }

  def calculateAverageAnnualTaxRate(annualTaxBreakdown: Option[TaxBreakdown]): Money =
    annualTaxBreakdown match {
      case Some(taxBreakdown: TaxBreakdown) => Money((taxBreakdown.totalDeductions / taxBreakdown.grossPay) * BigDecimal(100), 2, roundingUp = true)
      case _ => Money(0, 2, roundingUp = true)
    }

  def calculateScottishElement(payeTaxAmount: Money, taxCode: String, taxCalcResource: TaxCalcResource): Option[BigDecimal] =
    if (!taxCalcResource.isScottish) {
      if (isValidScottishTaxCode(taxCode)) Some((payeTaxAmount * taxCalcResource.taxBands.scottishRate / 100).value) else None
    } else None

  @JSExport
  def parseDate(date: String): LocalDate =
    LocalDate.of(date.substring(0, 4).toInt, date.substring(5, 7).toInt, date.substring(8, 10).toInt)
//    LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"))

  private def derivePeriodTaxBreakdowns(
    bandId:            Int,
    taxCode:           String,
    taxBreakdown:      TaxBreakdown,
    payeTax:           PAYETaxResult,
    nicTax:            NICTaxResult,
    payeAggregation:   Seq[Aggregation],
    isStatePensionAge: Boolean,
    taxCalcResource:   TaxCalcResource): Seq[TaxBreakdown] = {
    val grossPay             = Money(taxBreakdown.grossPay)
    val taxablePay           = payeTax.taxablePay
    val additionalTaxablePay = payeTax.additionalTaxablePay
    Seq(
      taxBreakdown,
      deriveTaxBreakdown(
        bandId,
        taxCode,
        grossPay,
        taxablePay,
        additionalTaxablePay,
        12,
        "monthly",
        nicTax,
        payeAggregation,
        isStatePensionAge,
        taxCalcResource),
      deriveTaxBreakdown(
        bandId,
        taxCode,
        grossPay,
        taxablePay,
        additionalTaxablePay,
        52,
        "weekly",
        nicTax,
        payeAggregation,
        isStatePensionAge,
        taxCalcResource)
    )
  }

  private def deriveTaxBreakdown(
    bandId:               Int,
    taxCode:              String,
    grossPay:             Money,
    taxablePay:           Money,
    additionalTaxablePay: Money,
    rhs:                  Int,
    payPeriod:            String,
    nicTax:               NICTaxResult,
    payeAggregation:      Seq[Aggregation],
    isStatePensionAge:    Boolean,
    taxCalcResource:      TaxCalcResource): TaxBreakdown = {

    val updatedGrossPay             = Money(grossPay / BigDecimal(rhs), 2, roundingUp = true)
    val updatedTaxablePay           = Money(taxablePay / BigDecimal(rhs), 2, roundingUp = true)
    val updatedAdditionalTaxablePay = Money(additionalTaxablePay / BigDecimal(rhs), 2, roundingUp = true)
    val payeTotal                   = Money(payeAggregation.foldLeft(BigDecimal(0.0))(_ + _.amount.setScale(2, RoundingMode.HALF_UP) / rhs), 2, roundingUp = true)
    val maxRate                     = MaxRateCalculator(payeTotal, updatedGrossPay, taxCalcResource).calculate().result
    val employeeNICAggregation      = nicTax.employeeNIC.collect(NICAggregationFunc(rhs))

    val employerNICAggregation = nicTax.employerNIC.collect(NICAggregationFunc(rhs))

    val nicTaxCategories =
      NICTaxCategoryBuilder(NICTaxResult(nicTax.employeeNICBandRate, employeeNICAggregation, employerNICAggregation)).build().taxCategories
    val taxCategories = Seq(TaxCategory(taxType = IncomeTax, payeTotal.value, derivePAYEAggregation(rhs, payeAggregation))) ++ nicTaxCategories

    val taxFreePay = if (updatedGrossPay > updatedTaxablePay) {
Money(updatedGrossPay - updatedTaxablePay , 2, roundingUp = true)
} else {
Money(0)
}

    val totalDeductions = Money(taxCategories.collect(TotalDeductionsFunc(maxRate.value)).foldLeft(BigDecimal(0.0))(_ + _), 2, roundingUp = true).value
    TaxBreakdown(
      payPeriod,
      updatedGrossPay.value,
      taxFreePay.value,
      updatedTaxablePay.value,
      updatedAdditionalTaxablePay.value,
      calculateScottishElement(updatedTaxablePay, taxCode, taxCalcResource),
      maxRate.value,
      taxCategories,
      totalDeductions,
      (updatedGrossPay - totalDeductions).value
    )
  }

  private def derivePAYEAggregation(rhs: Int, payeAggregation: Seq[Aggregation]): Seq[Aggregation] =
    for {
      aggregation <- payeAggregation
    } yield Aggregation(aggregation.percentage, Money(aggregation.amount / rhs, 2, roundingUp = true).value)

  private def NICAggregationFunc(rhs: Int): PartialFunction[Aggregation, Aggregation] = {
    case aggregate => Aggregation(aggregate.percentage, Money(aggregate.amount / rhs, 2, roundingUp = true).value)
  }

  private def TotalDeductionsFunc(maxTax: BigDecimal): PartialFunction[TaxCategory, BigDecimal] = {
    case taxCategory if evalMaxTaxCategory(maxTax != -1, taxCategory) =>
      if (maxTax > 0)
        taxCategory.total + maxTax
      else
        taxCategory.total
  }

  private def evalMaxTaxCategory(isMaxTax: Boolean, taxCategory: TaxCategory) = {
    val validCategory = !taxCategory.taxType.equals(EmployerNationalInsurance)
    if (isMaxTax)
      validCategory && !taxCategory.taxType.equals(IncomeTax)
    else
      validCategory
  }

  private def getHourlyGrossPay(hours: Option[Int], grossPay: BigDecimal): BigDecimal =
    hours match {
      case Some(_: Int) => grossPay / 100
      case _ => -1
    }

}

@JSExport
object LiveTaxCalculatorService extends TaxCalculatorService {
  override val payeTaxCalculatorService: PAYETaxCalculatorService = LivePAYETaxCalculatorService
  override val nicTaxCalculatorService:  NICTaxCalculatorService  = LiveNICTaxCalculatorService
}
