package uk.gov.hmrc.payeestimator.services

import uk.gov.hmrc.payeestimator.domain.{GovernmentSpendingData, _}
import upickle.default._

import scala.scalajs.js.annotation.JSExport

trait GovernmentSpendingReceiptingService {

  @JSExport
  def getGovernmentReceiptingData(): String = write(buildGovernmentReceiptingData)

  def buildGovernmentReceiptingData: GovernmentReceiptDataResponse = {
    import GovernmentReceiptData._
    GovernmentReceiptDataResponse(year, governmentReceipting, totalGovernmentReceipts)
  }

  @JSExport
  def getGovernmentSpendingData(): String = write(buildGovernmentSpendingData)

  def buildGovernmentSpendingData: GovernmentSpendingDataResponse = {
    import GovernmentSpendingData._
    GovernmentSpendingDataResponse(year, totalGovernmentReceipts, governmentSpending)
  }

}

@JSExport
object LiveGovernmentSpendingReceiptingService extends GovernmentSpendingReceiptingService {
}
