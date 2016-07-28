package fun.domain

import scala.util.{Success, Try, Failure}

class TryOpts[A](t: Try[A]) {
  def toEither[Throwable, A] = t match {
    case Success(x) => Right(x)
    case Failure(th) => Left(th)
  }
}

object TryOptsImplicits {
  implicit def toTryOpts[A](t: Try[A]): TryOpts[A] = new TryOpts[A](t)
}