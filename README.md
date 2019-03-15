# paye-estimator

[ ![Download](https://api.bintray.com/packages/hmrc/releases/paye-estimator/images/download.svg) ](https://bintray.com/hmrc/releases/paye-estimator/_latestVersion) [![Scala.js](https://www.scala-js.org/assets/badges/scalajs-0.6.14.svg)](https://www.scala-js.org)

A basic PAYE tax estimation calculator JavaScript library

Description
-----------
The ScalaJS library is used to generate the JavaScript, code enhancements must be made to the Scala implementation of the tax-calculator

How to use
----------
To use this code you need to generate the Javascript from the Scala to do this run the following command in the SBT console
 ```
 fullOptJS
 ```

API
---
The following function is exposed by the JavaScript library and can be used to generate a JSON that can be consumed by a service. [See example JSONs](/src/test/resources/data).

| *Supported Methods* | *Description* |
|----|----|
| `uk.gov.hmrc.payeestimator.services.LiveTaxCalculatorService().calculateTax(isStatePensionAge: String, date: Int, taxCode: String, grossPayPence: Int, payPeriod: String, hoursIn: Int)` | Calculates income tax and national insurance contributions  [More...](docs/calculate-tax.md) |


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
    