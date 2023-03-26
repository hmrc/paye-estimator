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

import uk.gov.hmrc.payeestimator.domain.{Money, PAYETaxResult, TaxCalcResource}

trait PAYETaxCalculatorService extends TaxCalculatorHelper {

  def calculatePAYETax(taxCode: String, grossPay: Money, taxCalcResource: TaxCalcResource): PAYETaxResult = {
    def getPreviousBandMaxTaxAmount(band: Int): Option[BigDecimal] =
      taxCalcResource.getPreviousTaxBand(band).map(_.period.cumulativeMaxTax)

    if (!isValidTaxCode(taxCode, taxCalcResource)) throw new TaxCalculatorConfigException(s"Invalid Tax Code - $taxCode!")
    else {
      //Remove X for Basic Rate Tax Codes only
      val removeXOnTaxCode = if (isBasicRateTaxCode(taxCode, taxCalcResource.isScottish)) taxCode.stripSuffix("X") else taxCode
      val taxablePay           = TaxablePayCalculator(removeXOnTaxCode, grossPay, taxCalcResource).calculate()
      val taxBand              = TaxBandCalculator(removeXOnTaxCode, taxablePay.result, taxCalcResource).calculate().result
      val excessPay            = ExcessPayCalculator(removeXOnTaxCode, taxBand.band, taxablePay.result, taxCalcResource).calculate().result
      val finalBandTaxedAmount = Money(excessPay * (taxBand.rate / 100), 2, roundingUp = true)
      val previousBandMaxTax =
        if (taxBand.band > 1 && !isBasicRateTaxCode(removeXOnTaxCode, taxCalcResource.isScottish)) {
          Money(getPreviousBandMaxTaxAmount(taxBand.band).get, 2, roundingUp = true)
        } else {
          Money(0)
        }
      val rate = if (taxablePay.result.value == 0) BigDecimal(0) else taxBand.rate
      PAYETaxResult(
        taxablePay.result,
        excessPay,
        finalBandTaxedAmount,
        taxBand.band,
        previousBandMaxTax,
        rate,
        taxablePay.isTapered,
        taxablePay.additionalTaxablePay)
    }
  }
}

object LivePAYETaxCalculatorService extends PAYETaxCalculatorService {}
