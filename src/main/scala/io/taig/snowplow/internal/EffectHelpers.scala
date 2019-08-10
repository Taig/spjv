package io.taig.snowplow.internal

import cats.ApplicativeError
import cats.effect.Sync

object EffectHelpers {
  def fail[F[_]]: ApplyFail[F] = new ApplyFail[F]

  final class ApplyFail[F[_]] {
    def apply[A](
        message: String
    )(implicit F: ApplicativeError[F, Throwable]): F[A] =
      F.raiseError(new RuntimeException(message))
  }

  def timestamp[F[_]](implicit F: Sync[F]): F[Long] =
    F.delay(System.currentTimeMillis())
}
