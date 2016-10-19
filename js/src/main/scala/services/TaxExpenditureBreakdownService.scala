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


import domain.{CategoryUsage, Category, CategoryBreakdown}

import scala.concurrent.Future
//import scala.io.Source._
//import scala.tools.nsc.interpreter.InputStream
import scala.concurrent.ExecutionContext.Implicits.global

trait TaxExpenditureBreakdownService {

  // TODO...DROP FUTURES!!!
  def taxExpenditureCategoryPercentage(taxYear: Int) = {
    val dd = taxExpenditureCategoryBreakdown(taxYear)//.map {
    dd.categoryBreakdown.map(a => convertToCategoryUsage(a, dd.expenditureOnServices))

//      categoryBreakdown =>
//        for { category <- categoryBreakdown.categoryBreakdown
//        } yield (convertToCategoryUsage(category, categoryBreakdown.expenditureOnServices))
//    }
  }

  // TODO...DROP FUTURES!!!
  def taxExpenditureCategories(taxYear: Int): Seq[String] = {
    val dd = taxExpenditureCategoryBreakdown(taxYear)//.map{ categoryBreakdown =>
    dd.categoryBreakdown.map(a => a.category)
//        for {
//          category <- categoryBreakdown.categoryBreakdown
//        } yield (category.category)
//    }
  }

  def taxExpenditureCategoryBreakdown(taxYear: Int): CategoryBreakdown = {
// TODO...
    CategoryBreakdown(Seq.empty, BigDecimal(1), BigDecimal(1), BigDecimal(3))
//    getClass.getResourceAsStream(s"/categories/pesa_${taxYear}.json") match {
//      case is: InputStream => {
//        Future.successful(Json.parse(fromInputStream(is).mkString).as[CategoryBreakdown])
//      }
//      case _ => Future.failed(new BadRequestException(s"No Category Breakdown Found for ${taxYear}"))
//    }
  }

  private def calculatePercentage(totalExpenditureOfServices: BigDecimal, allocation: BigDecimal) : BigDecimal = {
   (allocation./(totalExpenditureOfServices)).*(BigDecimal(100)).setScale(2, BigDecimal.RoundingMode.HALF_UP)
  }

  private def convertToCategoryUsage(category: Category, totalExpenditureOfServices: BigDecimal) : CategoryUsage = {

    val subCategories = for {
      sub <- category.subCategory
    } yield (convertToCategoryUsage(sub, totalExpenditureOfServices))

    CategoryUsage(category.category, category.allocation, subCategories, calculatePercentage(totalExpenditureOfServices, category.allocation))
  }

}

object LiveTaxExpenditureBreakdownService extends TaxExpenditureBreakdownService {

}

object SandboxTaxExpenditureBreakdownService extends TaxExpenditureBreakdownService {

}
