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

///*
// * Copyright 2016 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package domain
//
//import utest.TestSuite
//import utest._
//
//// See
//// http://www.scala-js.org/tutorial/basic/
//// https://github.com/lihaoyi/utest
//
//object MoneyTests extends TestSuite{
//
//  val tests = this{
//
//    'should_do_no_rounding{
//      val money = Money(1000.4567)
//      assert(money.value.equals(BigDecimal(1000.4567)))
//    }
//
//    'should_round_to_2_decimal_places_with_no_rounding_up{
//      val money = Money(1000.4567, 2, false)
//      assert(money.value.equals(BigDecimal(1000.45)))
//    }
//
//    'should_round_to_3_decimal_places_with_no_rounding_up{
//      val money = Money(1000.4567, 3, false)
//      assert(money.value.equals(BigDecimal(1000.456)))
//    }
//
//    'should_round_up_to_2_decimal_places{
//      val money = Money(1000.4567, 2, true)
//      assert(money.value.equals(BigDecimal(1000.46)))
//    }
//
//    'should_round_up_to_1_decimal_places{
//      val money = Money(1000.4567, 1, true)
//      assert(money.value.equals(BigDecimal(1000.5)))
//    }
//  }
//
//}
