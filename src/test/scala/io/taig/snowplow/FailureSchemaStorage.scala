package io.taig.snowplow

import cats.ApplicativeError
import cats.implicits._
import io.taig.snowplow.data.Id
import fs2.Stream

final class FailureSchemaStorage[F[_]: ApplicativeError[?[_], Throwable]](
    failure: Throwable
) extends SchemaStorage[F] {
  override def put(id: Id, schema: String): F[Unit] =
    failure.raiseError[F, Unit]

  override def get(id: Id): Stream[F, String] =
    Stream.raiseError[F](failure)
}

object FailureSchemaStorage {
  def apply[F[_]: ApplicativeError[?[_], Throwable]](
      failure: Throwable
  ): SchemaStorage[F] =
    new FailureSchemaStorage[F](failure)
}
