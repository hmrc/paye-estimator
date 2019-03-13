package uk.gov.hmrc.payeestimator.services

import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.payeestimator.domain.{Aggregation, EmployeeNationalInsurance, EmployerNationalInsurance, NICTaxResult}

class NICTaxCategoryBuilderSpec extends WordSpecLike with Matchers {

  "NICTaxCategoryBuilder build" should {
    "sums the NIC aggregation results" in {
      val employeeNIC = Seq(Aggregation(BigDecimal(12), BigDecimal(1000.00)), Aggregation(BigDecimal(2), BigDecimal(500.00)))
      val employerNIC = Seq(Aggregation(BigDecimal(13.8), BigDecimal(300.00)))
      val nicResult   = NICTaxResult(BigDecimal(0), employeeNIC, employerNIC)
      val result      = NICTaxCategoryBuilder(nicResult).build()
      result.taxCategories.map { taxCategory =>
        taxCategory.taxType match {
          case EmployeeNationalInsurance =>
            taxCategory.total            shouldBe 1500
            taxCategory.aggregation.size shouldBe 2
          case EmployerNationalInsurance =>
            taxCategory.total            shouldBe 300
            taxCategory.aggregation.size shouldBe 1
        }
      }
    }
  }
}
