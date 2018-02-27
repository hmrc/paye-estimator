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

import uk.gov.hmrc.payeestimator.domain.{TaxYear_2017_2018, TaxYear_2018_2019}

trait LivePAYETaxCalcServiceSuccess {
  val service = new PAYETaxCalculatorService {
  }

  val taxCalcResource2017 = new TaxYear_2017_2018(false)
  val taxCalcResource2018 = new TaxYear_2018_2019(false)
}

trait LiveNICTaxCalcServiceSuccess {
  val service = new NICTaxCalculatorService {
  }
}

trait LiveTaxCalcServiceSuccess {
  val service = new TaxCalculatorService {
    override val nicTaxCalculatorService: NICTaxCalculatorService = new NICTaxCalculatorService {}
    override val payeTaxCalculatorService: PAYETaxCalculatorService = new PAYETaxCalculatorService {}
  }

  val date = LocalDate.now()
}

trait LiveGovernmentSpendingReceiptingServiceSuccess {
  val service = LiveGovernmentSpendingReceiptingService
}

trait TaxCalculatorHelperSetup {
  val helper = new TaxCalculatorHelper {}
}
