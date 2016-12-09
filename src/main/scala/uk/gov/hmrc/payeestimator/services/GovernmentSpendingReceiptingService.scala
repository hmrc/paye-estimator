package uk.gov.hmrc.payeestimator.services

import uk.gov.hmrc.payeestimator.domain._
import upickle.default._

import scala.scalajs.js.annotation.JSExport

trait GovernmentSpendingReceiptingService {

  @JSExport
  def getGovernmentReceiptingData(): String = {
    val receiptingData = GovernmentReceiptData
    val rescieptingDataResponse = GovernmentReceiptDataResponse(receiptingData.year, receiptingData.governmentReceipting)
    val result = buildResponse(rescieptingDataResponse)
    result
  }

  @JSExport
  def getGovernmentSpendingData(): String = {
    val spendingData = GovernmentSpendingData
    write(spendingData)
  }

  @JSExport
  def buildResponse(governmentReceiptData:GovernmentReceiptDataResponse) = write(GovernmentReceiptData)
}

@JSExport
object LiveGovernmentSpendingReceiptingService extends GovernmentSpendingReceiptingService {
}
