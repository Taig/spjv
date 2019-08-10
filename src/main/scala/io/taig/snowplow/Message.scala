package io.taig.snowplow

import io.circe.generic.JsonCodec

@JsonCodec(encodeOnly = true)
final case class Message(action: Action, id: Id, status: Status, message: Option[String])
