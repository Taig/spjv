package io.taig.snowplow.exception

import cats.implicits._
import io.taig.snowplow.data.Id

final case class SchemaAlreadyExists(id: Id)
    extends RuntimeException(show"Schema with id $id already exists")
