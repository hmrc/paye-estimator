receipting-data
----

* **URL**

  `uk.gov.hmrc.payeestimator.services.GovernmentSpendingReceiptingService().getGovernmentReceiptingData()`

* **Method:**
  
  `GET`
  
  
* **Success Response:**

    **Content:** 

```String
{
  "year": "2016",
  "governmentReceipting": [
    {
      "receiptSource": "Income Tax",
      "amount": "164000000000"
    },
    {
      "receiptSource": "National Insurance",
      "amount": "110000000000"
    },
    {
      "receiptSource": "Excise Duties",
      "amount": "47000000000"
    },
    {
      "receiptSource": "Corporation Tax",
      "amount": "43000000000"
    },
    {
      "receiptSource": "VAT",
      "amount": "111000000000"
    },
    {
      "receiptSource": "Business Rates",
      "amount": "27000000000"
    },
    {
      "receiptSource": "Council Tax",
      "amount": "28000000000"
    },
    {
      "receiptSource": "Other",
      "amount": "124000000000"
    }
  ]
}
```