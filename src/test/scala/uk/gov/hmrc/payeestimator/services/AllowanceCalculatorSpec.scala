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

import org.scalatest.{Matchers, WordSpecLike}

class AllowanceCalculatorSpec extends WordSpecLike with Matchers {


  "AllowanceCalculator calculate " should {
    "should calculate a weekly allowance" in {
      val result = AllowanceCalculator("1100T").calculate().result
      result.allowance.value shouldBe 11009.00
      result.quotient.value  shouldBe 0
      result.remainder.value shouldBe 0
    }
    "should calculate a zero allowance with ZERO taxCode" in {
      val result = AllowanceCalculator("ZERO").calculate().result
      result.allowance.value shouldBe 0.00
      result.quotient.value  shouldBe 0.00
      result.remainder.value shouldBe 0.00
    }
    "should calculate a zero allowance with K0" in {
      val result = AllowanceCalculator("K0").calculate().result
      result.allowance.value shouldBe 0.00
      result.quotient.value  shouldBe 0.00
      result.remainder.value shouldBe 0.00
    }
    "should calculate a zero allowance with 0L" in {
      val result = AllowanceCalculator("0L").calculate().result
      result.allowance.value shouldBe 0.00
      result.quotient.value  shouldBe 0.00
      result.remainder.value shouldBe 0.00
    }
  }
}
