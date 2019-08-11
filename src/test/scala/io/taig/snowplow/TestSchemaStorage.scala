package io.taig.snowplow

import cats.{Applicative, ApplicativeError}
import cats.implicits._
import fs2.Stream
import io.taig.snowplow.data.Id

final class TestSchemaStorage[F[_]: Applicative](
    put: F[Unit],
    get: Stream[F, String]
) extends SchemaStorage[F] {
  override def put(id: Id, schema: String): F[Unit] = put

  override def get(id: Id): Stream[F, String] = get
}

object TestSchemaStorage {
  def apply[F[_]: Applicative](
      put: F[Unit],
      get: Stream[F, String]
  ): SchemaStorage[F] = new TestSchemaStorage[F](put, get)

  def pure[F[_]: Applicative](schema: String): SchemaStorage[F] =
    TestSchemaStorage[F](().pure[F], Stream.emit(schema))

  def error[F[_]: ApplicativeError[?[_], Throwable]](
      throwable: Throwable
  ): SchemaStorage[F] =
    TestSchemaStorage[F](
      throwable.raiseError[F, Unit],
      Stream.raiseError[F](throwable)
    )
}
