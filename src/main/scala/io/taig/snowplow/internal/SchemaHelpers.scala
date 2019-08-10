package io.taig.snowplow.internal

import cats.effect.Sync
import cats.implicits._
import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.JsonSchemaFactory

import scala.jdk.CollectionConverters._

object SchemaHelpers {
  def parse[F[_]](value: String)(implicit F: Sync[F]): F[JsonNode] =
    F.delay(JsonLoader.fromString(value))

  def validate[F[_]](schema: JsonNode, document: JsonNode)(
      implicit F: Sync[F]
  ): F[ProcessingReport] =
    F.delay(JsonSchemaFactory.byDefault()).map { factory =>
      factory.getJsonSchema(schema).validate(document)
    }

  def format(report: ProcessingReport): String =
    report.asScala.toList.map(_.getMessage).mkString("\n")
}
