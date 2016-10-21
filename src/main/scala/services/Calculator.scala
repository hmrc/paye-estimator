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

package services

import java.time.LocalDate
import domain._


trait Calculator {

  def calculate(): CalculatorResponse = ???

  protected def isAnnual(payPeriod: String): Boolean = {
   payPeriod.equals("annual")
  }

}

class TaxCalculatorConfigException(message: String) extends Exception(message)

case class ExcessPayCalculator(taxCode: String, date: LocalDate, taxBandId : Int, payPeriod: String, taxablePay: Money) extends Calculator with TaxCalculatorHelper {

  override def calculate(): ExcessPayResponse = {
    val taxBands = getTaxBands(date)
    if(taxBandId > 1){
      isBasicRateTaxCode(taxCode) match {
        case true => applyResponse(true, taxablePay)
        case false => {
          val previousBand = taxBands.taxBands.find(_.band == taxBandId - 1).getOrElse(throw new TaxCalculatorConfigException(s"Could not find tax band configured for band ${taxBandId - 1}"))
          val periodCalc = previousBand.periods.find(_.periodType.equals(payPeriod)).getOrElse(throw new TaxCalculatorConfigException(s"Could not find period calc configured for period $payPeriod in tax band ${taxBandId - 1}"))
          applyResponse(true, Money(periodCalc.threshold.-(taxablePay.value.intValue()).abs))
        }
      }
    }
    else applyResponse(true, taxablePay)
  }

  def applyResponse(success: Boolean, excessPay: Money): ExcessPayResponse = {
    ExcessPayResponse(success, excessPay)
  }
}

case class AllowanceCalculator(taxCode: String, payPeriod: String) extends Calculator with TaxCalculatorHelper{

  override def calculate(): AllowanceResponse = {
  taxCode match{
      case "ZERO" | "K0" | "0L" => applyResponse(true,Seq("weekly" -> ZeroAllowance(), "monthly" -> ZeroAllowance(), "annual" -> ZeroAllowance()))
      case _ => {
        val taxCodeNumber = BigDecimal(splitTaxCode(taxCode).toDouble)
        val seedData = PAYEAllowanceSeedData(taxCodeNumber)
        val response = payPeriod match {
          case "weekly" => Seq("weekly" -> WeeklyAllowance(seedData))
          case "monthly" => Seq("monthly" -> MonthlyAllowance(seedData))
          case "annual" => Seq("annual" -> AnnualAllowance(taxCode, taxCodeNumber))

        }
        applyResponse(true,response)
      }
    }
  }

  def applyResponse(success: Boolean, allowances: Seq[(String, Allowance)]): AllowanceResponse = {
    AllowanceResponse(success, allowances)
  }
}

case class TaxablePayCalculator(date: LocalDate, taxCode: String, payPeriod: String, grossPay: Money) extends Calculator with TaxCalculatorHelper{

  override def calculate(): TaxablePayResponse = {

    val taperingDeductionCalc = AnnualTaperingDeductionCalculator(removeScottishElement(taxCode), date, payPeriod, grossPay).calculate()
    val updatedTaxCode = taperingDeductionCalc.result

    val taxablePay: Seq[Money] = isBasicRateTaxCode(taxCode) match {
      case true   => Seq(grossPay)
      case false  => {
        isTaxableCode(taxCode) match {
          case true   => {
            for {
              allowance <- AllowanceCalculator(updatedTaxCode, payPeriod).calculate().result.filter(_._1.equals(payPeriod))
            } yield (if(isUnTaxedIncomeTaxCode(taxCode)) Money(grossPay+allowance._2.allowance) else Money(grossPay-allowance._2.allowance))
          }
          case false   => Seq(Money(0))
        }
      }
    }
    applyResponse(true, taxablePay.head, taperingDeductionCalc.isTapered)
  }

  def applyResponse(success: Boolean, taxablePay: Money, isTapered: Boolean): TaxablePayResponse = {
    val result = if(taxablePay < Money(0)) Money(0) else taxablePay
    TaxablePayResponse(success, result, isTapered)
  }
}

case class TaxBandCalculator(taxCode: String, date: LocalDate, payPeriod: String, taxablePay: Money) extends Calculator with TaxCalculatorHelper{

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
        val taxBands = getTaxBands(date).taxBands.collect(taxBandFilterFunc(payPeriod, taxablePay))
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

  private def taxBandFilterFunc(periodType: String, taxablePay: Money): PartialFunction[TaxBand, TaxBand] = {
    case taxBand: TaxBand  if isPeriodValid(periodType, taxBand.periods, taxablePay) => taxBand
  }

  private def isPeriodValid(periodType: String, periodCalcs: Seq[PeriodCalc], taxablePay: Money) : Boolean = {
    !periodCalcs.find(_.periodType.equals(periodType)).filter((_.threshold.>(taxablePay.value))).isEmpty
  }
}

case class NICRateCalculator(payPeriod: String, rate: BigDecimal, amount: Money) extends Calculator {

  override def calculate(): NICRateCalculatorResponse = {
    applyResponse(true, Money((amount.value * (rate / 100)) - 0.001, 2, true))
  }

  def applyResponse(success: Boolean, rate: Money): NICRateCalculatorResponse = {
    NICRateCalculatorResponse(success, rate)
  }
}

