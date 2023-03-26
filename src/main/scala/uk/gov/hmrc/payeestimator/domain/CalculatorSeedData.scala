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

package uk.gov.hmrc.payeestimator.domain

import uk.gov.hmrc.payeestimator.services.TaxCalculatorHelper

import scala.math.BigDecimal.RoundingMode

class PAYEAllowanceSeedData(taxCodeNumber: BigDecimal) {
  val recalculatedTaxCodeNumber: BigDecimal = taxCodeNumber - 1
  val initQuotient:              Int        = (recalculatedTaxCodeNumber / 500).intValue()
  val initRemainder:             BigDecimal = recalculatedTaxCodeNumber % 500
  val middleRemainder:           BigDecimal = ((initRemainder + 1) * 10) + 9
}

class AnnualAllowance(taxCode: String, taxCodeNumber: BigDecimal) extends Allowance with TaxCalculatorHelper {
  override val quotient  = Money(0)
  override val remainder = Money(0)
  override val allowance: Money = if (taxCode.matches("([0]{1}[L-N,l-n,T,t,X,x]{1}){1}")) {
    Money(0)
  } else {
    (Money(taxCodeNumber) * 10) + 9
  }
}

class MonthlyAllowance(payeSeedData: PAYEAllowanceSeedData) extends Allowance {
  override val quotient  = Money(payeSeedData.initQuotient * 416.67)
  override val remainder = Money((payeSeedData.middleRemainder / 12).setScale(2, RoundingMode.UP))
  override val allowance: Money = quotient + remainder
}

class WeeklyAllowance(payeSeedData: PAYEAllowanceSeedData) extends Allowance {
  override val quotient  = Money(payeSeedData.initQuotient.*(96.16))
  override val remainder = Money((payeSeedData.middleRemainder / 52).setScale(2, RoundingMode.UP))
  override val allowance: Money = quotient.+(remainder)
}

class ZeroAllowance() extends Allowance {
  override val quotient  = Money(0)
  override val remainder = Money(0)
  override val allowance: Money = quotient.+(remainder)
}

trait Allowance {
  def quotient:  Money
  def remainder: Money
  def allowance: Money
}

object AnnualAllowance {
  def apply(taxCode: String, taxCodeNumber: BigDecimal): AnnualAllowance = new AnnualAllowance(taxCode, taxCodeNumber)
}

object MonthlyAllowance {
  def apply(payeSeedData: PAYEAllowanceSeedData): MonthlyAllowance = new MonthlyAllowance(payeSeedData)
}

object WeeklyAllowance {
  def apply(payeSeedData: PAYEAllowanceSeedData): WeeklyAllowance = new WeeklyAllowance(payeSeedData)
}

object ZeroAllowance {
  def apply(): ZeroAllowance = new ZeroAllowance()
}

object PAYEAllowanceSeedData {
  def apply(taxCodeNumber: BigDecimal): PAYEAllowanceSeedData = new PAYEAllowanceSeedData(taxCodeNumber)
}
