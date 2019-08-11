package io.taig.snowplow

import cats.effect.IO
import org.scalatest.FlatSpec

trait SchemaValidatorTest { this: FlatSpec =>
  def validator: IO[SchemaValidator[IO]]

  def resource(name: String): IO[String]

  it should "return None when valid" in {
    val result = for {
      validator <- validator
      schema <- resource("config-schema.json")
      config <- resource("config.json")
      result <- validator.validate(schema, config)
    } yield result

    assert(result.unsafeRunSync().isEmpty)
  }

  it should "return Some when invalid" in {
    val result = for {
      validator <- validator
      schema <- resource("config-schema.json")
      config <- resource("config-invalid.json")
      result <- validator.validate(schema, config)
    } yield result

    assert(result.unsafeRunSync().nonEmpty)
  }

  it should "ignore null values" in {
    val result = for {
      validator <- validator
      schema <- resource("config-schema.json")
      config <- resource("config.json")
      configNulls <- resource("config-nulls.json")
      result <- validator.validate(schema, config)
      resultNulls <- validator.validate(schema, configNulls)
    } yield result == resultNulls

    assert(result.unsafeRunSync())
  }
}
