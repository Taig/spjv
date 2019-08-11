package io.taig.snowplow

/**
  * Contract specifying how to validate a JSON document against a schema
  */
abstract class SchemaValidator[F[_]] {

  /**
    * Validate the `document` against the `schema`
    *
    * @return `None` to indicate success, `Some(message)` to indicate an error
    */
  def validate(schema: String, document: String): F[Option[String]]
}
