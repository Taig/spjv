package io.taig.snowplow

import io.circe.Encoder

sealed abstract class Action extends Product with Serializable

object Action {
  final case object UploadSchema extends Action
  final case object ValidateDocument extends Action

  implicit val encoder: Encoder[Action] = Encoder[String].contramap {
    case UploadSchema     => "uploadSchema"
    case ValidateDocument => "validateDocument"
  }
}
