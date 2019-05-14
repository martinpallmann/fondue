package fondue.config.db

final case class DbConfig(
                           driver: String,
                           url: String,
                           user: String,
                           password: String)

object DbConfig {
  import java.net.URI

  import scala.util.Try

  def postgres(dbUrl: String): Either[String, DbConfig] =
    for {
      cfg   <- extractConfig(dbUrl)
    } yield cfg

  def extractConfig(dbUrl: String): Either[String, DbConfig] =
    Try {
      new URI(dbUrl)
    }.flatMap { uri =>
      Try {
        val splitted = uri.getUserInfo.split(":")
        val user     = splitted(0)
        val pass     = if (splitted.size > 1) splitted(1) else ""
        val port     = if (uri.getPort >= 0) uri.getPort else 5432
        val url      = s"jdbc:postgresql://${uri.getHost}:$port${uri.getPath}"
        DbConfig("org.postgresql.Driver", url, user, pass)
      }
    }
      .toEither
      .left
      .map(e => s"$e")
}
