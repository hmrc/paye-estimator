package uk.gov.hmrc.payeestimator.services

import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain._

class PAYEAggregateBuilderSpec extends WordSpecLike with Matchers with TaxYearChanges{
  "For 2017-18 PAYEAggregateBuilder build" should {
    val taxYear = new TaxYear_2017_2018(false)

    "build all aggregates with a value of 0 if in the first band and a Basic Rate Tax Code" in {
      val resultBR = PAYEAggregateBuilder("BR", 1, Money(160000, 2, true), taxYear).build
      resultBR.aggregation.map{ aggregation =>
        aggregation.amount shouldBe BigDecimal(0)
      }
      val resultD0 = PAYEAggregateBuilder("D0", 1, Money(160000, 2, true), taxYear).build
      resultD0.aggregation.map { aggregation =>
        aggregation.amount shouldBe BigDecimal(0)
      }
      val resultD1 = PAYEAggregateBuilder("D1", 1, Money(160000, 2, true), taxYear).build
      resultD1.aggregation.map { aggregation =>
        aggregation.amount shouldBe BigDecimal(0)
      }
    }
    "build aggregate band 2 with the entire value ignoring the band max for Basic Rate Tax Code" in {
      val resultBR = PAYEAggregateBuilder("BR", 2, Money(160000, 2, true), taxYear).build
      resultBR.aggregation(0).amount shouldBe 160000
      resultBR.aggregation(1).amount shouldBe 0
      resultBR.aggregation(2).amount shouldBe 0

      val resultD0 = PAYEAggregateBuilder("D0", 2, Money(160000, 2, true), taxYear).build
      resultD0.aggregation(0).amount shouldBe 160000
      resultD0.aggregation(1).amount shouldBe 0
      resultD0.aggregation(2).amount shouldBe 0

      val resultD1 = PAYEAggregateBuilder("D1", 2, Money(160000, 2, true), taxYear).build
      resultD1.aggregation(0).amount shouldBe 160000
      resultD1.aggregation(1).amount shouldBe 0
      resultD1.aggregation(2).amount shouldBe 0
    }
    "build aggregate band 3 with the entire value ignoring the band max for Basic Rate Tax Code" in {
      val resultBR = PAYEAggregateBuilder("BR", 3, Money(160000, 2, true), taxYear).build
      resultBR.aggregation(0).amount shouldBe 0
      resultBR.aggregation(1).amount shouldBe 160000
      resultBR.aggregation(2).amount shouldBe 0

      val resultD0 = PAYEAggregateBuilder("D0", 3, Money(160000, 2, true), taxYear).build
      resultD0.aggregation(0).amount shouldBe 0
      resultD0.aggregation(1).amount shouldBe 160000
      resultD0.aggregation(2).amount shouldBe 0

      val resultD1 = PAYEAggregateBuilder("D1", 3, Money(160000, 2, true), taxYear).build
      resultD1.aggregation(0).amount shouldBe 0
      resultD1.aggregation(1).amount shouldBe 160000
      resultD1.aggregation(2).amount shouldBe 0
    }
    "build aggregate band 4 with the entire value ignoring the band max for Basic Rate Tax Code" in {
      val resultBR = PAYEAggregateBuilder("BR", 4, Money(160000, 2, true), taxYear).build
      resultBR.aggregation(0).amount shouldBe 0
      resultBR.aggregation(1).amount shouldBe 0
      resultBR.aggregation(2).amount shouldBe 160000

      val resultD0 = PAYEAggregateBuilder("D0", 4, Money(160000, 2, true), taxYear).build
      resultD0.aggregation(0).amount shouldBe 0
      resultD0.aggregation(1).amount shouldBe 0
      resultD0.aggregation(2).amount shouldBe 160000

      val resultD1 = PAYEAggregateBuilder("D1", 4, Money(160000, 2, true), taxYear).build
      resultD1.aggregation(0).amount shouldBe 0
      resultD1.aggregation(1).amount shouldBe 0
      resultD1.aggregation(2).amount shouldBe 160000
    }
    "build aggregate band 1 for the entire amount for any tax code" in {
      val result = PAYEAggregateBuilder("Any Code", 1, Money(160000, 2, true), taxYear).build
      result.aggregation(0).amount shouldBe 160000
      result.aggregation(1).amount shouldBe 0
      result.aggregation(2).amount shouldBe 0
    }
    "build aggregate band 2 for the entire amount for any tax code, ignoring the first band" in {
      val result = PAYEAggregateBuilder("Any Code", 2, Money(160000, 2, true), taxYear).build
      result.aggregation(0).amount shouldBe 160000
      result.aggregation(1).amount shouldBe 0
      result.aggregation(2).amount shouldBe 0
    }
    "build aggregate band 2 and 3 using the max amount for band 2 and the balance to band 3 for any tax code, ignoring the first band" in {
      val result = PAYEAggregateBuilder("Any Code", 3, Money(160000, 2, true), taxYear).build
      result.aggregation(0).amount shouldBe 6700
      result.aggregation(1).amount shouldBe 153300
      result.aggregation(2).amount shouldBe 0
    }
    "build aggregate band 2, 3 and 4 using the max amount for band 2 and 3 and the balance to band 4 for any tax code, ignoring the first band" in {
      val result = PAYEAggregateBuilder("Any Code", 4, Money(160000, 2, true), taxYear).build
      println("result " + result)
      result.aggregation(0).amount shouldBe 6700
      result.aggregation(1).amount shouldBe 46600
      result.aggregation(2).amount shouldBe 106700
    }
  }

