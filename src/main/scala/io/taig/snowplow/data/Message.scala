package io.taig.snowplow.data

import cats.implicits._
import io.circe.generic.JsonCodec

@JsonCodec(encodeOnly = true)
final case class Message(
    action: Action,
    id: Id,
    status: Status,
    message: Option[String]
)

object Message {
  def success(action: Action, id: Id): Message =
    Message(action, id, Status.Success, message = None)

  def error(action: Action, id: Id, message: String): Message =
    Message(action, id, Status.Error, message.some)
}
