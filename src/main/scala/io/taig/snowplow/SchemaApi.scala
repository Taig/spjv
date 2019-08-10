package io.taig.snowplow

import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

final class SchemaApi[F[_]: Sync] extends Http4sDsl[F] {
  def apply(): HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / IntVar(id)  => ???
    case POST -> Root / IntVar(id) => ???
  }
}

object SchemaApi {
  def apply[F[_]: Sync]: SchemaApi[F] = new SchemaApi[F]
}
