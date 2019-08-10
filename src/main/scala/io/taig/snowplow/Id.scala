package io.taig.snowplow

import io.circe.Encoder

final case class Id(value: String) extends AnyVal

object Id {
  implicit val encoder: Encoder[Id] = Encoder[String].contramap(_.value)
}
