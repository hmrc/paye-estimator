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

  def isValidTaxCode(taxCode: String, taxCalcResource: TaxCalcResource): Boolean =
    isStandardTaxCode(taxCode) ||
      !isTaxableCode(taxCode, taxCalcResource.isScottish) ||
      isBasicRateTaxCode(taxCode, taxCalcResource.isScottish) ||
      isEmergencyTaxCode(taxCode, taxCalcResource) ||
      isValidScottishTaxCode(taxCode) ||
      isUnTaxedIncomeTaxCode(taxCode)

  //TODO Remove C from regex when Wales has its own band
  def isStandardTaxCode(taxCode: String): Boolean =
    taxCode.matches("(C)?([0-9]{1,4}[L-N,T]{1}){1}")

  def isTaxableCode(taxCode: String, isScottish: Boolean = false): Boolean =
    !taxCode.matches("([N][T]){1}") && !isBasicRateTaxCode(taxCode, isScottish)

  def isBasicRateTaxCode(taxCode: String, isScottish: Boolean = false): Boolean =
    taxCode.matches("(C)?([B][R]){1}") ||
      taxCode.matches(s"(C)?([D][0,1${if (isScottish) { ",2" } else { "" }}])")

  // 1150L, 1185L or 1250L
  def isMainTaxCode(taxCode: String, taxCalcResource: TaxCalcResource): Boolean =
    taxCode.matches(taxCalcResource.emergencyTaxCode.stripSuffix("L") + "L") //Stripping so that we can easily use the code for all years

  def isEmergencyTaxCode(taxCode: String, taxCalcResource: TaxCalcResource): Boolean =
    taxCode.matches(taxCalcResource.emergencyRegex)

  def isAdjustedTaxCode(taxCode: String): Boolean =
    taxCode.matches("(C)?([0-9]+[.]{1}[0-9]{2}[L]{1}){1}")

  def isValidScottishTaxCode(taxCode: String): Boolean =
    taxCode.matches("([S]{1}[0-9]{1,4}[L-N,T]{1}){1}") ||
      taxCode.matches("([S][B][R]){1}") ||
      taxCode.matches("([S][D][0,1,2]){1}") ||
      taxCode.matches("([S][K][0-9]{1,4}){1}")

  def isUnTaxedIncomeTaxCode(taxCode: String): Boolean =
    taxCode matches "(C|S)?([K]{1}[0-9]{1,4}){1}"

  def rateLimit(limitType: String): PartialFunction[RateLimit, Money] = {
    case rateLimit: RateLimit if rateLimit.rateLimitType.equals(limitType) =>
      Money(rateLimit.limit)
  }

  def splitTaxCode(taxCode: String): String =
    if (isStandardTaxCode(taxCode) || isAdjustedTaxCode(taxCode))
      taxCode.stripSuffix(taxCode.substring(taxCode.length - 1, taxCode.length))
    else if (isUnTaxedIncomeTaxCode(taxCode) && (taxCode.toUpperCase.contains("S") || taxCode.toUpperCase.contains("K"))) {
      taxCode.toUpperCase.stripPrefix("S").stripPrefix("K")
    } else
      taxCode

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
