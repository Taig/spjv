package io.taig.snowplow.internal

import java.io.File
import java.nio.file.Files

import cats.effect.{Blocker, ContextShift, Sync}
import cats.implicits._
import fs2.io
import fs2.text

object FileHelpers {
  def createTempDirectory[F[_]: Sync]: F[File] =
    EffectHelpers
      .timestamp[F]
      .flatMap(timestamp => createTempDirectory[F](String.valueOf(timestamp)))

  def createTempDirectory[F[_]](name: String)(implicit F: Sync[F]): F[File] =
    F.delay(Files.createTempDirectory(name).toFile)

  def resource[F[_]: ContextShift](name: String, blocker: Blocker)(
      implicit F: Sync[F]
  ): F[String] = {
    val stream = F.delay(this.getClass.getClassLoader.getResourceAsStream(name))
    io.readInputStream(stream, chunkSize = 4096, blocker)
      .through(text.utf8Decode)
      .compile
      .foldMonoid
  }
}
