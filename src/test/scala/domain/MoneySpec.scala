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

package domain

import org.scalatest.{WordSpecLike, Matchers}

class MoneySpec extends WordSpecLike with Matchers {

  "Money " should {

    "should do no rounding" in {
      val money = Money(1000.4567)
      money.value shouldBe (BigDecimal(1000.4567))
    }

    "should round to 2 decimal places with no rounding up" in {
      val money = Money(1000.4567, 2, false)
      money.value shouldBe (BigDecimal(1000.45))
    }

    "should round to 3 decimal places with no rounding up" in {
      val money = Money(1000.4567, 3, false)
      money.value shouldBe BigDecimal(1000.456)
    }

    "should round up to 2 decimal places" in {
      val money = Money(1000.4567, 2, true)
      money.value shouldBe BigDecimal(1000.46)
    }

    "should round up to 1 decimal places" in {
      val money = Money(1000.4567, 1, true)
      money.value shouldBe BigDecimal(1000.5)
    }
  }

}
