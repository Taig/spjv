package io.taig.snowplow

import cats.effect.Sync
import cats.implicits._
import fs2.Stream
import io.taig.snowplow.data.Id

abstract class SchemaStorage[F[_]] {
  def put(id: Id, schema: String): F[Unit]

  def get(id: Id): Stream[F, String]

  final def getAll(id: Id)(implicit F: Sync[F]): F[String] =
    get(id).compile.foldMonoid
}
