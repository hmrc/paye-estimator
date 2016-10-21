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

import java.time.LocalDate

import uk.gov.hmrc.payeestimator.domain.Money

trait LivePAYETaxCalcServiceSuccess {
  val service = new PAYETaxCalculatorService {
  }
}

trait LiveNICTaxCalcServiceSuccess {
  val service = new NICTaxCalculatorService {
  }
}

trait ExcessPayCalculatorSetup {
  val taxCode:String
  val payPeriod: String
  val date: LocalDate
  val taxablePay: Money
  val bandId: Int
}

trait ExcessPayCalculatorFullTaxableAmountSetup extends ExcessPayCalculatorSetup {
  override val date: LocalDate = LocalDate.now
  override val bandId: Int = 1
  override val taxablePay: Money = Money(BigDecimal.valueOf(60000.00))
}
