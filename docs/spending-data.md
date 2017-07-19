spending-data
----

* **URL**

  `uk.gov.hmrc.payeestimator.services.LiveGovernmentSpendingReceiptingService().getGovernmentSpendingData()`

* **Method:**
  
  `GET`
  
  
* **Success Response:**

    **Content:** 

```String
{
  "year": "2016",
  "totalGovernmentReceipts": "283000000000.00",
  "governmentSpending": [
    {
      "category": "Welfare",
      "percentage": "25",
      "spendingCategoryAmount": "70750000000.00"
    },
    {
      "category": "Health",
      "percentage": "19.9",
      "spendingCategoryAmount": "56317000000.00"
    },
    {
      "category": "State Pensions",
      "percentage": "12.8",
      "spendingCategoryAmount": "36224000000.00"
    },
    {
      "category": "Education",
      "percentage": "12",
      "spendingCategoryAmount": "33960000000.00"
    },
    {
      "category": "Defence",
      "percentage": "5.2",
      "spendingCategoryAmount": "14716000000.00"
    },
    {
      "category": "National Debt Interest",
      "percentage": "5.3",
      "spendingCategoryAmount": "14999000000.00"
    },
    {
      "category": "Public order & safety",
      "percentage": "4.3",
      "spendingCategoryAmount": "12169000000.00"
    },
    {
      "category": "Transport",
      "percentage": "4",
      "spendingCategoryAmount": "11320000000.00"
    },
    {
      "category": "Business and Industry",
      "percentage": "2.4",
      "spendingCategoryAmount": "6792000000.00"
    },
    {
      "category": "Government Administration",
      "percentage": "2",
      "spendingCategoryAmount": "5660000000.00"
    },
    {
      "category": "Culture e.g. sports, libraries, museums",
      "percentage": "1.6",
      "spendingCategoryAmount": "4528000000.00"
    },
    {
      "category": "Environment",
      "percentage": "1.7",
      "spendingCategoryAmount": "4811000000.00"
    },
    {
      "category": "Housing and Utilities",
      "percentage": "1.4",
      "spendingCategoryAmount": "3962000000.00"
    },
    {
      "category": "Overseas Aid",
      "percentage": "1.2",
      "spendingCategoryAmount": "3396000000.00"
    },
    {
      "category": "UK contribution to the EU budget",
      "percentage": "1.1",
      "spendingCategoryAmount": "3113000000.00"
    }
  ]
}
```