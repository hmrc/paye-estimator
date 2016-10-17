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

import org.scalatest.{Matchers, WordSpecLike}

class AllowanceCalculatorSpec extends WordSpecLike with Matchers {


  "AllowanceCalculator calculate " should {
    "should calculate a weekly allowance" in {
      val result = AllowanceCalculator("1100T", "weekly").calculate().result
      result.size shouldBe 1
      result.map{ allowance =>
        allowance._2.allowance.value shouldBe 211.73
        allowance._2.quotient.value  shouldBe 192.32
        allowance._2.remainder.value shouldBe 19.41
      }
    }
    "should calculate a monthly allowance" in {
      val result = AllowanceCalculator("1100T", "monthly").calculate().result
      result.size shouldBe 1
      result.map{ allowance =>
        allowance._2.allowance.value shouldBe 917.43
        allowance._2.quotient.value  shouldBe 833.34
        allowance._2.remainder.value shouldBe 84.09
      }
    }
    "should calculate a annual allowance" in {
      val result = AllowanceCalculator("1100T", "annual").calculate().result
      result.size shouldBe 1
      result.map{ allowance =>
        allowance._2.allowance.value shouldBe 11009.00
        allowance._2.quotient.value  shouldBe 0.00
        allowance._2.remainder.value shouldBe 0.00
      }
    }
    "should calculate a zero allowance with ZERO taxCode" in {
      val result = AllowanceCalculator("ZERO", "annual").calculate().result
      result.size shouldBe 3
      result.map{ allowance =>
        allowance._2.allowance.value shouldBe 0.00
        allowance._2.quotient.value  shouldBe 0.00
        allowance._2.remainder.value shouldBe 0.00
      }
    }
    "should calculate a zero allowance with K0" in {
      val result = AllowanceCalculator("K0", "weekly").calculate().result
      result.size shouldBe 3
      result.map{ allowance =>
        allowance._2.allowance.value shouldBe 0.00
        allowance._2.quotient.value  shouldBe 0.00
        allowance._2.remainder.value shouldBe 0.00
      }
    }
    "should calculate a zero allowance with 0L" in {
      val result = AllowanceCalculator("0L", "monthly").calculate().result
      result.size shouldBe 3
      result.map{ allowance =>
        allowance._2.allowance.value shouldBe 0.00
        allowance._2.quotient.value  shouldBe 0.00
        allowance._2.remainder.value shouldBe 0.00
      }
    }
  }
}
