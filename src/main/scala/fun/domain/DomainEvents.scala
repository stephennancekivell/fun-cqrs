package fun.domain

import fun.eventslib.Event
import spray.json.DefaultJsonProtocol

case class UserSignupRequest(
  email: String,
  firstname: String,
  surname: String)

object Protocols {
  import DefaultJsonProtocol._
  val userSignupFormat = jsonFormat3(UserSignupRequest)
}

case class UserSignupRequestEvent(
   request: UserSignupRequest,
   event: Event
)

trait ToEvent[A]{
  def toEvent(a:A): Event
}

object ToEventImplicits {
  implicit val usg = UserSignupRequestToEvent

  implicit class ToEventX[A](a:A)(implicit toEventI: ToEvent[A]) {
    def toEvent(): Event = toEventI.toEvent(a)
  }
}

object UserSignupRequestToEvent extends ToEvent[UserSignupRequest] {
  def toEvent(a: UserSignupRequest): Event = {
    Event(
      name = a.getClass.getName,
      data = Protocols.userSignupFormat.write(a).toString()
    )
  }
}