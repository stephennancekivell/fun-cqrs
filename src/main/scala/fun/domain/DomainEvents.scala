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