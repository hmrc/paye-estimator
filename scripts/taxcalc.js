var fastopt = require('../target/scala-2.11/paye-estimator-fastopt').__ScalaJSExportsNamespace;
var run = fastopt.uk.gov.hmrc.payeestimator.services.LiveTaxCalculatorService();

var result = run.calculateTax("false", 2016, "1100T", 1000, "annual", -1);
console.log(' result is ' + result);
