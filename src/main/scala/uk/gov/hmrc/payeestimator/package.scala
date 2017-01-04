package uk.gov.hmrc

import slogging._

package object payeestimator {

  LoggerConfig.factory = ConsoleLoggerFactory()

  LoggerConfig.level = LogLevel.DEBUG
}

