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

trait TaxCalculatorHelper {

  def isValidTaxCode(taxCode: String, taxCalcResource: TaxCalcResource): Boolean = {
    println(s"Tax Code to validate $taxCode")

    isStandardTaxCode(taxCode) ||
    !isTaxableCode(taxCode, taxCalcResource.isScottish) ||
    isBasicRateTaxCode(taxCode, taxCalcResource.isScottish) ||
    isEmergencyTaxCode(taxCode, taxCalcResource) ||
    isValidScottishTaxCode(taxCode) ||
    isUnTaxedIncomeTaxCode(taxCode)
  }
  //TODO Remove C from regex when Wales has its own band
  def isStandardTaxCode(taxCode: String): Boolean =
    taxCode.matches("(C)?([0-9]{1,4}[L-N,T]{1}){1}")

  def isTaxableCode(taxCode: String, isScottish: Boolean = false): Boolean =
    !taxCode.matches("([N][T]){1}") && !isBasicRateTaxCode(taxCode, isScottish)

  def isBasicRateTaxCode(taxCode: String, isScottish: Boolean = false): Boolean =
    taxCode.matches("([B][R]){1}(X)?") ||
      taxCode.matches(s"([D][0,1${if (isScottish) { ",2" } else { "" }}](X)?)")

  // 1150L, 1185L or 1250L
  def isMainTaxCode(taxCode: String, taxCalcResource: TaxCalcResource): Boolean =
    taxCode.matches(taxCalcResource.emergencyTaxCode.stripSuffix("L") + "L") //Stripping so that we can easily use the code for all years

  def isEmergencyTaxCode(taxCode: String, taxCalcResource: TaxCalcResource): Boolean =
    if (taxCalcResource.startDate.getYear == 2019) {
      (taxCode.toUpperCase.endsWith("X") || taxCode.toUpperCase.endsWith("M1") ||
      taxCode.toUpperCase.endsWith("W1")) && !taxCode.matches("(C|S)?[0-9]{1}X")
    } else {
      taxCode.matches(taxCalcResource.emergencyTaxCode)
    }

  def isAdjustedTaxCode(taxCode: String): Boolean =
    taxCode.matches("(C)?([0-9]+[.]{1}[0-9]{2}[L]{1}){1}")

  def isValidScottishTaxCode(taxCode: String): Boolean =
    taxCode.matches("[S]{1}[0-9]{1,4}( )?([L-N,T,X,W,1]){1,2}") ||
      taxCode.matches("([S][B][R]){1}(X)?") ||
      taxCode.matches("([S][D][0,1,2]){1}(X)?") ||
      taxCode.matches("([S][K][0-9]{1,4}){1}(X)?")

  def isUnTaxedIncomeTaxCode(taxCode: String): Boolean =
    taxCode matches "(C|S)?([K]{1}[0-9]{1,4}){1}"

  def rateLimit(limitType: String): PartialFunction[RateLimit, Money] = {
    case rateLimit: RateLimit if rateLimit.rateLimitType.equals(limitType) =>
      Money(rateLimit.limit)
  }

  def splitTaxCode(taxCode: String, taxCalcResource: TaxCalcResource): String =
    if (isStandardTaxCode(taxCode) || isAdjustedTaxCode(taxCode)) {
      taxCode.stripSuffix(taxCode.substring(taxCode.length - 1, taxCode.length))
    } else if (isEmergencyTaxCode(taxCode, taxCalcResource)) {
      taxCode.replace(" ", "").toUpperCase()
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
      taxCode
    }

  def removeCountryElementFromTaxCode(taxCode: String): String =
    if (isValidScottishTaxCode(taxCode)) {
      taxCode.toUpperCase.stripPrefix("S")
    } else {
      if (taxCode.toUpperCase.startsWith("C")) {
        taxCode.toUpperCase.stripPrefix("C")
      } else {
        taxCode
      }
    }
}
