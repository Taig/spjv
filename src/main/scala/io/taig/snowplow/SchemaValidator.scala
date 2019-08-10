package io.taig.snowplow

abstract class SchemaValidator[F[_]] {
  def validate(schema: String, document: String): F[Option[String]]
}
