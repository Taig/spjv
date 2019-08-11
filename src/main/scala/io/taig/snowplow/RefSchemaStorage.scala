package io.taig.snowplow

import cats.MonadError
import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.implicits._
import fs2.Stream
import io.taig.snowplow.data.Id
import io.taig.snowplow.exception.{SchemaAlreadyExists, SchemaNotFound}

/**
  * In-memory implementation of `SchemaStorage`
  */
final class RefSchemaStorage[F[_]: MonadError[?[_], Throwable]](
    store: Ref[F, Map[Id, String]]
) extends SchemaStorage[F] {
  override def put(id: Id, schema: String): F[Unit] = {
    store
      .modify { store =>
        if (store.contains(id)) (store, false)
        else (store.updated(id, schema), true)
      }
      .flatMap {
        case true  => ().pure[F]
        case false => SchemaAlreadyExists(id).raiseError[F, Unit]
      }
  }

  override def get(id: Id): Stream[F, String] =
    Stream.eval(store.get).flatMap { store =>
      store.get(id) match {
        case Some(value) => Stream.emit(value)
        case None        => Stream.raiseError[F](SchemaNotFound(id))
      }
    }
}

object RefSchemaStorage {
  def apply[F[_]: Sync]: F[SchemaStorage[F]] =
    Ref[F].of(Map.empty[Id, String]).map(new RefSchemaStorage[F](_))
}
