
# paye-estimator

[![Build Status](https://travis-ci.org/hmrc/paye-estimator.svg?branch=master)](https://travis-ci.org/hmrc/paye-estimator) [ ![Download](https://api.bintray.com/packages/hmrc/releases/paye-estimator/images/download.svg) ](https://bintray.com/hmrc/releases/paye-estimator/_latestVersion) [![Scala.js](https://www.scala-js.org/assets/badges/scalajs-0.6.15.svg)](https://www.scala-js.org)

A basic PAYE tax estimation calculator JavaScript library

Description
-----------

The scalaJS library is used to generate the JavaScript, code enhancements must be made to the Scala implementation of the tax-calculator, to regenerate the Javascript simply use the sbt command ```fullOptJS```.

Requirements
------------

The following function is exposed by the JavaScript library.

API
---

| *Supported Methods* | *Description* |
|----|----|
| ```uk.gov.hmrc.payeestimator.services.LiveTaxCalculatorService().calculateTax(isStatePensionAge: String, date: Int, taxCode: String, grossPayPence: Int, payPeriod: String, hoursIn: Int)``` | Calculates income tax and national insurance contributions  [More...](docs/calculate-tax.md) |
| ```uk.gov.hmrc.payeestimator.services.LiveGovernmentSpendingReceiptingService().getGovernmentReceiptingData()``` | Returns government receipting data  [More...](docs/receipting-data.md) |
| ```uk.gov.hmrc.payeestimator.services.LiveGovernmentSpendingReceiptingService().getGovernmentSpendingData()``` | Returns government spending data breakdown  [More...](docs/spending-data.md) |


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
    