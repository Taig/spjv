package io.taig.snowplow

import cats.effect._
import cats.implicits._
import io.taig.snowplow.api.{Api, SchemaApi, ValidateApi}
import io.taig.snowplow.internal.FileHelpers
import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext

case object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = runF[IO]

  def runF[F[_]: ConcurrentEffect: Timer: ContextShift]: F[ExitCode] = {
    for {
      target <- FileHelpers.createTempDirectory[F]("schemas")
      blocker = Blocker.liftExecutionContext(ExecutionContext.global)
      storage <- FileSchemaStorage[F](blocker, target)
      schema = SchemaApi[F](storage)
      validate = ValidateApi[F]
      api = Api(schema, validate).orNotFound
      _ <- server[F](api).use(_ => Async[F].never[Unit])
    } yield ExitCode.Success
  }

  def server[F[_]: ConcurrentEffect: Timer](
      api: HttpApp[F]
  ): Resource[F, Server[F]] =
    BlazeServerBuilder[F]
      .bindHttp(8080, "localhost")
      .withHttpApp(api)
      .resource
}
