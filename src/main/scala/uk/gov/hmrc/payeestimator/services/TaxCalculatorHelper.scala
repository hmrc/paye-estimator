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

  def isValidTaxCode(taxCode: String, taxCalcResource:TaxCalcResource): Boolean = {
    isStandardTaxCode(taxCode) ||
      !isTaxableCode(taxCode) ||
      isBasicRateTaxCode(taxCode) ||
      isEmergencyTaxCode(taxCode, taxCalcResource) ||
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

  def isEmergencyTaxCode(taxCode: String, taxCalcResource: TaxCalcResource): Boolean = {
    taxCode.equalsIgnoreCase(taxCalcResource.emergencyTaxCode)
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

  def rateLimit(limitType: String): PartialFunction[RateLimit, Money] = {
    case rateLimit: RateLimit if rateLimit.rateLimitType.equals(limitType) => {
      Money(rateLimit.limit)
    }
  }

  def splitTaxCode(taxCode: String): String = {
    if(isStandardTaxCode(taxCode) || isAdjustedTaxCode(taxCode))
      taxCode.stripSuffix(taxCode.substring(taxCode.length - 1, taxCode.length))
    else if(isUnTaxedIncomeTaxCode(taxCode) && (taxCode.toUpperCase.contains("S") || taxCode.toUpperCase.contains("K"))) {
        taxCode.toUpperCase.stripPrefix("S").stripPrefix("K")
    }
    else
      taxCode
  }

  def removeScottishElement(taxCode: String): String = {
    isValidScottishTaxCode(taxCode) match {
      case true => taxCode.substring(1,taxCode.length)
      case false => taxCode
    }
  }

}