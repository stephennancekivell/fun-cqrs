package fun

import org.scalatest.Assertions
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future

trait FutureSpecUtils extends ScalaFutures with Assertions {
  def assertResultFuture(expected: Any)(actualFuture: Future[Any]) = {
    whenReady(actualFuture)(assertResult(expected))
  }
}
