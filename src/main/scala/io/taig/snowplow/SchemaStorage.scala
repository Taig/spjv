package io.taig.snowplow

import io.taig.snowplow.data.Id
import fs2.Stream

abstract class SchemaStorage[F[_]] {
  def put(id: Id, schema: String): F[Unit]

  def get(id: Id): Stream[F, String]
}
