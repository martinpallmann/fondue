import Dependencies.Compile._
import sbt._

object Dependencies {

  object Versions {
    val circe = "0.11.1"
    val doobie = "0.7.0-M3"
    val flyway = "5.2.4"
    val http4s = "0.20.0"
    val jgrapht = "1.3.0"
    val logback = "1.2.3"
    val minitest = "2.3.2"
    val mouse = "0.20"
    val quicklens = "1.4.12"
    val scalatags = "0.6.8"
    val scalaUri = "1.4.5"
    val tapir = "0.7.1"
    val zio = "1.0-RC4"
  }

  object Compile {
    private[Dependencies] val circe = "io.circe" ** ("circe-core", "circe-generic") * Versions.circe
    private val doobieGroup = "org.tpolecat"
    private[Dependencies] val doobieCore = doobieGroup ** "doobie-core" * Versions.doobie
    private[Dependencies] val doobieH2 = doobieGroup ** "doobie-h2" * Versions.doobie
    private[Dependencies] val doobieHikari = "org.tpolecat" ** "doobie-hikari" * Versions.doobie
    private[Dependencies] val doobie = doobieCore ++ doobieHikari
    private[Dependencies] val flyway = "org.flywaydb" * "flyway-core" * Versions.flyway
    private[Dependencies] val http4s = "org.http4s" ** ("http4s-server", "http4s-blaze-server", "http4s-dsl", "http4s-circe") * Versions.http4s
    private[Dependencies] val jgrapht = "org.jgrapht" * "jgrapht-core" * Versions.jgrapht
    private[Dependencies] val logback = "ch.qos.logback" * "logback-classic" * Versions.logback
    private[Dependencies] val mouse = "org.typelevel" ** "mouse" * Versions.mouse
    private[Dependencies] val quicklens = "com.softwaremill.quicklens" ** "quicklens" * Versions.quicklens
    private[Dependencies] val scalatags = "com.lihaoyi" ** "scalatags" * Versions.scalatags
    private[Dependencies] val scalaUri = "io.lemonlabs" ** "scala-uri" * Versions.scalaUri
    private val tapirGroup = "com.softwaremill.tapir"
    private[Dependencies] val tapirCore = tapirGroup ** "tapir-core" * Versions.tapir
    private[Dependencies] val tapirCirce = tapirGroup ** "tapir-json-circe" * Versions.tapir
    private[Dependencies] val tapirHttp4s = tapirGroup ** "tapir-http4s-server" * Versions.tapir
    private[Dependencies] val tapirOpenApi = tapirGroup ** ("tapir-openapi-docs", "tapir-openapi-circe-yaml") * Versions.tapir
    private[Dependencies] val tapir = tapirCore ++ tapirCirce ++ tapirHttp4s ++ tapirOpenApi
    private[Dependencies] val zio = "org.scalaz" ** ("scalaz-zio", "scalaz-zio-interop-cats") * Versions.zio

    val `api-descriptions`: List[sbt.ModuleID] = List(tapirCore).sbt
    val server: List[sbt.ModuleID] = List(doobie, flyway, http4s, logback, zio).sbt
  }

  object Test {
    private val minitest = "io.monix" ** "minitest" * Versions.minitest
    private val minitestLaws = "io.monix" ** "minitest-laws" * Versions.minitest

    val `api-descriptions`: List[sbt.ModuleID] = List(minitest).sbt
    val server: List[sbt.ModuleID] = List(minitest).sbt
  }

  object Runtime {
    private val postgres = "org.tpolecat" ** "doobie-postgres" * Versions.doobie

    val server: List[sbt.ModuleID] = List(postgres).sbt
  }

  val `api-descriptions`: List[ModuleID] =
    Compile.`api-descriptions` ++ Test.`api-descriptions`.map(_ % sbt.Test)

  val server: List[sbt.ModuleID] =
    Compile.server ++ Test.server.map(_ % sbt.Test) ++ Runtime.server.map(_ % sbt.Runtime)

  implicit class StringOps(g: String) {
    def *(as: String*): Artifacts = Artifacts(g, as.toList, isScala = false)
    def **(as: String*): Artifacts = Artifacts(g, as.toList, isScala = true)
  }

  implicit class ModulesOps(ms: List[Modules]) {
    def sbt: List[ModuleID] =
      ms.flatMap(m => m.as.map {
        case (a, v) if m.isScala => m.g %% a % v
        case (a, v) => m.g % a % v
      })
  }

  final case class Artifacts(g: String, as: List[String], isScala: Boolean) {
    def *(v: String): Modules = Modules(g, as.map(_ -> v), isScala)
  }

  final case class Modules(g: String, as: List[(String, String)], isScala: Boolean) {
    def ++ (m: Modules): Modules = Modules(g, m.as ++ as, isScala)
  }
}
