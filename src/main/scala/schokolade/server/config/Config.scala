package schokolade.server.config

import java.net.URI
import scala.util.Try

case class Config(dbConfig: DbConfig, appConfig: AppConfig)

object Config {

  def env(s: String): Either[String, String] = sys.env.get(s).toRight(s"no env var: $s")

  def toInt(s: String): Either[String, Int] =
    Try { s.toInt }.toEither.left.map(e => s"$e")

  def split(dbUrl: String): Either[String, (String, String, String)] = Try {
    new URI(dbUrl)
  }.flatMap { uri =>
    Try {
      val splitted = uri.getUserInfo.split(":")
      val user = splitted(0)
      val pass = if (splitted.size > 1) splitted(1) else ""
      val port = if (uri.getPort >= 0) uri.getPort else 5432
      val url = s"jdbc:postgresql://${uri.getHost}:$port${uri.getPath}"
      (user, pass, url)
    }
  }.toEither.left.map(e => s"$e")

  def load: Either[String, Config] = for {
    dbUrl <- env("DATABASE_URL")
    upu <- split(dbUrl)
    port <- env("PORT").flatMap(toInt)
  } yield upu match { case (user, pass, url) =>
    Config(
      DbConfig("org.postgresql.Driver", url, user, pass),
      AppConfig(port)
    )
  }
}
