package uk.gov.hmrc.payeestimator.domain

import java.time.LocalDate

import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain.TaxCalcResourceBuilder.resourceForDate

class TaxCalcResourceSpec extends WordSpecLike with Matchers {
  
  "Creating the TaxCalcResource" should {

    "return the 2016/17 TaxCalcResource when the date is before 6th April 2017" in {
      resourceForDate(LocalDate.of(2016, 4, 6), false) shouldBe new TaxYear_2016_2017(false)
    }

    "return the 2016/17 TaxCalcResource when the date is on 6th April 2017" in {
      resourceForDate(LocalDate.of(2017, 4, 6), false) shouldBe new TaxYear_2017_2018(false)
    }

    "return the 2017/18 TaxCalcResource when the date is after April 6th 2017" in {
      resourceForDate(LocalDate.of(2017, 5, 1), false) shouldBe new TaxYear_2017_2018(false)
    }

    "throw an IllegalArgumentException for dates before 6th April 2016" in {
      val exception = intercept[IllegalArgumentException](resourceForDate(LocalDate.of(2016, 4, 5), false))
      exception.getMessage shouldBe "Unsupported Tax Period"
    }

    "throw an IllegalArgumentException for dates after 6th April 2018" in {
      val exception = intercept[IllegalArgumentException](resourceForDate(LocalDate.of(2018, 4, 7), false))
      exception.getMessage shouldBe "Unsupported Tax Period"
    }
  }
}
