package io.taig.snowplow.data

import cats.Show
import io.circe.Encoder

final case class Id(value: String) extends AnyVal

object Id {
  implicit val encoder: Encoder[Id] = Encoder[String].contramap(_.value)

  implicit val show: Show[Id] = _.value
}
