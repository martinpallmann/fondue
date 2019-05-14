package fondue.db.migrations

object Main {
  import scala.util.{Failure, Success, Try}
  import fondue.config.db.DbConfig
  import fondue.db.migrations.logging.Logging

  def main(args: Array[String]): Unit = {
    Logging.configure()
    loadDbConfig.flatMap(migrate).get
  }

  def toTry[A](e: Either[String, A]): Try[A] =
    e.fold(s => Failure(new RuntimeException(s)), Success(_))

  def env(s: String): Try[String] =
    Try { sys.env(s) }

  def loadDbConfig: Try[DbConfig] =
    for {
      dbUrl <- env("DATABASE_URL")
      dbCfg <- toTry(DbConfig.postgres(dbUrl))
    } yield dbCfg

  def migrate(cfg: DbConfig): Try[Unit] =
    Try {
      import org.flywaydb.core.Flyway
      val fw = Flyway
        .configure()
        .dataSource(cfg.url, cfg.user, cfg.password)
        .load()
      fw.migrate()
      ()
    }
}
