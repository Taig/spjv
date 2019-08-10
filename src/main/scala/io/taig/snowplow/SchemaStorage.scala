package io.taig.snowplow

abstract class SchemaStorage[F[_]] {
  def put(id: Id, schema: String): F[Unit]

  def get(id: Id): F[Option[String]]
}
