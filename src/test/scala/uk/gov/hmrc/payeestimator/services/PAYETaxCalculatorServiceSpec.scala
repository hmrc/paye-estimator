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
import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain.{TaxBands, Money}


class PAYETaxCalculatorServiceSpec extends WordSpecLike with Matchers {


  "PAYETaxCalculatorService " should {
    "return the correct tax band for a 2016" in new LivePAYETaxCalcServiceSuccess {

      val date = LocalDate.of(2016, 9, 21)
      val result: TaxBands = service.getTaxBands(date)
      result.fromDate shouldBe LocalDate.of(2016,4,5)
    }

// TODO...WIP!!!
//    "return the correct tax band for a 2017" in new LivePAYETaxCalcServiceSuccess {
//
//      val date = LocalDate.of(2017, 8, 22)
//      val result: TaxBands = service.getTaxBands(date)
//      result.fromDate shouldBe LocalDate.of(2017,4,5)
//    }
  }

  "PAYETaxCalculatorService.isValidTaxCode " should {
    "return true if pattern matches ([0-9]{1,4}[L-N,l-n,T,t,X,x]{1}){1}" in new LivePAYETaxCalcServiceSuccess {
      service.isValidTaxCode("1000L") shouldBe true
    }

    "return false for X1919 its pattern doesn't match ([0-9]{1,4}[L-N,l-n,T,t,X,x]{1}){1}" in new LivePAYETaxCalcServiceSuccess {
      service.isValidTaxCode("X1919") shouldBe false
    }

    "return false for 99999m its pattern doesn't match ([0-9]{1,4}[L-N,l-n,T,t,X,x]{1}){1}" in new LivePAYETaxCalcServiceSuccess {
      service.isValidTaxCode("99999M") shouldBe false
    }

    "return true for 0X which matched the pattern ([0-9]{1,4}[L-N,l-n,T,t,X,x]{1}){1}" in new LivePAYETaxCalcServiceSuccess {
      service.isValidTaxCode("0X") shouldBe false
    }
  }
//
//  "PAYETaxCalculatorService.calculatePAYETaxablePay " should {
//    "calculate weekly taxable pay" in new LivePAYETaxCalcServiceSuccess {
//      val result = service.calculatePAYETaxablePay("1100T", "weekly", Money(673.08))
//      result.size shouldBe 1
//      result.head.value shouldBe 461.35
//    }
//
//    "calculate monthly taxable pay" in new LivePAYETaxCalcServiceSuccess {
//      val result = service.calculatePAYETaxablePay("1100T", "monthly",Money(2916.86))
//      result.size shouldBe 1
//      result.head.value shouldBe 1999.43
//    }
//
//    "calculate annual taxable pay" in new LivePAYETaxCalcServiceSuccess {
//      val result = service.calculatePAYETaxablePay("1100T", "annual", Money(35002.32))
//      result.size shouldBe 1
//      result.head.value shouldBe 23993.32
//    }
//  }
//
//  "PAYETaxCalculatorService.determineTaxBand " should {
//    "return annual taxBand 2" in new LivePAYETaxCalcServiceSuccess { //35002.32
//      val result = service.determineTaxBand("1100T", "annual", Money(23993.32))
//      result.band shouldBe 2
//    }
//    "return annual taxBand 3" in new LivePAYETaxCalcServiceSuccess { //70000
//      val result = service.determineTaxBand("1100T", "annual", Money(58991.00))
//      result.band shouldBe 3
//    }
//    "return annual taxBand 4" in new LivePAYETaxCalcServiceSuccess { //200000
//      val result = service.determineTaxBand("1100T", "annual", Money(188991.00))
//      result.band shouldBe 4
//    }
//    "return monthly taxBand 2" in new LivePAYETaxCalcServiceSuccess { //2120.20
//      val result = service.determineTaxBand("1100T", "monthly", Money(1102.77))
//      result.band shouldBe 2
//    }
//    "return monthly taxBand 3" in new LivePAYETaxCalcServiceSuccess { //11500.00
//      val result = service.determineTaxBand("1100T", "monthly", Money(10582.57))
//      result.band shouldBe 3
//    }
//    "return monthly taxBand 4" in new LivePAYETaxCalcServiceSuccess { //15000.00
//      val result = service.determineTaxBand("1100T", "monthly", Money(14082.57))
//      result.band shouldBe 4
//    }
//    "return weekly taxBand 2" in new LivePAYETaxCalcServiceSuccess { //600.00
//      val result = service.determineTaxBand("1100T", "weekly", Money(388.27))
//      result.band shouldBe 2
//    }
//    "return weekly taxBand 3" in new LivePAYETaxCalcServiceSuccess { //2550.55
//      val result = service.determineTaxBand("1100T", "weekly", Money(2338.82))
//      result.band shouldBe 3
//    }
//    "return weekly taxBand 4" in new LivePAYETaxCalcServiceSuccess { //4500.00
//      val result = service.determineTaxBand("1100T", "weekly", Money(4288.27))
//      result.band shouldBe 4
//    }
//  }
//
//  "PAYETaxCalculatorService.calculateExcessPay " should {
//    "calculate the annual excess value against tax band 2" in new LivePAYETaxCalcServiceSuccess {
//      val taxBand = service.determineTaxBand("1100T", "annual", Money(23993.32))
//      val result = service.calculateExcessPay(taxBand, "annual", Money(23993.32))
//      result.value shouldBe BigDecimal(23993)
//    }
//    "calculate the annual excess value against tax band 3" in new LivePAYETaxCalcServiceSuccess { //70000
//      val taxBand = service.determineTaxBand("1100T", "annual", Money(58991.00))
//      val result = service.calculateExcessPay(taxBand, "annual", Money(58991.00))
//      result.value shouldBe BigDecimal(26991)
//    }
//    "calculate the annual excess value against tax band 4" in new LivePAYETaxCalcServiceSuccess { //200000
//      val taxBand = service.determineTaxBand("1100T", "annual", Money(188991.00))
//      val result = service.calculateExcessPay(taxBand, "annual", Money(188991.00))
//      result.value shouldBe BigDecimal(38991)
//    }
//    "calculate the monthly excess value against tax band 2" in new LivePAYETaxCalcServiceSuccess {
//      val taxBand = service.determineTaxBand("1100T", "monthly", Money(1202.77))
//      val result = service.calculateExcessPay(taxBand, "monthly", Money(1202.77))
//      result.value shouldBe BigDecimal(1202)
//    }
//    "calculate the monthly excess value against tax band 3" in new LivePAYETaxCalcServiceSuccess {
//      val taxBand = service.determineTaxBand("1100T", "monthly", Money(10582.57))
//      val result = service.calculateExcessPay(taxBand, "monthly", Money(10582.57))
//      result.value shouldBe BigDecimal(7915.3334)
//    }
//    "calculate the monthly excess value against tax band 4" in new LivePAYETaxCalcServiceSuccess {
//      val taxBand = service.determineTaxBand("1100T", "monthly", Money(14082.57))
//      val result = service.calculateExcessPay(taxBand, "monthly", Money(14082.57))
//      result.value shouldBe BigDecimal(1582)
//    }
//    "calculate the weekly excess value against tax band 2" in new LivePAYETaxCalcServiceSuccess {
//      val taxBand = service.determineTaxBand("1100T", "weekly", Money(388.27))
//      val result = service.calculateExcessPay(taxBand, "weekly", Money(388.27))
//      result.value shouldBe BigDecimal(388)
//    }
//    "calculate the weekly excess value against tax band 3" in new LivePAYETaxCalcServiceSuccess {
//      val taxBand = service.determineTaxBand("1100T", "weekly", Money(2338.82))
//      val result = service.calculateExcessPay(taxBand, "weekly", Money(2338.82))
//      result.value shouldBe BigDecimal(1722.6154)
//    }
//    "calculate the weekly excess value against tax band 4" in new LivePAYETaxCalcServiceSuccess {
//      val taxBand = service.determineTaxBand("1100T", "weekly", Money(4288.27))
//      val result = service.calculateExcessPay(taxBand, "weekly", Money(4288.27))
//      result.value shouldBe BigDecimal(1403.3847)
//    }
//  }