case class EmployeeRateCalculator(date: LocalDate, grossPay: Money, payPeriod: String, limitId: Int) extends Calculator with TaxCalculatorHelper {

  val nicRateLimit = getRateLimits(date)
  val rate = nicRateLimit.employeeRate.collect(rateLimit(s"$limitId", payPeriod)).head.value
  override def calculate(): RateCalculatorResponse = {
    if(grossPay <= nicRateLimit.threshold.collect(rateLimit("primary", payPeriod)).head){
      applyResponse(true, zeroRate.aggregation)
    }
    else {
      val result = limitId match {
        case 1 => {
          grossPay > nicRateLimit.threshold.collect(rateLimit("secondary", payPeriod)).head match {
            case true => RateResult(nicRateLimit.threshold.collect(rateLimit("secondary", payPeriod)).head,
              nicRateLimit.threshold.collect(rateLimit("primary", payPeriod)).head, rate, payPeriod)
            case false => grossPay <= nicRateLimit.threshold.collect(rateLimit("secondary", payPeriod)).head  &&
              grossPay > nicRateLimit.threshold.collect(rateLimit("primary", payPeriod)).head match {
              case true => RateResult(grossPay, nicRateLimit.threshold.collect(rateLimit("primary", payPeriod)).head, rate, payPeriod)
              case false => zeroRate
            }
          }
        }
        case 3 => {
          grossPay > nicRateLimit.earningLimit.collect(rateLimit("upper", payPeriod)).head match {
            case true => RateResult(nicRateLimit.earningLimit.collect(rateLimit("upper", payPeriod)).head,
              nicRateLimit.threshold.collect(rateLimit("secondary", payPeriod)).head, rate, payPeriod)
            case false => grossPay <= nicRateLimit.earningLimit.collect(rateLimit("upper", payPeriod)).head &&
              grossPay > nicRateLimit.threshold.collect(rateLimit("secondary", payPeriod)).head match {
              case true => RateResult(grossPay, nicRateLimit.threshold.collect(rateLimit("secondary", payPeriod)).head, rate, payPeriod)
              case false => zeroRate
            }
          }
        }
        case 4 => {
          grossPay > nicRateLimit.earningLimit.collect(rateLimit("upper", payPeriod)).head match {
            case true => RateResult(grossPay, nicRateLimit.earningLimit.collect(rateLimit("upper", payPeriod)).head, rate, payPeriod)
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

  private def zeroRate = RateResult(Money(0), Money(0), rate, payPeriod)
}

case class EmployerRateCalculator(date: LocalDate, grossPay: Money, payPeriod: String, limitId: Int) extends Calculator with TaxCalculatorHelper {
  val nicRateLimit = getRateLimits(date)
  val rate = nicRateLimit.employerRate.collect(rateLimit(s"$limitId", payPeriod)).head.value

  override def calculate(): RateCalculatorResponse = {
    val result = limitId match {
      case 2 => {
        grossPay > nicRateLimit.earningLimit.collect(rateLimit("upper", payPeriod)).head match {
          case true => RateResult(nicRateLimit.earningLimit.collect(rateLimit("upper", payPeriod)).head,
                                  nicRateLimit.threshold.collect(rateLimit("secondary", payPeriod)).head, rate, payPeriod)
          case false => grossPay <= nicRateLimit.earningLimit.collect(rateLimit("upper", payPeriod)).head &&
                        grossPay >  nicRateLimit.threshold.collect(rateLimit("secondary", payPeriod)).head match {
            case true => RateResult(grossPay, nicRateLimit.threshold.collect(rateLimit("secondary", payPeriod)).head, rate, payPeriod)
            case false => zeroRate
          }
        }
      }
      case 3 => {
        grossPay > nicRateLimit.earningLimit.collect(rateLimit("upper", payPeriod)).head match {
          case true => RateResult(grossPay, nicRateLimit.earningLimit.collect(rateLimit("upper", payPeriod)).head, rate, payPeriod)
          case false => zeroRate
        }
      }
    }
    applyResponse(true, result.aggregation)
  }

  def applyResponse(success: Boolean, aggregation: Aggregation): RateCalculatorResponse = {
    RateCalculatorResponse(success, aggregation)
  }

  private def zeroRate = RateResult(Money(0), Money(0), rate, payPeriod)
}

case class AnnualTaperingDeductionCalculator(taxCode: String, date: LocalDate, payPeriod: String, grossPay: Money) extends Calculator with TaxCalculatorHelper{

  override def calculate(): TaperingResponse = {
    val annualIncomeThreshold = getTaxBands(date).annualIncomeThreshold
    isEmergencyTaxCode(taxCode) match {
      case false => applyResponse(true, taxCode, false)
      case true  => {
        val annualIncome = grossPay * (payPeriod match {
          case "annual" => BigDecimal(1)
          case "monthly" => BigDecimal(12)
          case "weekly" => BigDecimal(52)
          case _ => throw new Exception(s"Pay period ${payPeriod} is not valid")
        })
        (annualIncome > annualIncomeThreshold) match {
          case false => applyResponse(true, taxCode, false)
          case true => {
            val taperingDeduction = Money(((annualIncome.value - annualIncomeThreshold) / 2).intValue() / BigDecimal(10), 2, true)
            val taxCodeNumber = Money(BigDecimal(splitTaxCode(taxCode).toInt), 2, true)
            (taperingDeduction < taxCodeNumber) match {
              case false => applyResponse(true, "ZERO", false)
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


case class RateResult(lhs: Money, rhs: Money, rate: BigDecimal, payPeriod: String) {
  val amount = NICRateCalculator(payPeriod, rate, lhs-rhs).calculate().result.value
  val aggregation = Aggregation(rate, amount)
}
