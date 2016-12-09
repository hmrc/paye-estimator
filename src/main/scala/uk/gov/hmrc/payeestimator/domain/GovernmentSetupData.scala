package uk.gov.hmrc.payeestimator.domain

import scala.math.BigDecimal

case class GovernmentReceipt(receiptSource: String ,amount: BigDecimal)

case class GovernmentSpending(category: String, percentage: BigDecimal, val totalGovernmentReceipt: BigDecimal) {
    val spendingCategoryAmount = totalGovernmentReceipt * (percentage / 100)
}

case class GovernmentReceiptDataResponse(year: String, governmentReceipting: Seq[GovernmentReceipt])

object GovernmentReceiptData {

  val year = "2016"
  val tx1 = new GovernmentReceipt("Income Tax",BigDecimal(164.00))
  val governmentReceipting = Seq(GovernmentReceipt("Income Tax",BigDecimal(164.00)),
                                 GovernmentReceipt("National Insurance",BigDecimal(110.00)),
                                 GovernmentReceipt("Excise Duties",BigDecimal(470.00)),
                                 GovernmentReceipt("Corporation Tax",BigDecimal(430.00)),
                                 GovernmentReceipt("VAT",BigDecimal(111.00)),
                                 GovernmentReceipt("Business Rates",BigDecimal(270.00)),
                                 GovernmentReceipt("Council Tax",BigDecimal(280.00)),
                                 GovernmentReceipt("Other",BigDecimal(124.00)))
}

object GovernmentSpendingData {

  val receiptsYear = "2014-15"
  val totalGovernmentReceipts = GovernmentReceiptData.governmentReceipting.filter(
                                receipt => {
                                  receipt.receiptSource.equalsIgnoreCase("Income Tax") ||
                                  receipt.receiptSource.equalsIgnoreCase("National Insurance")
                                }).foldLeft(BigDecimal(0.0))(_ + _.amount)

  val governmentSpending = Seq(GovernmentSpending("Welfare"                                ,BigDecimal(25.30), totalGovernmentReceipts),
                               GovernmentSpending("Health"                                 ,BigDecimal(19.90), totalGovernmentReceipts),
                               GovernmentSpending("State Pensions"                         ,BigDecimal(12,80), totalGovernmentReceipts),
                               GovernmentSpending("Education"                              ,BigDecimal(12.50), totalGovernmentReceipts),
                               GovernmentSpending("Defence"                                ,BigDecimal(5.40) , totalGovernmentReceipts),
                               GovernmentSpending("National Debt Interest"                 ,BigDecimal(5.00) , totalGovernmentReceipts),
                               GovernmentSpending("Public order & safety"                  ,BigDecimal(4.40) , totalGovernmentReceipts),
                               GovernmentSpending("Transport"                              ,BigDecimal(3.00) , totalGovernmentReceipts),
                               GovernmentSpending("Business and Industry"                  ,BigDecimal(2.70) , totalGovernmentReceipts),
                               GovernmentSpending("Government Administration"              ,BigDecimal(2.00) , totalGovernmentReceipts),
                               GovernmentSpending("Culture e.g. sports, libraries, museums",BigDecimal(1.80) , totalGovernmentReceipts),
                               GovernmentSpending("Environment"                            ,BigDecimal(1.70) , totalGovernmentReceipts),
                               GovernmentSpending("Housing and Utilities"                  ,BigDecimal(1.60) , totalGovernmentReceipts),
                               GovernmentSpending("Overseas Aid"                           ,BigDecimal(1.30) , totalGovernmentReceipts),
                               GovernmentSpending("UK contribution to the EU budget"       ,BigDecimal(0.60) , totalGovernmentReceipts))
}