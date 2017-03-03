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

import uk.gov.hmrc.payeestimator.domain._

import scala.math.BigDecimal.RoundingMode

trait Calculator {

  def calculate(): CalculatorResponse

  protected def isAnnual(payPeriod: String): Boolean = {
    payPeriod.equals("annual")
  }

}

class TaxCalculatorConfigException(message: String) extends Exception(message)

case class ExcessPayCalculator(taxCode: String, taxBandId: Int, taxablePay: Money, taxCalcResource: TaxCalcResource) extends Calculator with TaxCalculatorHelper {

  override def calculate(): ExcessPayResponse = {

    if (taxBandId > 1) {
      isBasicRateTaxCode(taxCode) match {
        case true => applyResponse(true, taxablePay)
        case false => {
          val previousBand = taxCalcResource.getPreviousTaxBand(taxBandId).getOrElse(throw new TaxCalculatorConfigException(s"Could not find tax band configured for band ${taxBandId - 1}"))
          applyResponse(true, Money(previousBand.period.threshold.-(taxablePay.value.intValue()).abs))
        }
      }
    }
    else applyResponse(true, taxablePay)
  }

  def applyResponse(success: Boolean, excessPay: Money): ExcessPayResponse = {
    ExcessPayResponse(success, excessPay)
  }
}

case class AllowanceCalculator(taxCode: String) extends Calculator with TaxCalculatorHelper {

  override def calculate(): AllowanceResponse = {
    taxCode match {
      case "ZERO" | "K0" | "0L" => applyResponse(true, ZeroAllowance())
      case _ => applyResponse(true, AnnualAllowance(taxCode, BigDecimal(splitTaxCode(taxCode).toDouble)))
    }
  }

  def applyResponse(success: Boolean, allowance: Allowance): AllowanceResponse = {
    AllowanceResponse(success, allowance)
  }
}

case class TaxablePayCalculator(taxCode: String, grossPay: Money, taxCalcResource: TaxCalcResource) extends Calculator with TaxCalculatorHelper {

  override def calculate(): TaxablePayResponse = {

    val taperingDeductionCalc = AnnualTaperingDeductionCalculator(removeScottishElement(taxCode), grossPay, taxCalcResource).calculate()
    val updatedTaxCode = taperingDeductionCalc.result

    val taxablePay: Money = isBasicRateTaxCode(taxCode) match {
      case true => grossPay
      case false => {
        isTaxableCode(taxCode) match {
          case true => {
            val allowance = AllowanceCalculator(updatedTaxCode).calculate().result
            if (isUnTaxedIncomeTaxCode(taxCode))
              Money(grossPay + allowance.allowance)
            else Money(grossPay - allowance.allowance)
          }
          case false => Money(0, 2, true)
        }
      }
    }

    val additionalTaxablePay = if (isUnTaxedIncomeTaxCode(taxCode)) taxablePay - grossPay else Money(0, 2, true)
    applyResponse(true, taxablePay, taperingDeductionCalc.isTapered, additionalTaxablePay)
  }

  def applyResponse(success: Boolean, taxablePay: Money, isTapered: Boolean, additionalTaxablePay: Money): TaxablePayResponse = {
    val result = if (taxablePay < Money(0)) Money(0, 2, true) else taxablePay
    TaxablePayResponse(success, result, isTapered, additionalTaxablePay)
  }
}

case class TaxBandCalculator(taxCode: String, taxablePay: Money, taxCalcResource: TaxCalcResource) extends Calculator with TaxCalculatorHelper {

  override def calculate(): TaxBandResponse = {
    val taxBand = if (isBasicRateTaxCode(taxCode)) {
      taxCode match {
        case "BR" => taxCalcResource.findTaxBand(2).head
        case "D0" => taxCalcResource.findTaxBand(3).head
        case "D1" => taxCalcResource.findTaxBand(4).head
      }
    }
    else {
      val taxBands = taxCalcResource.taxBands.taxBands.collect(taxBandFilterFunc(taxablePay))
      if (taxBands.nonEmpty) taxBands.head else taxCalcResource.taxBands.taxBands.last
    }


    applyResponse(true, taxBand)
  }


  def applyResponse(success: Boolean, taxBand: Band): TaxBandResponse = {
    TaxBandResponse(success, taxBand)
  }

  private def taxBandFilterFunc(taxablePay: Money): PartialFunction[Band, Band] = {
    case taxBand: Band if taxBand.period.threshold > taxablePay.value => taxBand
  }
}

case class NICRateCalculator(rate: BigDecimal, amount: Money) extends Calculator {

  override def calculate(): NICRateCalculatorResponse = {
    applyResponse(true, Money((amount.value * (rate / 100)) - 0.001, 2, true))
  }

  def applyResponse(success: Boolean, rate: Money): NICRateCalculatorResponse = {
    NICRateCalculatorResponse(success, rate)
  }
}

