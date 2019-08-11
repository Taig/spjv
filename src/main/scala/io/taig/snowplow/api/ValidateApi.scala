package io.taig.snowplow.api

import cats.effect.Sync
import cats.implicits._
import io.circe.syntax._
import io.taig.snowplow.data.{Action, Id, Payload}
import io.taig.snowplow.{SchemaStorage, SchemaValidator}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Response, Status}

/**
  * POST (valid)
  * {{{
  * curl \
  *   --verbose \
  *   -X POST \
  *   -H "Content-Type: application/json" \
  *   -d @src/test/resources/config.json \
  *   http://localhost:8080/validate/config-schema
  * }}}
  *
  * POST (invalid)
  * {{{
  * curl \
  *   --verbose \
  *   -X POST \
  *   -H "Content-Type: application/json" \
  *   -d @src/test/resources/config-invalid.json \
  *   http://localhost:8080/validate/config-schema
  * }}}
  */
final class ValidateApi[F[_]: Sync](
    storage: SchemaStorage[F],
    validator: SchemaValidator[F]
) extends Http4sDsl[F] {
  def apply(): HttpRoutes[F] = HttpRoutes.of[F] {
    case payload @ POST -> Root / id =>
      payload.as[String].flatMap(validate(Id(id), _))
  }

  def validate(id: Id, body: String): F[Response[F]] = {
    val action = Action.ValidateDocument

    for {
      schema <- storage.getAll(id)
      result <- validator.validate(schema, body)
    } yield result match {
      case Some(error) =>
        val payload = Payload.error(action, id, error)
        Response[F](Status.BadRequest).withEntity(payload.asJson)
      case None =>
        val payload = Payload.success(action, id)
        Response[F](Status.Ok).withEntity(payload.asJson)
    }
  }
}

object ValidateApi {
  def apply[F[_]: Sync](
      storage: SchemaStorage[F],
      validator: SchemaValidator[F]
  ): ValidateApi[F] =
    new ValidateApi[F](storage, validator)
}
