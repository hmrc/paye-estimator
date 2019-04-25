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

import uk.gov.hmrc.payeestimator.domain.{Aggregation, Allowance, Band, Money}

trait CalculatorResponse {
  def success: Boolean
  def result:  Any
}

case class ExcessPayResponse(override val success: Boolean, override val result: Money) extends CalculatorResponse

case class AllowanceResponse(override val success: Boolean, override val result: Allowance) extends CalculatorResponse

case class TaxablePayResponse(override val success: Boolean, override val result: Money, isTapered: Boolean, additionalTaxablePay: Money)
    extends CalculatorResponse

case class TaxBandResponse(override val success: Boolean, override val result: Band) extends CalculatorResponse

case class NICRateCalculatorResponse(override val success: Boolean, override val result: Money) extends CalculatorResponse

case class RateCalculatorResponse(override val success: Boolean, override val result: Aggregation) extends CalculatorResponse

case class MaxRateCalculatorResponse(override val success: Boolean, override val result: Money) extends CalculatorResponse
