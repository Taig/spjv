package io.taig.snowplow

import cats.effect.Sync
import cats.implicits._
import fs2.Stream
import io.taig.snowplow.data.Id

/**
  * Contract specifying how to store schema definitions
  */
abstract class SchemaStorage[F[_]] {

  /**
    * Store a schema with an `Id`
    *
    * Fails with `SchemaAlreadyExists` if the id is already in use.
    */
  def put(id: Id, schema: String): F[Unit]

  /**
    * Load a schema by an `Id`
    *
    * Fails with `SchemaNotFound` if the id does not exist.
    */
  def get(id: Id): Stream[F, String]

  final def getAll(id: Id)(implicit F: Sync[F]): F[String] =
    get(id).compile.foldMonoid
}
