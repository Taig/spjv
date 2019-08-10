package io.taig.snowplow.api

import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.server.Router

object Api {
  def apply[F[_]: Sync](
      schema: SchemaApi[F],
      validate: ValidateApi[F]
  ): HttpRoutes[F] =
    Router("/schema" -> schema(), "/validate" -> validate())
}
