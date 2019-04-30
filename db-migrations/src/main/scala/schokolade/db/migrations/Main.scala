package schokolade.db.migrations

object Main {
  import scala.util.{Failure, Success, Try}
  import schokolade.config.db.DbConfig

  def main(args: Array[String]): Unit = {
    import org.flywaydb.core.api.logging.{Log, LogFactory}
    LogFactory.setLogCreator((c: Class[_]) =>
    new Log {
      def isDebugEnabled: Boolean      = false
      def debug(message: String): Unit = ()
      def info(message: String): Unit  = if (c.getSimpleName != "DatabaseFactory") println(message)
      def warn(message: String): Unit  = println(s"WARN  $message")
      def error(message: String): Unit = println(s"ERROR $message")
      def error(message: String, e: Exception): Unit = {
        println(s"ERROR $message")
        e.printStackTrace()
      }
  })
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
