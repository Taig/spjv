package io.taig.snowplow.api

import cats.effect.Sync
import cats.implicits._
import io.taig.snowplow.SchemaStorage
import io.taig.snowplow.data.Id
import io.taig.snowplow.internal.SchemaHelpers
import org.http4s.{HttpRoutes, Response}
import org.http4s.dsl.Http4sDsl

final class ValidateApi[F[_]: Sync](storage: SchemaStorage[F])
    extends Http4sDsl[F] {
  def apply(): HttpRoutes[F] = HttpRoutes.of[F] {
    case payload @ POST -> Root / id =>
      payload.as[String].flatMap(validate(Id(id), _))
  }

  def validate(id: Id, body: String): F[Response[F]] = {
    SchemaHelpers.parse[F](body)
    ???
  }
}

object ValidateApi {
  def apply[F[_]: Sync](storage: SchemaStorage[F]): ValidateApi[F] =
    new ValidateApi[F](storage)
}
