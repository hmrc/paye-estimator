calculate-tax
----

  HMRC Tax Calculator service API - calculates income tax, as well as national insurance contributions for Employers and Employees
  
* **URL**

  `uk.gov.hmrc.payeestimator.services.LiveTaxCalculatorService().calculateTax(isStatePensionAge: String, taxYear: Int, taxCode: String, grossPayPence: Int, payPeriod: String, hoursIn: Int)`

* **Method:**
  
  `GET`
  
   **Required:**

   * `isStatePensionAge=[Boolean]`
   Accepted values are true\false
   * `taxYear=[Integer]`
   Not used at present.
   * `taxCode=[String]`
   The taxCode must be a valid tax code [More...](valid-tax-codes.md) 
   * `grossPay=[Integer]`
   The grossPay value is a pence value, example £100.55 should be represented as 10055.
   * `payPeriod=[String]`
   The payPeriod accepted values are 'weekly', 'monthly' and 'annual'.
   * `hoursIn=[Integer]` 
   Hours to be supplied when tax calculation to be performed on an hourly rate. The hourly rate will be supplied as the grossPay. 
   
* **Success Response:**

    **Content:** 

```String
{
  "statePensionAge": false,
  "taxCode": "S1100L",
  "payPerHour": 55,
  "hours": 40,
  "averageAnnualTaxRate": 37.99,
  "marginalTaxRate": 42,
  "maxTaxRate":"50"
  "payeBand": 40,
  "employeeNICBand": 2,
  "tapered": true,
  "taxBreakdown": [
    {
      "period": "annual",
      "grossPay": 114400,
      "taxFreePay": 3809,
      "taxablePay": 110591,
      "scottishElement": 3783.64,
      "maxTaxAmount":"-1",
      "taxCategories": [
        {
          "taxType": "incomeTax",
          "total": 37836.4,
          "aggregation": [
            {
              "percentage": 20,
              "amount": 6400
            },
            {
              "percentage": 40,
              "amount": 31436.4
            },
            {
              "percentage": 45,
              "amount": 0
            }
          ]
        },
        {
          "taxType": "employeeNationalInsurance",
          "total": 5620.8,
          "aggregation": [
            {
              "percentage": 12,
              "amount": 4192.8
            },
            {
              "percentage": 2,
              "amount": 1428
            }
          ]
        },
        {
          "taxType": "employerNationalInsurance",
          "total": 14667.74,
          "aggregation": [
            {
              "percentage": 13.8,
              "amount": 14667.74
            }
          ]
        }
      ],
      "totalDeductions": 43457.2,
      "takeHomePay": 70942.8
    },
    {
      "period": "monthly",
      "grossPay": 9533.33,
      "taxFreePay": 317.5,
      "taxablePay": 9215.83,
      "scottishElement": 315.3,
      "maxTaxAmount":"-1",
      "taxCategories": [
        {
          "taxType": "incomeTax",
          "total": 3153.03,
          "aggregation": [
            {
              "percentage": 20,
              "amount": 533.33
            },
            {
              "percentage": 40,
              "amount": 2619.7
            },
            {
              "percentage": 45,
              "amount": 0
            }
          ]
        },
        {
          "taxType": "employeeNationalInsurance",
          "total": 468.4,
          "aggregation": [
            {
              "percentage": 12,
              "amount": 349.4
            },
            {
              "percentage": 2,
              "amount": 119
            }
          ]
        },
        {
          "taxType": "employerNationalInsurance",
          "total": 1222.31,
          "aggregation": [
            {
              "percentage": 13.8,
              "amount": 1222.31
            }
          ]
        }
      ],
      "totalDeductions": 3621.43,
      "takeHomePay": 5911.9
    },
    {
      "period": "weekly",
      "grossPay": 2200,
      "taxFreePay": 73.25,
      "taxablePay": 2126.75,
      "scottishElement": 72.76,
      "maxTaxAmount":"-1",
      "taxCategories": [
        {
          "taxType": "incomeTax",
          "total": 727.62,
          "aggregation": [
            {
              "percentage": 20,
              "amount": 123.08
            },
            {
              "percentage": 40,
              "amount": 604.55
            },
            {
              "percentage": 45,
              "amount": 0
            }
          ]
        },
        {
          "taxType": "employeeNationalInsurance",
          "total": 108.09,
          "aggregation": [
            {
              "percentage": 12,
              "amount": 80.63
            },
            {
              "percentage": 2,
              "amount": 27.46
            }
          ]
        },
        {
          "taxType": "employerNationalInsurance",
          "total": 282.07,
          "aggregation": [
            {
              "percentage": 13.8,
              "amount": 282.07
            }
          ]
        }
      ],
      "totalDeductions": 835.71,
      "takeHomePay": 1364.29
    }
  ]
}
```

The above response is the result of a calculation using a Scottish Rate tax code, with Annual Tapering.
Please take note of the optional values in the response.

* **Optional Response Values**

    * The `payPerHour` and `hours` values will be available if the service call is made for an hourly calculation.
    * Under the `taxCategories` the `taxType=[employeeNationalInsurance|employerNationalInsurance]` will not be calculated if the `isStatePensionAge=true`.
    * The `scottishElement` will only be calculated if the tax code is a valid scottish tax code.
    * The `maxTaxAmount` is populated if the income tax total exceeds the gross pay amount * max tax rate, and will impact the calculation of totalDeductions by replacing the income tax total amount.