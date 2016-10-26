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

object TaxCalculatorStartup {

  val taxCalcData = Map("taxYearBands" -> populateTaxBands.getOrElse(None), "nicRateLimits" -> populateNIC.getOrElse(None))

  def populateTaxBands: Option[TaxYearBands] = Some(TaxCalcResources.taxYearBands)

  def populateNIC: Option[NICRateLimits] = Some(TaxCalcResources.nicRateLimits)
}

trait TaxCalculatorHelper {

  def isValidTaxCode(taxCode: String): Boolean = {
    isStandardTaxCode(taxCode) ||
      !isTaxableCode(taxCode) ||
      isBasicRateTaxCode(taxCode) ||
      isEmergencyTaxCode(taxCode) ||
      isValidScottishTaxCode(taxCode) ||
      isUnTaxedIncomeTaxCode(taxCode)
  }

  def isStandardTaxCode(taxCode: String): Boolean = {
    taxCode.matches("([0-9]{1,4}[L-N,T]{1}){1}")
  }

  def isTaxableCode(taxCode: String): Boolean = {
    !taxCode.matches("([N][T]){1}") && !isBasicRateTaxCode(taxCode)
  }

  def isBasicRateTaxCode(taxCode: String): Boolean = {
    taxCode.matches("([B][R]){1}") ||
      taxCode.matches("([D][0,1]){1}")
  }

  def isEmergencyTaxCode(taxCode: String): Boolean = {
    taxCode.matches("([1]{2}[0]{2}[L]{1}){1}")
  }

  def isAdjustedTaxCode(taxCode: String): Boolean = {
    taxCode.matches("([0-9]+[.]{1}[0-9]{2}[L]{1}){1}")
  }

  def isValidScottishTaxCode(taxCode: String): Boolean = {
    taxCode.matches("([S]{1}[0-9]{1,4}[L-N,T]{1}){1}") ||
      taxCode.matches("([S][B][R]){1}") ||
      taxCode.matches("([S][D][0,1]){1}") ||
      taxCode.matches("([S][K][0-9]{1,4}){1}")
  }

  def isUnTaxedIncomeTaxCode(taxCode: String): Boolean = {
    taxCode matches "([S]?[K]{1}[0-9]{1,4}){1}"
  }

  def loadTaxBands() : TaxYearBands = {
    TaxCalculatorStartup.taxCalcData.get("taxYearBands") match {
      case Some(taxYearBands: TaxYearBands) => taxYearBands
      case _ => throw new TaxCalculatorConfigException("Error, no tax bands configured")
    }
  }

  def loadNICRateLimits() : NICRateLimits = {
    TaxCalculatorStartup.taxCalcData.get("nicRateLimits") match {
      case Some(nicRateLimits: NICRateLimits) => nicRateLimits
      case _ => throw new TaxCalculatorConfigException("Error, no national insurance rates and limits configured")
    }
  }

  def getTaxBands(localDate: LocalDate) : TaxBands = {
    val taxBands = loadTaxBands().taxYearBands.sortWith(_.fromDate.getYear < _.fromDate.getYear())
      .filter(band => band.fromDate.isBefore(localDate) || band.fromDate.isEqual(localDate))
    taxBands.last
  }

  def getRateLimits(localDate: LocalDate) : NICRateLimit = {
    val rateLimits = loadNICRateLimits().rateLimits.sortWith(_.fromDate.getYear < _.fromDate.getYear())
      .filter(rateLimit => rateLimit.fromDate.isBefore(localDate) || rateLimit.fromDate.isEqual(localDate))
    rateLimits.last
  }

  def getPreviousBandMaxTaxAmount(payPeriod: String, band: Int): Option[BigDecimal] = {
    Option(getTaxBands(LocalDate.now()).taxBands.filter(_.band == band-1).head.periods.filter(_.periodType.equals(payPeriod)).head.cumulativeMaxTax)
  }

  def resolveRateLimitByPeriod(rateLimit:RateLimit, period:String) = {
    period match {
      case "annual" => rateLimit.annual
      case "monthly" => rateLimit.monthly
      case "weekly" => rateLimit.weekly
    }
  }

  def rateLimit(limitType: String, payPeriod: String): PartialFunction[RateLimit, Money] = {
    case rateLimit: RateLimit if rateLimit.rateLimitType.equals(limitType) => {
      Money(resolveRateLimitByPeriod(rateLimit, payPeriod))
    }
  }

  def splitTaxCode(taxCode: String): String = {
    if(isStandardTaxCode(taxCode) || isAdjustedTaxCode(taxCode))
      taxCode.stripSuffix(taxCode.substring(taxCode.length - 1, taxCode.length))
    else if(isUnTaxedIncomeTaxCode(taxCode))
      taxCode.toUpperCase.contains("SK") match {
        case true => taxCode.toUpperCase.stripPrefix("SK")
        case false => taxCode.toUpperCase.stripPrefix("K")
      }
    else taxCode
  }

  def removeScottishElement(taxCode: String): String = {
    isValidScottishTaxCode(taxCode) match {
      case true => taxCode.substring(1,taxCode.length)
      case false => taxCode
    }
  }

}