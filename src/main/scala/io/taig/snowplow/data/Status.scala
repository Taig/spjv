package io.taig.snowplow.data

import io.circe.Encoder

sealed abstract class Status extends Product with Serializable

object Status {
  final case object Error extends Status
  final case object Success extends Status

  implicit val encoder: Encoder[Status] = Encoder[String].contramap {
    case Error   => "error"
    case Success => "success"
  }
}
