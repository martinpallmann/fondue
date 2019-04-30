package fondue.server.db

import fondue.server.config.DbConfig
import org.flywaydb.core.Flyway
import scalaz.zio.{Task, ZIO}

object Migrations {
  def initDb(cfg: DbConfig): Task[Unit] =
    ZIO.effect {
      val fw = Flyway
        .configure()
        .dataSource(cfg.url, cfg.user, cfg.password)
        .load()
      fw.migrate()
    }.unit
}
