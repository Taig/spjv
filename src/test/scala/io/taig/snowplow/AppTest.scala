package io.taig.snowplow

import cats.effect.IO

class AppTest extends Suite {
  "target" should "fail when more than one arguments are received" in {
    intercept[Throwable](App.target[IO](List("foo", "bar")).unsafeRunSync())
  }

  it should "fall back to a temporary directory" in {
    val file = App.target[IO](List.empty).unsafeRunSync()
    assert(file.isDirectory)
  }
}
