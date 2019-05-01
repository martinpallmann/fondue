package schokolade.db.migrations.logging

import org.flywaydb.core.api.logging.LogFactory

object Logging {
  def configure(): Unit = LogFactory.setLogCreator(LogCreator)
}
