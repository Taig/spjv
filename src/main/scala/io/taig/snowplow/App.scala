package io.taig.snowplow

import cats.effect._
import cats.implicits._
import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder

case object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = runF[IO]

  def runF[F[_]: ConcurrentEffect: Timer]: F[ExitCode] = {
    val schema = SchemaApi[F]
    val validate = ValidateApi[F]
    val api = Api(schema, validate).orNotFound
    server[F](api).use(_ => Async[F].never[Unit]).as(ExitCode.Success)
  }

  def server[F[_]: ConcurrentEffect: Timer](
      api: HttpApp[F]
  ): Resource[F, Server[F]] =
    BlazeServerBuilder[F]
      .bindHttp(8080, "localhost")
      .withHttpApp(api)
      .resource
}