trait EmployeeEmployerCalculations {

  helper:TaxCalculatorHelper =>

  def taxCalcResource : TaxCalcResource
  def rate: BigDecimal
  def grossPay : Money
  val nicRateLimit: NICRateLimits = taxCalcResource.nicRateLimits
  def findThresholdLimit(limit: String): Money = collectRateLimit(limit, nicRateLimit.threshold)
  def collectRateLimit(limit: String, collection: Seq[RateLimit]) = collection.collect(rateLimit(limit)).head
  lazy val upperEarningLimit: Money = collectRateLimit("upper", nicRateLimit.earningLimit)
  lazy val primaryThresholdLimit = findThresholdLimit("primary")
  lazy val secondaryThresholdLimit = findThresholdLimit("secondary")
  def zeroRate = RateResult(Money(0), Money(0), rate)

  def calculateRate(leftLimit: Money, rightLimit: Money) = {
    if(grossPay > leftLimit) RateResult(leftLimit, rightLimit, rate)
    else if(grossPay <= leftLimit && grossPay > rightLimit) RateResult(grossPay, rightLimit, rate)
    else zeroRate
  }
}

case class EmployeeRateCalculator(grossPay: Money, limitId: Int, taxCalcResource: TaxCalcResource)
  extends Calculator with TaxCalculatorHelper with EmployeeEmployerCalculations {

  override def rate: BigDecimal = collectRateLimit(s"$limitId", nicRateLimit.employeeRate).value

  override def calculate(): RateCalculatorResponse = {
    if (grossPay <= primaryThresholdLimit)
      RateCalculatorResponse(true, zeroRate.aggregation)
    else
      RateCalculatorResponse(true, calculateAggregation)
  }

  private def calculateAggregation: Aggregation = {
    (limitId match {
      case 1 => calculateRate(secondaryThresholdLimit, primaryThresholdLimit)
      case 3 => calculateRate(upperEarningLimit, secondaryThresholdLimit)
      case 4 =>
        if (grossPay > upperEarningLimit) RateResult(grossPay, upperEarningLimit, rate) else zeroRate
    }).aggregation
  }
}

case class EmployerRateCalculator(grossPay: Money, limitId: Int, taxCalcResource: TaxCalcResource)
  extends Calculator with TaxCalculatorHelper with EmployeeEmployerCalculations{

  override def rate = collectRateLimit(s"$limitId", nicRateLimit.employerRate).value
  override def calculate(): RateCalculatorResponse = RateCalculatorResponse(true, calculaeAggregation)

  private def calculaeAggregation = {
    (limitId match {
      case 2 => calculateRate(upperEarningLimit, secondaryThresholdLimit)
      case 3 if grossPay > upperEarningLimit => RateResult(grossPay, upperEarningLimit, rate)
      case 3 => zeroRate
    }).aggregation
  }
}

case class AnnualTaperingDeductionCalculator(taxCode: String, grossPay: Money, taxCalcResource: TaxCalcResource) extends Calculator with TaxCalculatorHelper {

  override def calculate(): TaperingResponse = {

    val annualIncomeThreshold = taxCalcResource.taxBands.annualIncomeThreshold

    isEmergencyTaxCode(taxCode, taxCalcResource) match {
      case false => TaperingResponse(true, taxCode, false)
      case true => {
        (grossPay > annualIncomeThreshold) match {
          case false => TaperingResponse(true, taxCode, false)
          case true => {
            val taperingDeduction = Money(((grossPay.value - annualIncomeThreshold) / 2).intValue() / BigDecimal(10), 2, true)
            val taxCodeNumber = Money(BigDecimal(splitTaxCode(taxCode).toInt), 2, true)
            (taperingDeduction < taxCodeNumber) match {
              case false => TaperingResponse(true, "ZERO", true)
              case true => TaperingResponse(true, s"${(taxCodeNumber - taperingDeduction).value}L", true)
            }
          }
        }
      }
    }
  }
}

case class MaxRateCalculator(payeAmount: Money, grossPay: Money, taxCalcResource: TaxCalcResource) extends Calculator with TaxCalculatorHelper {

  override def calculate(): MaxRateCalculatorResponse = {
    val maxRate = Money((grossPay.value * (taxCalcResource.taxBands.maxRate / BigDecimal(100))).setScale(2, RoundingMode.DOWN), 2, false)

    MaxRateCalculatorResponse( true,
      if(payeAmount > maxRate) maxRate else Money(BigDecimal(-1))
    )
  }
}


case class RateResult(lhs: Money, rhs: Money, rate: BigDecimal) {
  val amount = NICRateCalculator(rate, lhs - rhs).calculate().result.value
  val aggregation = Aggregation(rate, amount)
}