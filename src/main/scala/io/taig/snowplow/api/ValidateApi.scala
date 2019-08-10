package io.taig.snowplow.api

import cats.effect.Sync
import cats.implicits._
import com.fasterxml.jackson.databind.JsonNode
import io.circe.Printer
import io.circe.syntax._
import io.taig.snowplow.SchemaStorage
import io.taig.snowplow.data.{Action, Id, Payload}
import io.taig.snowplow.internal.SchemaHelpers
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Response, Status}
import io.circe.parser._

/**
  * POST
  * {{{
  * curl \
  *   --verbose \
  *   -X POST \
  *   -H "Content-Type: application/json" \
  *   -d @config.json \
  *   http://localhost:8080/validate/config-schema
  * }}}
  */
final class ValidateApi[F[_]: Sync](storage: SchemaStorage[F])
    extends Http4sDsl[F] {
  def apply(): HttpRoutes[F] = HttpRoutes.of[F] {
    case payload @ POST -> Root / id =>
      payload.as[String].flatMap(validate(Id(id), _))
  }

  def validate(id: Id, body: String): F[Response[F]] = {
    val action = Action.ValidateDocument

    for {
      schema <- storage.getAll(id).flatMap(SchemaHelpers.parse[F])
      document <- document(body)
      report <- SchemaHelpers.validate(schema, document)
    } yield
      if (report.isSuccess) {
        val payload = Payload.success(action, id)
        Response[F](Status.Ok).withEntity(payload.asJson)
      } else {
        val messages = SchemaHelpers.format(report)
        val payload = Payload.error(action, id, messages)
        Response[F](Status.BadRequest).withEntity(payload.asJson)
      }
  }

  def document(body: String): F[JsonNode] =
    for {
      json <- parse(body).pure[F].rethrow
      normalized = json.pretty(Printer.noSpaces.copy(dropNullValues = true))
      document <- SchemaHelpers.parse[F](normalized)
    } yield document
}

object ValidateApi {
  def apply[F[_]: Sync](storage: SchemaStorage[F]): ValidateApi[F] =
    new ValidateApi[F](storage)
}
