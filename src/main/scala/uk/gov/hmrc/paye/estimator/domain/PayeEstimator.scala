package uk.gov.hmrc.paye.estimator.domain

import scala.scalajs.js.annotation.{JSExport, JSExportAll}

@JSExport
object PayeEstimator{
  @JSExport
  def calculation() = println("1 million pounds")
}

@JSExport
class PayeEstimator2{
  @JSExport
  def calculation() = println("2 million pounds")
}