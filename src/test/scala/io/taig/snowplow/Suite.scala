package io.taig.snowplow

import cats.effect.{Blocker, ContextShift, IO}
import org.scalatest.FlatSpec

import scala.concurrent.ExecutionContext

abstract class Suite extends FlatSpec {
  val blocker: Blocker = Blocker.liftExecutionContext(ExecutionContext.global)

  implicit val contextShift: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)
}
