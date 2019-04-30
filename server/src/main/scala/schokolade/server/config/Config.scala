package schokolade.server.config

import scala.util.Try
import schokolade.config.db.DbConfig

case class Config(dbConfig: DbConfig, appConfig: AppConfig)

object Config {
  def env(s: String): Either[String, String] =
    sys.env.get(s).toRight(s"no env var: $s")

  def toInt(s: String): Either[String, Int] =
    Try { s.toInt }.toEither.left.map(e => s"$e")

  def load: Either[String, Config] =
    for {
      dbUrl <- env("DATABASE_URL")
      dbCfg <- DbConfig.postgres(dbUrl)
      port  <- env("PORT").flatMap(toInt)
    } yield Config(dbCfg, AppConfig(port))
}
