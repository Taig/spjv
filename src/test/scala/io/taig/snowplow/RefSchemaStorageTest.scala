package io.taig.snowplow
import cats.effect.IO

class RefSchemaStorageTest extends SchemaStorageTest {
  override def storage: IO[SchemaStorage[IO]] =
    RefSchemaStorage[IO]
}
