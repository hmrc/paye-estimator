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
  "totalGovernmentReceipts": "274000000000.00",
  "governmentSpending": [
    {
      "category": "Welfare",
      "percentage": "25",
      "spendingCategoryAmount": "69322000000.00"
    },
    {
      "category": "Health",
      "percentage": "19.9",
      "spendingCategoryAmount": "54526000000.00"
    },
    {
      "category": "State Pensions",
      "percentage": "12.8",
      "spendingCategoryAmount": "35072000000.00"
    },
    {
      "category": "Education",
      "percentage": "12",
      "spendingCategoryAmount": "34250000000.00"
    },
    {
      "category": "Defence",
      "percentage": "5.2",
      "spendingCategoryAmount": "14796000000.00"
    },
    {
      "category": "National Debt Interest",
      "percentage": "5.3",
      "spendingCategoryAmount": "13700000000.00"
    },
    {
      "category": "Public order & safety",
      "percentage": "4.3",
      "spendingCategoryAmount": "12056000000.00"
    },
    {
      "category": "Transport",
      "percentage": "4",
      "spendingCategoryAmount": "8220000000.00"
    },
    {
      "category": "Business and Industry",
      "percentage": "2.4",
      "spendingCategoryAmount": "7398000000.00"
    },
    {
      "category": "Government Administration",
      "percentage": "2",
      "spendingCategoryAmount": "5480000000.00"
    },
    {
      "category": "Culture e.g. sports, libraries, museums",
      "percentage": "1.6",
      "spendingCategoryAmount": "4932000000.00"
    },
    {
      "category": "Environment",
      "percentage": "1.7",
      "spendingCategoryAmount": "4658000000.00"
    },
    {
      "category": "Housing and Utilities",
      "percentage": "1.4",
      "spendingCategoryAmount": "4384000000.00"
    },
    {
      "category": "Overseas Aid",
      "percentage": "1.2",
      "spendingCategoryAmount": "3562000000.00"
    },
    {
      "category": "UK contribution to the EU budget",
      "percentage": "1.1",
      "spendingCategoryAmount": "1644000000.00"
    }
  ]
}
```