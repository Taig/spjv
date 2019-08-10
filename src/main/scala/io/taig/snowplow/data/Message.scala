package io.taig.snowplow.data

import io.circe.{Encoder, Json}
import io.circe.syntax._

sealed abstract class Message extends Product with Serializable

object Message {
  final case class Values(values: List[String]) extends Message
  final case class Document(value: Json) extends Message

  implicit val encoder: Encoder[Message] = Encoder.instance {
    case Values(values)  => values.asJson
    case Document(value) => value
  }
}
