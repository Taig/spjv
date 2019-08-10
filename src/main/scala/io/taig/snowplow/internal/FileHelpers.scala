package io.taig.snowplow.internal

import java.io.File
import java.nio.file.Files

import cats.effect.Sync
import cats.implicits._

object FileHelpers {
  def createTempDirectory[F[_]: Sync]: F[File] =
    EffectHelpers
      .timestamp[F]
      .flatMap(timestamp => createTempDirectory[F](String.valueOf(timestamp)))

  def createTempDirectory[F[_]](name: String)(implicit F: Sync[F]): F[File] =
    F.delay(Files.createTempDirectory(name).toFile)
}