  "PAYETaxCalculatorService.calculatePAYETax " should {
    "Calculate Annual PAYE tax for a gross salary of 35002.32 in tax band 2" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("1100T", "annual", Money(35002.32))
      result.payeTaxAmount.value shouldBe BigDecimal(4798.60)
    }

    "Calculate Annual PAYE tax for a gross salary of 70000.00 in tax band 3" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("1100T", "annual", Money(70000.00))
      result.payeTaxAmount.value shouldBe BigDecimal(17196.40)
    }

    "Calculate Annual PAYE tax for a gross salary of 200000.00 in tax band 4" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("1100T", "annual", Money(200000.00))
      result.payeTaxAmount.value shouldBe BigDecimal(71145.95)
    }

    "Calculate Monthly PAYE tax for a gross salary of 2120.20 in tax band 2" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("1100T", "monthly", Money(2120.20))
      result.payeTaxAmount.value shouldBe BigDecimal(240.40)
    }

// TODO...WIP!

//    "Calculate Monthly PAYE tax for a gross salary of 11500.00 in tax band 3" in new LivePAYETaxCalcServiceSuccess {
//      val result = service.calculatePAYETax("1100T", "monthly", Money(11500.00))
//      result.payeTaxAmount.value shouldBe BigDecimal(3699.46)
//    }
//
//    "Calculate Monthly PAYE tax for a gross salary of 15000.00 in tax band 4" in new LivePAYETaxCalcServiceSuccess {
//      val result = service.calculatePAYETax("1100T", "monthly", Money(15000.00))
//      result.payeTaxAmount.value shouldBe BigDecimal(5178.57)
//    }



    "Calculate Weekly PAYE tax for a gross salary of 600.00 in tax band 2" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("1100T", "weekly", Money(600.00))
      result.payeTaxAmount.value shouldBe BigDecimal(77.60)
    }

    "Calculate Weekly PAYE tax for a gross salary of 2550.55 in tax band 3" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("1100T", "weekly", Money(2550.55))
      result.payeTaxAmount.value shouldBe BigDecimal(812.13)
    }

    "Calculate Weekly PAYE tax for a gross salary of 4500.00 in tax band 4" in new LivePAYETaxCalcServiceSuccess {
      val result = service.calculatePAYETax("1100T", "weekly", Money(4500.00))
      result.payeTaxAmount.value shouldBe BigDecimal(1662.29)
    }
  }
}
