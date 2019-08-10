package io.taig.snowplow.api

import cats.effect.IO
import io.taig.snowplow.{FailureSchemaStorage, RefSchemaStorage}
import io.taig.snowplow.data.Id
import org.http4s.{Method, Request, Status}
import org.scalatest.FlatSpec
import org.http4s.implicits._

class SchemaApiTest extends FlatSpec {
  "GET" should "return a 200 for an existing schema" in {
    val program = for {
      storage <- RefSchemaStorage[IO]
      _ <- storage.put(Id("foobar"), "{}")
      api = SchemaApi(storage)
      request = Request[IO](Method.GET, uri"/foobar")
      response <- api().run(request).value
    } yield response

    assert(program.unsafeRunSync().map(_.status).contains(Status.Ok))
  }

  it should "return a 404 for a non-existing schema" in {
    val program = for {
      storage <- RefSchemaStorage[IO]
      api = SchemaApi(storage)
      request = Request[IO](Method.GET, uri"/foobar")
      response <- api().run(request).value
    } yield response

    assert(program.unsafeRunSync().map(_.status).contains(Status.NotFound))
  }

  it should "return a 500 for other failures" in {
    val storage = FailureSchemaStorage[IO](new RuntimeException)
    val api = SchemaApi(storage)
    val request = Request[IO](Method.GET, uri"/foobar")
    val program = api().run(request).value

    assert(
      program.unsafeRunSync().map(_.status).contains(Status.InternalServerError)
    )
  }

  "POST" should "return a 201 after creating a schema" in {
    val program = for {
      storage <- RefSchemaStorage[IO]
      api = SchemaApi(storage)
      request = Request[IO](Method.POST, uri"/foobar").withEntity("{}")
      response <- api().run(request).value
    } yield response

    assert(program.unsafeRunSync().map(_.status).contains(Status.Created))
  }

  it should "return a Conflict when the schema already exists" in {
    val program = for {
      storage <- RefSchemaStorage[IO]
      _ <- storage.put(Id("foobar"), "{}")
      api = SchemaApi(storage)
      request = Request[IO](Method.POST, uri"/foobar").withEntity("{}")
      response <- api().run(request).value
    } yield response

    assert(program.unsafeRunSync().map(_.status).contains(Status.Conflict))
  }

  it should "return a 500 for other failures" in {
    val storage = FailureSchemaStorage[IO](new RuntimeException)
    val api = SchemaApi(storage)
    val request = Request[IO](Method.POST, uri"/foobar")
    val program = api().run(request).value

    assert(
      program.unsafeRunSync().map(_.status).contains(Status.InternalServerError)
    )
  }
}
