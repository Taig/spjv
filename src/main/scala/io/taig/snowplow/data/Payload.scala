package io.taig.snowplow.data

import cats.implicits._
import io.circe.Json
import io.circe.generic.JsonCodec

@JsonCodec(encodeOnly = true)
final case class Payload(
    action: Action,
    id: Id,
    status: Status,
    message: Option[Message]
)

object Payload {
  def success(action: Action, id: Id): Payload =
    Payload(action, id, Status.Success, message = None)

  def success(action: Action, id: Id, message: String): Payload =
    Payload(action, id, Status.Success, Message.Values(List(message)).some)

  def success(action: Action, id: Id, message: Json): Payload =
    Payload(action, id, Status.Success, Message.Document(message).some)

  def error(action: Action, id: Id, throwable: Throwable): Payload =
    error(action, id, Option(throwable.getMessage).getOrElse("Unknown failure"))

  def error(action: Action, id: Id, message: String): Payload =
    error(action, id, List(message))

  def error(action: Action, id: Id, messages: List[String]): Payload =
    Payload(action, id, Status.Error, Message.Values(messages).some)
}
