package io.taig.snowplow.data

import cats.implicits._
import io.circe.generic.JsonCodec

@JsonCodec(encodeOnly = true)
final case class Payload(
    action: Action,
    id: Id,
    status: Status,
    message: Option[String]
)

object Payload {
  def success(action: Action, id: Id): Payload =
    Payload(action, id, Status.Success, message = None)

  def error(action: Action, id: Id, message: String): Payload =
    Payload(action, id, Status.Error, message.some)
}