  "For 2018-19 PAYEAggregateBuilder build" should {
    val taxYear = new TaxYear_2018_2019(false)

    "build all aggregates with a value of 0 if in the first band and a Basic Rate Tax Code" in {
      val resultBR = PAYEAggregateBuilder("BR", 1, Money(160000, 2, true), taxYear).build
      resultBR.aggregation.map{ aggregation =>
        aggregation.amount shouldBe BigDecimal(0)
      }
      val resultD0 = PAYEAggregateBuilder("D0", 1, Money(160000, 2, true), taxYear).build
      resultD0.aggregation.map { aggregation =>
        aggregation.amount shouldBe BigDecimal(0)
      }
      val resultD1 = PAYEAggregateBuilder("D1", 1, Money(160000, 2, true), taxYear).build
      resultD1.aggregation.map { aggregation =>
        aggregation.amount shouldBe BigDecimal(0)
      }
    }
    "build aggregate band 2 with the entire value ignoring the band max for Basic Rate Tax Code" in {
      val resultBR = PAYEAggregateBuilder("BR", 2, Money(160000, 2, true), taxYear).build
      resultBR.aggregation(0).amount shouldBe 160000
      resultBR.aggregation(1).amount shouldBe 0
      resultBR.aggregation(2).amount shouldBe 0

      val resultD0 = PAYEAggregateBuilder("D0", 2, Money(160000, 2, true), taxYear).build
      resultD0.aggregation(0).amount shouldBe 160000
      resultD0.aggregation(1).amount shouldBe 0
      resultD0.aggregation(2).amount shouldBe 0

      val resultD1 = PAYEAggregateBuilder("D1", 2, Money(160000, 2, true), taxYear).build
      resultD1.aggregation(0).amount shouldBe 160000
      resultD1.aggregation(1).amount shouldBe 0
      resultD1.aggregation(2).amount shouldBe 0
    }
    "build aggregate band 3 with the entire value ignoring the band max for Basic Rate Tax Code" in {
      val resultBR = PAYEAggregateBuilder("BR", 3, Money(160000, 2, true), taxYear).build
      resultBR.aggregation(0).amount shouldBe 0
      resultBR.aggregation(1).amount shouldBe 160000
      resultBR.aggregation(2).amount shouldBe 0

      val resultD0 = PAYEAggregateBuilder("D0", 3, Money(160000, 2, true), taxYear).build
      resultD0.aggregation(0).amount shouldBe 0
      resultD0.aggregation(1).amount shouldBe 160000
      resultD0.aggregation(2).amount shouldBe 0

      val resultD1 = PAYEAggregateBuilder("D1", 3, Money(160000, 2, true), taxYear).build
      resultD1.aggregation(0).amount shouldBe 0
      resultD1.aggregation(1).amount shouldBe 160000
      resultD1.aggregation(2).amount shouldBe 0
    }
    "build aggregate band 4 with the entire value ignoring the band max for Basic Rate Tax Code" in {
      val resultBR = PAYEAggregateBuilder("BR", 4, Money(160000, 2, true), taxYear).build
      resultBR.aggregation(0).amount shouldBe 0
      resultBR.aggregation(1).amount shouldBe 0
      resultBR.aggregation(2).amount shouldBe 160000

      val resultD0 = PAYEAggregateBuilder("D0", 4, Money(160000, 2, true), taxYear).build
      resultD0.aggregation(0).amount shouldBe 0
      resultD0.aggregation(1).amount shouldBe 0
      resultD0.aggregation(2).amount shouldBe 160000

      val resultD1 = PAYEAggregateBuilder("D1", 4, Money(160000, 2, true), taxYear).build
      resultD1.aggregation(0).amount shouldBe 0
      resultD1.aggregation(1).amount shouldBe 0
      resultD1.aggregation(2).amount shouldBe 160000
    }
    "build aggregate band 1 for the entire amount for any tax code" in {
      val result = PAYEAggregateBuilder("Any Code", 1, Money(160000, 2, true), taxYear).build
      result.aggregation(0).amount shouldBe 160000
      result.aggregation(1).amount shouldBe 0
      result.aggregation(2).amount shouldBe 0
    }
    "build aggregate band 2 for the entire amount for any tax code, ignoring the first band" in {
      val result = PAYEAggregateBuilder("Any Code", 2, Money(160000, 2, true), taxYear).build
      result.aggregation(0).amount shouldBe 160000
      result.aggregation(1).amount shouldBe 0
      result.aggregation(2).amount shouldBe 0
    }
    "build aggregate band 2 and 3 using the max amount for band 2 and the balance to band 3 for any tax code, ignoring the first band" in {
      val result = PAYEAggregateBuilder("Any Code", 3, Money(160000, 2, true), taxYear).build
      result.aggregation(0).amount shouldBe 6900
      result.aggregation(1).amount shouldBe 153100
      result.aggregation(2).amount shouldBe 0
    }
    "build aggregate band 2, 3 and 4 using the max amount for band 2 and 3 and the balance to band 4 for any tax code, ignoring the first band" in {
      val result = PAYEAggregateBuilder("Any Code", 4, Money(160000, 2, true), taxYear).build
      println("result " + result)
      result.aggregation(0).amount shouldBe 6900
      result.aggregation(1).amount shouldBe 46200
      result.aggregation(2).amount shouldBe 106900
    }
  }
}
