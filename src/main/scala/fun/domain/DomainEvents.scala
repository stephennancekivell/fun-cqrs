package fun.domain

import fun.eventslib.Event
import spray.json.DefaultJsonProtocol

case class UserSignupRequest(
  email: String,
  firstname: String,
  surname: String)

case class UserSignupRequestV2(
  email: String,
  firstname: String,
  surname: String,
  address: Option[String],
  city: Option[String])

object Protocols {
  import DefaultJsonProtocol._
  val userSignupFormat = jsonFormat3(UserSignupRequest)
  val userSignupV2Format = jsonFormat5(UserSignupRequestV2)
}

case class UserSignupRequestEvent(
   request: UserSignupRequest,
   event: Event
)

case class UserSignupRequestV2Event(
 request: UserSignupRequestV2,
 event: Event
)

trait ToEvent[A]{
  def toEvent(a:A): Event
}

object ToEventImplicits {
  implicit val usg = UserSignupRequestToEvent
  implicit val usgV2 = UserSignupRequestV2ToEvent

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

object UserSignupRequestV2ToEvent extends ToEvent[UserSignupRequestV2] {
  def toEvent(a: UserSignupRequestV2): Event = {
    Event(
      name = a.getClass.getName,
      data = Protocols.userSignupV2Format.write(a).toString()
    )
  }
}