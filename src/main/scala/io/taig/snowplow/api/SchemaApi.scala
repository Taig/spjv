package io.taig.snowplow.api

import cats.effect.Sync
import cats.implicits._
import io.taig.snowplow.SchemaStorage
import io.taig.snowplow.data.{Action, Id, Message}
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
      for {
        body <- request.as[String]
        node <- SchemaHelpers.parse[F](body)
        _ <- storage.put(Id(id), body)
      } yield {
        val message = Message.success(Action.UploadSchema, Id(id))
        Response(Status.Created).withEntity(message.asJson)
      }
  }
}

object SchemaApi {
  def apply[F[_]: Sync](storage: SchemaStorage[F]): SchemaApi[F] =
    new SchemaApi[F](storage)
}
