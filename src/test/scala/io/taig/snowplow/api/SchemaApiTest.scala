package io.taig.snowplow.api

import cats.effect.IO
import io.taig.snowplow.data.Id
import io.taig.snowplow.exception.{SchemaAlreadyExists, SchemaNotFound}
import io.taig.snowplow.{Suite, TestSchemaStorage}
import org.http4s.implicits._
import org.http4s.{Method, Request, Status}

class SchemaApiTest extends Suite {
  "GET" should "return a 200 for an existing schema" in {
    val storage = TestSchemaStorage.pure[IO]("{}")
    val api = SchemaApi(storage)
    val request = Request[IO](Method.GET, uri"/foobar")
    val response = api().run(request).value.unsafeRunSync()

    assert(response.map(_.status).contains(Status.Ok))
  }

  it should "return a 404 for a non-existing schema" in {
    val storage = TestSchemaStorage.error[IO](SchemaNotFound(Id("foobar")))
    val api = SchemaApi(storage)
    val request = Request[IO](Method.GET, uri"/foobar")
    val response = api().run(request).value.unsafeRunSync()

    assert(response.map(_.status).contains(Status.NotFound))
  }

  it should "return a 500 for other failures" in {
    val storage = TestSchemaStorage.error[IO](new RuntimeException)
    val api = SchemaApi(storage)
    val request = Request[IO](Method.GET, uri"/foobar")
    val response = api().run(request).value.unsafeRunSync()

    assert(response.map(_.status).contains(Status.InternalServerError))
  }

  "POST" should "return a 201 after creating a schema" in {
    val storage = TestSchemaStorage.pure[IO]("{}")
    val api = SchemaApi(storage)
    val request = Request[IO](Method.POST, uri"/foobar").withEntity("{}")
    val response = api().run(request).value.unsafeRunSync()

    assert(response.map(_.status).contains(Status.Created))
  }

  it should "return a Conflict when the schema already exists" in {
    val storage = TestSchemaStorage.error[IO](SchemaAlreadyExists(Id("foobar")))
    val api = SchemaApi(storage)
    val request = Request[IO](Method.POST, uri"/foobar").withEntity("{}")
    val response = api().run(request).value.unsafeRunSync()

    assert(response.map(_.status).contains(Status.Conflict))
  }

  it should "return a 500 for other failures" in {
    val storage = TestSchemaStorage.error[IO](new RuntimeException)
    val api = SchemaApi(storage)
    val request = Request[IO](Method.POST, uri"/foobar")
    val response = api().run(request).value.unsafeRunSync()

    assert(response.map(_.status).contains(Status.InternalServerError))
  }
}
