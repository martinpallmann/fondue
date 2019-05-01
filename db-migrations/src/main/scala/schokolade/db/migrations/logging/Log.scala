package schokolade.db.migrations.logging

import org.flywaydb.core.api.logging.{Log => FlywayLog}
import org.slf4j.Logger

private [logging] class Log(log: Logger) extends FlywayLog {
  def isDebugEnabled: Boolean = log.isDebugEnabled
  def debug(msg: String): Unit = log.debug(msg)
  def info(msg: String): Unit  = log.info(msg)
  def warn(msg: String): Unit  = log.warn(msg)
  def error(msg: String): Unit = log.error(msg)
  def error(msg: String, e: Exception): Unit = log.error(msg, e)
}
