package io.taig.snowplow.internal

import cats.effect.Sync
import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader

object SchemaHelpers {
  def parse[F[_]](value: String)(implicit F: Sync[F]): F[JsonNode] =
    F.delay(JsonLoader.fromString(value))
}
