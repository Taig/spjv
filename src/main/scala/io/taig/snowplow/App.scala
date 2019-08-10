package io.taig.snowplow

import cats._
import cats.implicits._
import cats.effect.{ExitCode, IO, IOApp}

case object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = runF[IO]

  def runF[F[_]: Applicative]: F[ExitCode] = ExitCode.Success.pure[F]
}
