package uk.gov.hmrc.payeestimator.domain

import java.time.LocalDate

import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain.TaxCalcResourceBuilder.resourceForDate

class TaxCalcResourceSpec extends WordSpecLike with Matchers with TaxYearChanges{
  
  "Creating the TaxCalcResource" should {

    "return the 2017/18 TaxCalcResource on April 6th 2017" in {
      resourceForDate(LocalDate.of(2017, 4, 6), false) shouldBe TaxYear_2017_2018()
    }

    "return the 2017/18 TaxCalcResource on April 5th 2018" in {
      resourceForDate(LocalDate.of(2018, 4, 5), false) shouldBe TaxYear_2017_2018()
    }

    "return the 2018/19 TaxCalcResource on April 6th 2018" in {
      resourceForDate(LocalDate.of(2018, 4, 6), false) shouldBe TaxYear_2018_2019()
    }

    "return the 2018/19 TaxCalcResource until April 5th 2019" in {
      resourceForDate(LocalDate.of(2019, 4, 5), false) shouldBe TaxYear_2018_2019()
    }

    "throw an IllegalArgumentException for dates before 6th April 2016" in {
      val exception = intercept[IllegalArgumentException](resourceForDate(LocalDate.of(2016, 4, 5), false))
      exception.getMessage shouldBe "Unsupported Tax Period"
    }

    "throw an IllegalArgumentException on 6th April 2019" in {
      val exception = intercept[IllegalArgumentException](resourceForDate(LocalDate.of(2019, 4, 6), false))
      exception.getMessage shouldBe "Unsupported Tax Period"
    }

    "throw an IllegalArgumentException after 6th April 2019" in {
      val exception = intercept[IllegalArgumentException](resourceForDate(LocalDate.of(2019, 5, 1), false))
      exception.getMessage shouldBe "Unsupported Tax Period"
    }
  }
}
