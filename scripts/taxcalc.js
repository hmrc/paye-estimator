var fastopt = require('../target/scala-2.11/classes/paye-estimator-fastopt').__ScalaJSExportsNamespace;
var run = fastopt.services.LiveTaxCalculatorService();

var result = run.calculateTax("false", 2015, "1100T", 1000, "weekly", -1);
console.log(' result is ' + result);
