package io.taig.snowplow.exception

import cats.implicits._
import io.taig.snowplow.data.Id

final case class SchemaNotFound(id: Id)
    extends RuntimeException(show"No schema for id $id")
