package io.taig.snowplow

import cats.Applicative
import cats.implicits._
import fs2.Stream
import io.taig.snowplow.data.Id

final class PureSchemaStorage[F[_]: Applicative](schema: String)
    extends SchemaStorage[F] {
  override def put(id: Id, schema: String): F[Unit] = ().pure[F]

  override def get(id: Id): Stream[F, String] = Stream.emit(schema)
}

object PureSchemaStorage {
  def apply[F[_]: Applicative](schema: String): SchemaStorage[F] =
    new PureSchemaStorage[F](schema)
}
