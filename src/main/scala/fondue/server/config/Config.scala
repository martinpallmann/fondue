package fondue.server.config

import scala.util.Try

case class Config(dbConfig: DbConfig)

object Config {

  def env(s: String): Either[String, String] = sys.env.get(s).toRight(s"no env var: $s")

  def classOf(a: String): Either[String, String] =
    Try { Class.forName(a) }
      .toEither
      .fold(
        e => Left(s"$e"),
        _ => Right(a)
      )

  def load: Either[String, Config] = for {
    driverName <- env("FONDUE_DB_DRIVER") // "org.postgresql.Driver"
    driver <- classOf(driverName)
    url <- env("FONDUE_DB_URL") // "jdbc:postgresql:fondue"
    user <- env("FONDUE_DB_USER") // "postgres"
    password <- env("FONDUE_DB_PASSWORD") // ""
  } yield Config(DbConfig(
    driver,
    url,
    user,
    password
  ))
}
