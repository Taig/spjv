package io.taig.snowplow.api

import java.nio.file.FileAlreadyExistsException

import cats.effect.Sync
import cats.implicits._
import io.taig.snowplow.SchemaStorage
import io.taig.snowplow.data.{Action, Id, Payload}
import io.taig.snowplow.internal.SchemaHelpers
import org.http4s.{HttpRoutes, Response, Status}
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import io.circe.syntax._

/**
  * GET
  * {{{
  * curl
  * }}}
  *
  * POST
  * {{{
  * curl \
  *   --verbose \
  *   -X POST \
  *   -H "Content-Type: application/json" \
  *   -d '{}' \
  *   http://localhost:8080/schema/id
  * }}}
  */
final class SchemaApi[F[_]: Sync](storage: SchemaStorage[F])
    extends Http4sDsl[F] {
  def apply(): HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / id => ???
    case request @ POST -> Root / id =>
      request.as[String].flatMap(body => upload(Id(id), body))
  }

  def upload(id: Id, body: String): F[Response[F]] = {
    val action = Action.UploadSchema

    (SchemaHelpers.parse[F](body) >> storage.put(id, body))
      .as(Status.Created -> Payload.success(action, id))
      .handleError { throwable =>
        val payload = Payload.error(action, id, message(throwable))
        Status.InternalServerError -> payload
      }
      .map {
        case (status, payload) => Response[F](status).withEntity(payload.asJson)
      }
  }

  def message(throwable: Throwable): String = throwable match {
    case _: FileAlreadyExistsException => "Schema already defined"
    case _                             => throwable.getMessage
  }
}

object SchemaApi {
  def apply[F[_]: Sync](storage: SchemaStorage[F]): SchemaApi[F] =
    new SchemaApi[F](storage)
}
