package schokolade.server

import cats.effect._
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import config.{Config, DbConfig}
import db.Migrations._
import org.http4s.implicits._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS
import scalaz.zio._
import scalaz.zio.blocking.Blocking
import scalaz.zio.clock.Clock
import scalaz.zio.console._
import scalaz.zio.scheduler.Scheduler
import scalaz.zio.interop.catz._

import scala.concurrent.ExecutionContext

object Main extends App {

  type AppEnvironment = Clock with Console with Blocking
  type AppTask[A]     = TaskR[AppEnvironment, A]

  val dsl: Http4sDsl[AppTask] = Http4sDsl[AppTask]
  import dsl._

  def service: HttpRoutes[AppTask] = HttpRoutes.of[AppTask] {
    case GET -> Root => Ok("ok")
  }

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] =
    (for {
      cfg        <- ZIO.fromEither(Config.load)
      _          <- initDb(cfg.dbConfig)
      blockingEC <- blockingExecutor
      transactorR = mkTransactor(
        cfg.dbConfig,
        Platform.executor.asEC,
        blockingEC
      )
      httpApp = Router[AppTask]("/" -> service).orNotFound
      server = ZIO.runtime[AppEnvironment].flatMap { implicit rts =>
        BlazeServerBuilder[AppTask]
          .bindHttp(cfg.appConfig.port, "0.0.0.0")
          .withHttpApp(CORS(httpApp))
          .serve
          .compile[AppTask, AppTask, ExitCode]
          .drain
      }
      program <- transactorR.use { transactor =>
        server.provideSome[Environment] { base =>
          new Clock with Console with Blocking {
//            override protected def xa: doobie.Transactor[Task] = transactor
            override val scheduler: Scheduler.Service[Any] = base.scheduler
            override val console: Console.Service[Any]     = base.console
            override val clock: Clock.Service[Any]         = base.clock
            override val blocking: Blocking.Service[Any]   = base.blocking
          }
        }
      }
    } yield program)
      .foldM(
        {
          case e: Throwable =>
            for {
              _ <- putStrLn(s"Execution failed with: $e")
              _ = e.printStackTrace()
              s <- ZIO.succeed(1)
            } yield s
          case e =>
            putStrLn(s"Execution failed with: $e") *> ZIO.succeed(1)
        },
        _ => ZIO.succeed(0)
      )

  private def blockingExecutor: ZIO[Blocking, Nothing, ExecutionContext] =
    ZIO
      .environment[Blocking]
      .flatMap(_.blocking.blockingExecutor)
      .map(_.asEC)

  def mkTransactor(
      cfg: DbConfig,
      connectEC: ExecutionContext,
      transactEC: ExecutionContext
  ): Managed[Throwable, Transactor[Task]] = {
    val xa = HikariTransactor.newHikariTransactor[Task](
      cfg.driver,
      cfg.url,
      cfg.user,
      cfg.password,
      connectEC,
      transactEC
    )

    val res = xa.allocated.map {
      case (transactor, cleanupM) =>
        Reservation(ZIO.succeed(transactor), cleanupM.orDie)
    }.uninterruptible

    Managed(res)
  }

}
