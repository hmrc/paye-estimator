/*
 * Copyright 2023 HM Revenue & Customs
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

import uk.gov.hmrc.payeestimator.domain._

import scala.math.BigDecimal

trait Builder {

  def build(): BuildResult

  protected def calculateAggregationTotal(aggregation: Seq[Aggregation]): BigDecimal =
    aggregation match {
      case aggregate: Seq[Aggregation] => aggregate.foldLeft(BigDecimal(0.0))(_ + _.amount)
      case _ => BigDecimal(0.0)
    }
}

case class PAYEAggregateBuilder(taxCode: String, bandId: Int, payeTaxAmount: Money, taxCalcResource: TaxCalcResource)
    extends Builder
    with TaxCalculatorHelper {

  private val taxbands = taxCalcResource.taxBands
  private val numberOfBands: Int = taxbands.taxBands.size

  override def build(): AggregationBuildResult =
    if (isBasicRateTaxCode(taxCode, taxCalcResource.isScottish)) {
      AggregationBuildResult(taxbands.taxBands.filter(_.band != 1).collect(BasicRatePAYEAggregationFunc()))
    } else
      appendAggregate(AggregationBuildResult(taxbands.taxBands.collect(PAYEAggregationFunc())))

  private def PAYEAggregationFunc(): PartialFunction[Band, Aggregation] = {
    case taxBand if taxBand.band == bandId && payeTaxAmount != Money(0) && taxBand.band != numberOfBands =>
      val sum = taxbands.taxBands.filter(_.band < bandId).collect(previousBandMaxTaxFunction()).foldLeft(BigDecimal(0.0))(_ + _)
      Aggregation(taxBand.rate, (payeTaxAmount - sum).value)
    case taxBand if taxBand.band < bandId && taxBand.band != 1 && payeTaxAmount != Money(0) => createPAYEAggregation(taxBand)
    case taxBand if taxBand.band != 1 && payeTaxAmount == Money(0)                          => Aggregation(taxBand.rate, BigDecimal(0.0))
    case taxBand if taxBand.band > bandId && taxBand.band != numberOfBands                  => Aggregation(taxBand.rate, BigDecimal(0.0))
  }

  private def previousBandMaxTaxFunction(): PartialFunction[Band, BigDecimal] = {
    case taxBand => taxBand.period.maxTax
  }

  private def createPAYEAggregation(taxBand: Band): Aggregation =
    Aggregation(taxBand.rate, Money(taxBand.period.maxTax, 2, roundingUp = true).value)

  private def appendAggregate(result: AggregationBuildResult): AggregationBuildResult =
    if (payeTaxAmount != Money(0)) {
      val total  = calculateAggregationTotal(result.aggregation)
      val amount = if (payeTaxAmount.value <= total) Money(0) else payeTaxAmount - total
      val append = Seq(Aggregation(taxbands.taxBands.last.rate, amount.value))
      AggregationBuildResult(result.aggregation ++ append)
    } else {
      result
    }

  private def BasicRatePAYEAggregationFunc(): PartialFunction[Band, Aggregation] = {
    case taxBand if taxBand.band == bandId => Aggregation(taxBand.rate, payeTaxAmount.value)
    case taxBand if taxBand.band != bandId => Aggregation(taxBand.rate, BigDecimal(0.0))
  }
}

case class NICTaxCategoryBuilder(taxResult: NICTaxResult) extends Builder {

  override def build(): TaxCategoryBuildResult =
    TaxCategoryBuildResult(
      Seq(
        TaxCategory(taxType = EmployeeNationalInsurance.toString, calculateAggregationTotal(taxResult.employeeNIC), taxResult.employeeNIC),
        TaxCategory(taxType = EmployerNationalInsurance.toString, calculateAggregationTotal(taxResult.employerNIC), taxResult.employerNIC)
      ))
}

trait BuildResult

case class AggregationBuildResult(aggregation: Seq[Aggregation]) extends BuildResult

case class TaxCategoryBuildResult(taxCategories: Seq[TaxCategory]) extends BuildResult
