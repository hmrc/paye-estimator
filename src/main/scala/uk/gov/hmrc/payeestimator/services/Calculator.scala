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

import scala.math

trait Calculator {

  def calculate(): CalculatorResponse

  protected def isAnnual(payPeriod: String): Boolean = {
    payPeriod.equals("annual")
  }

}

class TaxCalculatorConfigException(message:String) extends Exception(message)

case class ExcessPayCalculator(taxCode: String, date: LocalDate, taxBandId : Int, taxablePay: Money) extends Calculator with TaxCalculatorHelper {

  override def calculate(): ExcessPayResponse = {
    val taxBands = getTaxBands(date)
    if(taxBandId > 1){
      isBasicRateTaxCode(taxCode) match {
        case true => applyResponse(true, taxablePay)
        case false => {
          val previousBand = taxBands.taxBands.find(_.band == taxBandId - 1).getOrElse(throw new TaxCalculatorConfigException(s"Could not find tax band configured for band ${taxBandId - 1}"))
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

case class AllowanceCalculator(taxCode: String) extends Calculator with TaxCalculatorHelper{

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

case class TaxablePayCalculator(date: LocalDate, taxCode: String, grossPay: Money) extends Calculator with TaxCalculatorHelper{

  override def calculate(): TaxablePayResponse = {

    val taperingDeductionCalc = AnnualTaperingDeductionCalculator(removeScottishElement(taxCode), date, grossPay).calculate()
    val updatedTaxCode = taperingDeductionCalc.result

    val taxablePay: Money = isBasicRateTaxCode(taxCode) match {
      case true   => grossPay
      case false  => {
        isTaxableCode(taxCode) match {
          case true   => {
            val allowance = AllowanceCalculator(updatedTaxCode).calculate().result
            if(isUnTaxedIncomeTaxCode(taxCode))
              Money(grossPay+allowance.allowance)
            else Money(grossPay-allowance.allowance)
          }
          case false  => Money(0, 2, true)
        }
      }
    }

    val additionalTaxablePay = if(isUnTaxedIncomeTaxCode(taxCode)) taxablePay - grossPay else Money(0, 2, true)
    applyResponse(true, taxablePay, taperingDeductionCalc.isTapered, additionalTaxablePay)
  }

  def applyResponse(success: Boolean, taxablePay: Money, isTapered: Boolean, additionalTaxablePay: Money): TaxablePayResponse = {
    val result = if(taxablePay < Money(0)) Money(0, 2, true) else taxablePay
    TaxablePayResponse(success, result, isTapered, additionalTaxablePay)
  }
}

case class TaxBandCalculator(taxCode: String, date: LocalDate, taxablePay: Money) extends Calculator with TaxCalculatorHelper{

  override def calculate(): TaxBandResponse = {
    val taxBand = isBasicRateTaxCode(taxCode) match {
      case true => {
        taxCode match {
          case "BR" => getTaxBands(date).taxBands.find(_.band == 2).head
          case "D0" => getTaxBands(date).taxBands.find(_.band == 3).head
          case "D1" => getTaxBands(date).taxBands.find(_.band == 4).head
        }
      }
      case false => {
        val taxBands = getTaxBands(date).taxBands.collect(taxBandFilterFunc(taxablePay))
        !taxBands.isEmpty match {
          case true => taxBands.head
          case false => getTaxBands(date).taxBands.last
        }
      }
    }
    applyResponse (true, taxBand)
  }

  def applyResponse(success: Boolean, taxBand: TaxBand): TaxBandResponse = {
    TaxBandResponse(success, taxBand)
  }

  private def taxBandFilterFunc(taxablePay: Money): PartialFunction[TaxBand, TaxBand] = {
    case taxBand: TaxBand  if taxBand.period.threshold > taxablePay.value => taxBand
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

case class EmployeeRateCalculator(date: LocalDate, grossPay: Money, limitId: Int) extends Calculator with TaxCalculatorHelper {

  val nicRateLimit = getRateLimits(date)
  val rate = nicRateLimit.employeeRate.collect(rateLimit(s"$limitId")).head.value
  override def calculate(): RateCalculatorResponse = {
    if(grossPay <= nicRateLimit.threshold.collect(rateLimit("primary")).head){
      applyResponse(true, zeroRate.aggregation)
    }
    else {
      val result = limitId match {
        case 1 => {
          grossPay > nicRateLimit.threshold.collect(rateLimit("secondary")).head match {
            case true => RateResult(nicRateLimit.threshold.collect(rateLimit("secondary")).head,
              nicRateLimit.threshold.collect(rateLimit("primary")).head, rate)
            case false => grossPay <= nicRateLimit.threshold.collect(rateLimit("secondary")).head  &&
              grossPay > nicRateLimit.threshold.collect(rateLimit("primary")).head match {
              case true => RateResult(grossPay, nicRateLimit.threshold.collect(rateLimit("primary")).head, rate)
              case false => zeroRate
            }
          }
        }
        case 3 => {
          grossPay > nicRateLimit.earningLimit.collect(rateLimit("upper")).head match {
            case true => RateResult(nicRateLimit.earningLimit.collect(rateLimit("upper")).head,
              nicRateLimit.threshold.collect(rateLimit("secondary")).head, rate)
            case false => grossPay <= nicRateLimit.earningLimit.collect(rateLimit("upper")).head &&
              grossPay > nicRateLimit.threshold.collect(rateLimit("secondary")).head match {
              case true => RateResult(grossPay, nicRateLimit.threshold.collect(rateLimit("secondary")).head, rate)
              case false => zeroRate
            }
          }
        }
        case 4 => {
          grossPay > nicRateLimit.earningLimit.collect(rateLimit("upper")).head match {
            case true => RateResult(grossPay, nicRateLimit.earningLimit.collect(rateLimit("upper")).head, rate)
            case false => zeroRate
          }
        }
      }
      applyResponse(true, result.aggregation)
    }
  }

  def applyResponse(success: Boolean, aggregation: Aggregation): RateCalculatorResponse = {
    RateCalculatorResponse(success, aggregation)
  }

  private def zeroRate = RateResult(Money(0), Money(0), rate)
}

case class EmployerRateCalculator(date: LocalDate, grossPay: Money, limitId: Int) extends Calculator with TaxCalculatorHelper {
  val nicRateLimit = getRateLimits(date)
  val rate = nicRateLimit.employerRate.collect(rateLimit(s"$limitId")).head.value

  override def calculate(): RateCalculatorResponse = {
    val result = limitId match {
      case 2 => {
        grossPay > nicRateLimit.earningLimit.collect(rateLimit("upper")).head match {
          case true => RateResult(nicRateLimit.earningLimit.collect(rateLimit("upper")).head,
            nicRateLimit.threshold.collect(rateLimit("secondary")).head, rate)
          case false => grossPay <= nicRateLimit.earningLimit.collect(rateLimit("upper")).head &&
            grossPay >  nicRateLimit.threshold.collect(rateLimit("secondary")).head match {
            case true => RateResult(grossPay, nicRateLimit.threshold.collect(rateLimit("secondary")).head, rate)
            case false => zeroRate
          }
        }
      }
      case 3 => {
        grossPay > nicRateLimit.earningLimit.collect(rateLimit("upper")).head match {
          case true => RateResult(grossPay, nicRateLimit.earningLimit.collect(rateLimit("upper")).head, rate)
          case false => zeroRate
        }
      }
    }
    applyResponse(true, result.aggregation)
  }

  def applyResponse(success: Boolean, aggregation: Aggregation): RateCalculatorResponse = {
    RateCalculatorResponse(success, aggregation)
  }

  private def zeroRate = RateResult(Money(0), Money(0), rate)
}

case class AnnualTaperingDeductionCalculator(taxCode: String, date: LocalDate, grossPay: Money) extends Calculator with TaxCalculatorHelper{

  override def calculate(): TaperingResponse = {
    val annualIncomeThreshold = getTaxBands(date).annualIncomeThreshold
    isEmergencyTaxCode(taxCode) match {
      case false => applyResponse(true, taxCode, false)
      case true  => {
        (grossPay > annualIncomeThreshold) match {
          case false => applyResponse(true, taxCode, false)
          case true => {
            val taperingDeduction = Money(((grossPay.value - annualIncomeThreshold) / 2).intValue() / BigDecimal(10), 2, true)
            val taxCodeNumber = Money(BigDecimal(splitTaxCode(taxCode).toInt), 2, true)
            (taperingDeduction < taxCodeNumber) match {
              case false => applyResponse(true, "ZERO", true)
              case true => applyResponse(true, s"${(taxCodeNumber-taperingDeduction).value}L", true)
            }
          }
        }
      }
    }
  }

  def applyResponse(success: Boolean, taxCode: String, isTapered: Boolean): TaperingResponse = {
    TaperingResponse(success, taxCode, isTapered)
  }
}

case class RateResult(lhs: Money, rhs: Money, rate: BigDecimal) {
  val amount = NICRateCalculator(rate, lhs-rhs).calculate().result.value
  val aggregation = Aggregation(rate, amount)
}