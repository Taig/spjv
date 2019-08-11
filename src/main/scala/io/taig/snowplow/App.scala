package io.taig.snowplow

import java.io.File

import cats.effect._
import cats.implicits._
import io.taig.snowplow.api.Api
import io.taig.snowplow.internal.{EffectHelpers, FileHelpers}
import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext

case object App extends IOApp {
  override def run(arguments: List[String]): IO[ExitCode] = runF[IO](arguments)

  def runF[F[_]: ConcurrentEffect: Timer: ContextShift](
      arguments: List[String]
  ): F[ExitCode] =
    for {
      target <- target(arguments)
      blocker = Blocker.liftExecutionContext(ExecutionContext.global)
      storage <- FileSchemaStorage[F](blocker, target)
      validator = JsonSchemaValidator[F]
      api = Api(storage, validator).orNotFound
      _ <- server[F](api).use(_ => Async[F].never[Unit])
    } yield ExitCode.Success

  def target[F[_]](arguments: List[String])(implicit F: Sync[F]): F[File] =
    arguments match {
      case Nil => FileHelpers.createTempDirectory[F]("schemas")
      case target :: Nil =>
        F.delay(new File(target)).flatTap(file => F.delay(file.mkdirs()))
      case _ => EffectHelpers.fail[F]("Invalid arguments")
    }

  def server[F[_]: ConcurrentEffect: Timer](
      api: HttpApp[F]
  ): Resource[F, Server[F]] =
    BlazeServerBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(api)
      .resource
}
