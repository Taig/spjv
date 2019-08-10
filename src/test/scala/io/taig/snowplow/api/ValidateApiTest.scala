package io.taig.snowplow.api

import cats.effect.IO
import io.taig.snowplow.{PureSchemaStorage, PureSchemaValidator}
import org.http4s.implicits._
import org.http4s.{Method, Request, Status}
import org.scalatest.FlatSpec

class ValidateApiTest extends FlatSpec {
  "POST" should "return a 200 for successful validation" in {
    val storage = PureSchemaStorage[IO]("{}")
    val validator = PureSchemaValidator.success[IO]
    val api = ValidateApi(storage, validator)
    val request = Request[IO](Method.POST, uri"/foobar").withEntity("{}")
    val program = api().run(request).value

    assert(program.unsafeRunSync().map(_.status).contains(Status.Ok))
  }

  it should "return a 400 for failed validation" in {
    val storage = PureSchemaStorage[IO]("{}")
    val validator = PureSchemaValidator.error[IO](":(")
    val api = ValidateApi(storage, validator)
    val request = Request[IO](Method.POST, uri"/foobar").withEntity("{}")
    val program = api().run(request).value

    assert(program.unsafeRunSync().map(_.status).contains(Status.BadRequest))
  }
}
