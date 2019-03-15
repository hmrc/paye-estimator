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

import uk.gov.hmrc.payeestimator.domain.{TaxYear_2017_2018, TaxYear_2018_2019, TaxYear_2019_2020}

trait LivePAYETaxCalcServiceSuccess {
  val service: PAYETaxCalculatorService = new PAYETaxCalculatorService {}

  val taxCalcResource2017         = TaxYear_2017_2018()
  val taxCalcResource2018         = TaxYear_2018_2019()
  val taxCalcResource2019         = TaxYear_2019_2020()
  val taxCalcResource2017Scottish = TaxYear_2017_2018(isScottish = true)
  val taxCalcResource2018Scottish = TaxYear_2018_2019(isScottish = true)
  val taxCalcResource2019Scottish = TaxYear_2019_2020(isScottish = true)

}

trait LiveNICTaxCalcServiceSuccess {
  val service: NICTaxCalculatorService = new NICTaxCalculatorService {}
}

trait LiveTaxCalcServiceSuccess {
  val service: TaxCalculatorService = new TaxCalculatorService {
    override val nicTaxCalculatorService:  NICTaxCalculatorService  = new NICTaxCalculatorService {}
    override val payeTaxCalculatorService: PAYETaxCalculatorService = new PAYETaxCalculatorService {}
  }

  val date: LocalDate = LocalDate.now()
}

trait TaxCalculatorHelperSetup {
  val helper: TaxCalculatorHelper = new TaxCalculatorHelper {}
}
