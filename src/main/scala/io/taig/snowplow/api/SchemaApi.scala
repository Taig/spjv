package io.taig.snowplow.api

import cats.effect.Sync
import cats.implicits._
import io.circe.parser._
import io.circe.syntax._
import io.taig.snowplow.SchemaStorage
import io.taig.snowplow.data.{Action, Id, Payload}
import io.taig.snowplow.exception.{SchemaAlreadyExists, SchemaNotFound}
import io.taig.snowplow.internal.SchemaHelpers
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Response, Status}

/**
  * GET
  * {{{
  * curl \
  *   --verbose \
  *   http://localhost:8080/schema/config-schema
  * }}}
  *
  * POST
  * {{{
  * curl \
  *   --verbose \
  *   -X POST \
  *   -H "Content-Type: application/json" \
  *   -d @config-schema.json \
  *   http://localhost:8080/schema/config-schema
  * }}}
  */
final class SchemaApi[F[_]: Sync](storage: SchemaStorage[F])
    extends Http4sDsl[F] {
  def apply(): HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / id => get(Id(id))
    case request @ POST -> Root / id =>
      request.as[String].flatMap(body => upload(Id(id), body))
  }

  def get(id: Id): F[Response[F]] = {
    val action = Action.GetSchema
    storage
      .get(id)
      .compile
      .foldMonoid
      .map(parse)
      .rethrow
      .map { schema =>
        val payload = Payload.success(action, id, schema)
        Response[F](Status.Ok).withEntity(payload.asJson)
      }
      .handleError {
        case _: SchemaNotFound =>
          val payload = Payload.error(action, id, "Schema does not exist")
          Response[F](Status.NotFound).withEntity(payload.asJson)
        case throwable =>
          val payload = Payload.error(action, id, throwable)
          Response[F](Status.InternalServerError).withEntity(payload.asJson)
      }
  }

  def upload(id: Id, body: String): F[Response[F]] = {
    val action = Action.UploadSchema

    (SchemaHelpers.parse[F](body) >> storage.put(id, body))
      .as {
        val payload = Payload.success(action, id)
        Response[F](Status.Created).withEntity(payload.asJson)
      }
      .handleError {
        case _: SchemaAlreadyExists =>
          val message = "Schema already defined"
          val payload = Payload.error(action, id, message)
          Response[F](Status.Conflict).withEntity(payload.asJson)
        case throwable =>
          val payload = Payload.error(action, id, throwable)
          Response[F](Status.InternalServerError).withEntity(payload.asJson)
      }
  }
}

object SchemaApi {
  def apply[F[_]: Sync](storage: SchemaStorage[F]): SchemaApi[F] =
    new SchemaApi[F](storage)
}
