package io.taig.snowplow

import cats.Applicative
import cats.implicits._

final class PureSchemaValidator[F[_]: Applicative](result: Option[String])
    extends SchemaValidator[F] {
  override def validate(schema: String, document: String): F[Option[String]] =
    result.pure[F]
}

object PureSchemaValidator {
  def apply[F[_]: Applicative](result: Option[String]): SchemaValidator[F] =
    new PureSchemaValidator[F](result)

  def success[F[_]: Applicative]: SchemaValidator[F] = PureSchemaValidator(None)

  def error[F[_]: Applicative](message: String): SchemaValidator[F] =
    PureSchemaValidator(message.some)
}
