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

import domain.{Aggregation, TaxBand, Allowance, Money}

//import uk.gov.hmrc.taxcalc.domain.{Aggregation, Allowance, Money, TaxBand}

trait CalculatorResponse {

  def success: Boolean = ???
  def result: Any = ???
}

case class ExcessPayResponse(override val success: Boolean, override val result: Money) extends CalculatorResponse

case class AllowanceResponse(override val success: Boolean, override val result: Seq[(String, Allowance)]) extends CalculatorResponse

case class TaxablePayResponse(override val success: Boolean, override val result: Money, isTapered: Boolean) extends CalculatorResponse

case class TaxBandResponse(override val success: Boolean, override val result: TaxBand) extends CalculatorResponse

case class NICRateCalculatorResponse(override val success: Boolean, override val result: Money) extends CalculatorResponse

case class RateCalculatorResponse(override val success: Boolean, override val result: Aggregation) extends CalculatorResponse

case class TaperingResponse(override val success: Boolean, override val result: String, isTapered: Boolean) extends CalculatorResponse
