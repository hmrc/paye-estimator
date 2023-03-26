/*
 * Copyright 2023 HM Revenue & Customs
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

sealed trait PayPeriod {
  override def toString: String = this match {
    case Weekly => "weekly"
    case Monthly => "monthly"
    case Annual => "annual"
    case e => throw new RuntimeException(s"There is no toString implementation for $e")
  }
}
case object Weekly extends PayPeriod
case object Monthly extends PayPeriod
case object Annual extends PayPeriod

sealed trait TaxType{
  override def toString: String = this match {
    case IncomeTax => "incomeTax"
    case EmployerNationalInsurance => "employerNationalInsurance"
    case EmployeeNationalInsurance => "employeeNationalInsurance"
    case e => throw new RuntimeException(s"There is no toString implementation for $e")
  }
}
case object IncomeTax extends TaxType
case object EmployerNationalInsurance extends TaxType
case object EmployeeNationalInsurance extends TaxType

trait TaxCalculatorHelper {

  def isValidTaxCode(taxCode: String, taxCalcResource: TaxCalcResource): Boolean = {
    isStandardTaxCode(taxCode) ||
    !isTaxableCode(taxCode, taxCalcResource.isScottish) ||
    isBasicRateTaxCode(taxCode, taxCalcResource.isScottish) ||
    isEmergencyTaxCode(taxCode, taxCalcResource) ||
    isValidScottishTaxCode(taxCode) ||
    isUnTaxedIncomeTaxCode(taxCode)
  }

  //TODO Remove C from regex when Wales has its own band
  def isStandardTaxCode(taxCode: String): Boolean =
    taxCode.toUpperCase.matches("(C)?([0-9]{1,4}[L-N,T]{1}){1}")

  def isTaxableCode(taxCode: String, isScottish: Boolean = false): Boolean =
    !taxCode.toUpperCase.matches("([N][T]){1}") && !isBasicRateTaxCode(taxCode, isScottish)

  def isBasicRateTaxCode(taxCode: String, isScottish: Boolean = false): Boolean =
    taxCode.toUpperCase.matches("([B][R]){1}(X)?") ||
      taxCode.toUpperCase.matches(s"([D][0,1${if (isScottish) { ",2" } else { "" }}](X)?)")

  // 1150L, 1185L or 1250L
  def isMainTaxCode(taxCode: String, taxCalcResource: TaxCalcResource): Boolean =
    taxCode.toUpperCase.matches(taxCalcResource.emergencyTaxCode.stripSuffix("L") + "L") //Stripping so that we can easily use the code for all years

  def isEmergencyTaxCode(taxCode: String, taxCalcResource: TaxCalcResource): Boolean =
    if (taxCalcResource.startDate.getYear == 2019) {
      (taxCode.toUpperCase.toUpperCase.endsWith("X") || taxCode.toUpperCase.endsWith("M1") ||
      taxCode.toUpperCase.toUpperCase.endsWith("W1")) && !taxCode.toUpperCase.matches("(C|S)?[0-9]{1}X")
    } else {
      taxCode.toUpperCase.matches(taxCalcResource.emergencyTaxCode)
    }

  def isAdjustedTaxCode(taxCode: String): Boolean =
    taxCode.toUpperCase.matches("(C)?([0-9]+[.]{1}[0-9]{2}[L]{1}){1}")

  def isValidScottishTaxCode(taxCode: String): Boolean =
    taxCode.toUpperCase.matches("[S]{1}[0-9]{1,4}( )?([L-N,T,X,W,1]){1,2}") ||
      taxCode.toUpperCase.matches("([S][B][R]){1}(X)?") ||
      taxCode.toUpperCase.matches("([S][D][0,1,2]){1}(X)?") ||
      taxCode.toUpperCase.matches("([S][K][0-9]{1,4}){1}(X)?")

  def isUnTaxedIncomeTaxCode(taxCode: String): Boolean =
    taxCode.toUpperCase.matches("(C|S)?([K]{1}[0-9]{1,4}){1}")

  def rateLimit(limitType: String): PartialFunction[RateLimit, Money] = {
    case rateLimit: RateLimit if rateLimit.rateLimitType.equals(limitType) =>
      Money(rateLimit.limit)
  }

  def splitTaxCode(taxCode: String, taxCalcResource: TaxCalcResource): String =
    if (isStandardTaxCode(taxCode) || isAdjustedTaxCode(taxCode)) {
      taxCode.toUpperCase.stripSuffix(taxCode.substring(taxCode.length - 1, taxCode.length))
    } else if (isEmergencyTaxCode(taxCode, taxCalcResource)) {
      taxCode.replace(" ", "").toUpperCase
        .stripPrefix("K")
        .stripSuffix("W1")
        .stripSuffix("M1")
        .stripSuffix("X")
        .stripSuffix("M")
        .stripSuffix("N")
        .stripSuffix("L")
    } else if (isUnTaxedIncomeTaxCode(taxCode) && (taxCode.toUpperCase.contains("S") || taxCode.toUpperCase.contains("K"))) {
      taxCode.toUpperCase.stripPrefix("S").stripPrefix("K")
    } else {
      taxCode.toUpperCase
    }

  def removeCountryElementFromTaxCode(taxCode: String): String =
    if (isValidScottishTaxCode(taxCode)) {
      taxCode.toUpperCase.stripPrefix("S")
    } else {
      if (taxCode.toUpperCase.startsWith("C")) {
        taxCode.toUpperCase.stripPrefix("C")
      } else {
        taxCode.toUpperCase
      }
    }
}
