package io.taig.snowplow

import cats.effect.{Blocker, ContextShift, IO}
import io.taig.snowplow.internal.FileHelpers

import scala.concurrent.ExecutionContext

class FileSchemaStorageTest extends SchemaStorageTest {
  val blocker: Blocker = Blocker.liftExecutionContext(ExecutionContext.global)

  implicit val contextShift: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)

  override val storage: IO[SchemaStorage[IO]] =
    FileHelpers
      .createTempDirectory[IO]
      .flatMap(FileSchemaStorage[IO](blocker, _))
}
