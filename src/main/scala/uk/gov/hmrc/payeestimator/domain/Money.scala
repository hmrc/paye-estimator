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

package uk.gov.hmrc.payeestimator.domain

import scala.math.BigDecimal.RoundingMode

class Money(amount: BigDecimal, decimalPlaces: Int, roundingUp: Boolean){

  val roundingMode =  if (roundingUp) RoundingMode.HALF_UP else RoundingMode.DOWN

  def value: BigDecimal = {
    if(decimalPlaces >= 0) {
      val scaledAmount = amount.setScale(decimalPlaces, roundingMode)
      scaledAmount
    } else {
      amount
    }
  }

  def /(that: Money): Money = {
    Money(value./(that.value), decimalPlaces, roundingUp)
  }
  def /(that: BigDecimal): Money = {
    Money(value./(that), decimalPlaces, roundingUp)
  }

  def *(that: Money): Money = {
    Money(value.*(that.value), decimalPlaces, roundingUp)
  }
  def *(that: BigDecimal): Money = {
    Money(value.*(that), decimalPlaces, roundingUp)
  }

  def -(that: Money): Money = {
    Money(value.-(that.value), decimalPlaces, roundingUp)
  }

  def -(that: BigDecimal): Money = {
    Money(value.-(that), decimalPlaces, roundingUp)
  }

  def +(that: Money): Money = {
    Money(value.+(that.value), decimalPlaces, roundingUp)
  }

  def +(that: BigDecimal): Money = {
    Money(value.+(that), decimalPlaces, roundingUp)
  }

  def >(that: Money): Boolean = {
    value > that.value
  }
  def >(that: BigDecimal): Boolean = {
    value > that
  }

  def <(that: Money): Boolean = {
    value < that.value
  }

  def >=(that: Money): Boolean = {
    value >= that.value
  }

  def <=(that: Money): Boolean = {
    value <= that.value
  }

  def ==(that: Money): Boolean = {
    value == that.value
  }

  def !=(that: Money): Boolean = {
    value != that.value
  }

}

object Money {
  def apply(amount: Money, decimalPlaces: Int, roundingUp: Boolean): Money = new Money(amount.value, decimalPlaces, roundingUp)
  def apply(amount: Money): Money = new Money(amount.value, -1, false)
  def apply(value: BigDecimal, decimalPlaces: Int, roundingUp: Boolean): Money = new Money(value, decimalPlaces, roundingUp)
  def apply(value: BigDecimal): Money = new Money(value, -1, false)
}
