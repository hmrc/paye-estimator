receipting-data
----

* **URL**

  `uk.gov.hmrc.payeestimator.services.LiveGovernmentSpendingReceiptingService().getGovernmentReceiptingData()`

* **Method:**
  
  `GET`
  
  
* **Success Response:**

    **Content:** 

```String
{
  "year": "2015-16",
  "governmentReceipting": [
    {
      "receiptSource": "Income Tax",
      "amount": "169000000000.00"
    },
    {
      "receiptSource": "National Insurance",
      "amount": "114000000000.00"
    },
    {
      "receiptSource": "Excise Duties",
      "amount": "47000000000.00"
    },
    {
      "receiptSource": "Corporation Tax",
      "amount": "44000000000.00"
    },
    {
      "receiptSource": "VAT",
      "amount": "116000000000.00"
    },
    {
      "receiptSource": "Business Rates",
      "amount": "29000000000.00"
    },
    {
      "receiptSource": "Council Tax",
      "amount": "29000000000.00"
    },
    {
      "receiptSource": "Other",
      "amount": "132000000000.00"
    }
  ],
  "totalGovernmentReceipts": "680000000000.00"
}
```