package fun.domain

import fun.domain.tables.{User, UsersDao}
import fun.eventslib.{Event, EventHandler}
import TryOptsImplicits._

import scala.concurrent.Future
import scala.util.Try

class CreateUserEventHandler(usersDao: UsersDao) extends EventHandler[UserSignupRequestEvent] {
  val eventName = "UserSignupRequest"
  def handle(t:UserSignupRequestEvent): Future[Unit] = {
    usersDao.insert(User(
      email = t.request.email,
      firstname = t.request.firstname,
      surname = t.request.surname
    ))
  }

  def parseEvent(data: Event): Either[String,UserSignupRequestEvent] = {
    import spray.json._

    Try(Protocols.userSignupFormat.read(data.data.parseJson))
      .toEither
      .left.map(_.toString)
      .right.map(UserSignupRequestEvent(_, data))
  }
}
