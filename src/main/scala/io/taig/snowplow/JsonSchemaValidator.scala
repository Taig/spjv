package io.taig.snowplow

import cats.effect.Sync
import cats.implicits._
import com.fasterxml.jackson.databind.JsonNode
import io.circe.Printer
import io.circe.parser.parse
import io.taig.snowplow.internal.SchemaHelpers

/**
  * json-schema-validator based implementation of `SchemaValidator`
  */
final class JsonSchemaValidator[F[_]: Sync] extends SchemaValidator[F] {
  override def validate(schema: String, document: String): F[Option[String]] =
    for {
      schema <- parseSchema(schema)
      document <- parseDocument(document)
      report <- SchemaHelpers.validate(schema, document)
    } yield if (report.isSuccess) None else SchemaHelpers.format(report).some

  def parseSchema(value: String): F[JsonNode] =
    SchemaHelpers.parse[F](value)

  def parseDocument(value: String): F[JsonNode] =
    for {
      json <- parse(value).pure[F].rethrow
      normalized = json.pretty(Printer.noSpaces.copy(dropNullValues = true))
      document <- SchemaHelpers.parse[F](normalized)
    } yield document
}

object JsonSchemaValidator {
  def apply[F[_]: Sync]: SchemaValidator[F] = new JsonSchemaValidator[F]
}
