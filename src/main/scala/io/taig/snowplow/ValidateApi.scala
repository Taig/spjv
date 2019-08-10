package io.taig.snowplow

import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

final class ValidateApi[F[_]: Sync] extends Http4sDsl[F] {
  def apply(): HttpRoutes[F] = HttpRoutes.of[F] {
    case POST -> Root / IntVar(id) => ???
  }
}

object ValidateApi {
  def apply[F[_]: Sync]: ValidateApi[F] = new ValidateApi[F]
}
