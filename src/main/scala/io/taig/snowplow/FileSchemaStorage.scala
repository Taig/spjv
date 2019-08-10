package io.taig.snowplow

import java.io.File
import java.nio.file.{Path, StandardOpenOption}

import cats.effect.{Blocker, ContextShift, Sync}
import cats.implicits._
import io.taig.snowplow.data.Id
import io.taig.snowplow.internal.EffectHelpers
import fs2._

final class FileSchemaStorage[F[_]: Sync: ContextShift](
    blocker: Blocker,
    target: File
) extends SchemaStorage[F] {
  override def put(id: Id, schema: String): F[Unit] = {
    val flags = List(StandardOpenOption.CREATE_NEW)
    val write = io.file.writeAll[F](path(id), blocker, flags)

    Stream
      .emit(schema)
      .through(text.utf8Encode)
      .through(write)
      .compile
      .drain
  }

  override def get(id: Id): Stream[F, String] =
    io.file
      .readAll(path(id), blocker, chunkSize = 4096)
      .through(fs2.text.utf8Decode)

  def path(id: Id): Path = new File(target, show"$id.json").toPath
}

object FileSchemaStorage {
  def apply[F[_]: ContextShift](
      blocker: Blocker,
      target: File
  )(implicit F: Sync[F]): F[SchemaStorage[F]] =
    F.ifM[SchemaStorage[F]](F.delay(target.isDirectory))(
      new FileSchemaStorage[F](blocker, target).pure[F].widen,
      EffectHelpers.fail[F]("Target file is not a directory")
    )
}
