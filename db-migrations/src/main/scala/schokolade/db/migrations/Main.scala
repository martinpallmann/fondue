package schokolade.db.migrations

object Main {
  import schokolade.config.db.DbConfig

  def main(args: Array[String]): Unit = {
    for {
      cfg <- loadDbConfig
    } yield {
      import org.flywaydb.core.Flyway
      val fw = Flyway
        .configure()
        .dataSource(cfg.url, cfg.user, cfg.password)
        .load()
      fw.migrate()
    }
  }

  def env(s: String): Either[String, String] =
    sys.env.get(s).toRight(s"no env var: $s")

  def loadDbConfig: Either[String, DbConfig] =
    for {
      dbUrl <- env("DATABASE_URL")
      dbCfg <- DbConfig.postgres(dbUrl)
    } yield dbCfg

}
