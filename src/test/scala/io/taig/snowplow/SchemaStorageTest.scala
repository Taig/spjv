package io.taig.snowplow

import cats.effect.IO
import io.taig.snowplow.data.Id
import io.taig.snowplow.exception.{SchemaAlreadyExists, SchemaNotFound}
import org.scalatest.FlatSpec

trait SchemaStorageTest { this: FlatSpec =>
  def storage: IO[SchemaStorage[IO]]

  it should "return an existing schema" in {
    val program = for {
      storage <- storage
      id = Id("foo")
      _ <- storage.put(id, "{}")
      schema <- storage.getAll(id)
    } yield schema

    assert(program.unsafeRunSync() == "{}")
  }

  it should "fail when trying to get a non existing schema" in {
    intercept[SchemaNotFound](
      storage.flatMap(_.getAll(Id("404"))).unsafeRunSync()
    )
  }

  it should "fail to put a schema that already exists" in {
    val program = for {
      storage <- storage
      id = Id("foo")
      _ <- storage.put(id, "{}")
      _ <- storage.put(id, "{}")
    } yield ()

    intercept[SchemaAlreadyExists](program.unsafeRunSync())
  }
}
