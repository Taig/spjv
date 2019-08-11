package io.taig.snowplow

import cats.effect.IO
import io.taig.snowplow.internal.FileHelpers

class FileSchemaStorageTest extends Suite with SchemaStorageTest {
  override val storage: IO[SchemaStorage[IO]] =
    FileHelpers
      .createTempDirectory[IO]
      .flatMap(FileSchemaStorage[IO](blocker, _))
}
