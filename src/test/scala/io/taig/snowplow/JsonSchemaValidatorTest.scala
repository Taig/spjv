package io.taig.snowplow
import cats.effect.IO
import io.taig.snowplow.internal.FileHelpers

class JsonSchemaValidatorTest extends Suite with SchemaValidatorTest {
  override val validator: IO[SchemaValidator[IO]] =
    IO.pure(JsonSchemaValidator[IO])

  override def resource(name: String): IO[String] =
    FileHelpers.resource[IO](name, blocker)
}
