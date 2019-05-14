package fondue.db.migrations.logging

import org.flywaydb.core.api.logging.{ LogCreator => FlywayLogCreator }
import org.flywaydb.core.api.logging.{ Log => FlywayLog }
import org.slf4j.LoggerFactory

private [logging] object LogCreator extends FlywayLogCreator {
  def createLogger(c: Class[_]): FlywayLog = new Log(LoggerFactory.getLogger(c))
}
