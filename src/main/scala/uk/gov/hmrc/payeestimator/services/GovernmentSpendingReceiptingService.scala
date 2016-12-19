package uk.gov.hmrc.payeestimator.services

import uk.gov.hmrc.payeestimator.domain.{GovernmentSpendingData, _}
import upickle.default._

import scala.scalajs.js.annotation.JSExport

trait GovernmentSpendingReceiptingService {

  @JSExport
  def getGovernmentReceiptingData(): String = {
    val receiptingData = GovernmentReceiptData
    val receiptingDataResponse = GovernmentReceiptDataResponse(receiptingData.year, receiptingData.governmentReceipting)
    val result = buildReceiptingResponse(receiptingDataResponse)
    result
  }

  @JSExport
  def getGovernmentSpendingData(): String = {
    val spendingData = GovernmentSpendingData
    val spendingDataResponse = GovernmentSpendingDataResponse(spendingData.year, spendingData.totalGovernmentReceipts, spendingData.governmentSpending)
    buildSpendingResponse(spendingDataResponse)
  }

  @JSExport
  def buildReceiptingResponse(governmentReceiptData:GovernmentReceiptDataResponse) = write(governmentReceiptData)

  @JSExport
  def buildSpendingResponse(governmentSpendingData:GovernmentSpendingDataResponse) = write(governmentSpendingData)
}

@JSExport
object LiveGovernmentSpendingReceiptingService extends GovernmentSpendingReceiptingService {
}
