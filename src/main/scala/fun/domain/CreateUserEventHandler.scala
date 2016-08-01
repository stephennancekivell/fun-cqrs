package fun.domain

import fun.domain.tables.{User, UsersDao}
import fun.eventslib.{Event, EventHandler, EventMachine}
import TryOptsImplicits._

import scala.concurrent.Future
import scala.util.Try

class CreateUserEventHandler(usersDao: UsersDao) extends EventHandler[UserSignupRequestEvent] {
  val eventName = classOf[UserSignupRequest].getName
  def handle(event: Event, t:UserSignupRequestEvent)(implicit eventMachine: EventMachine): Future[Unit] = {
    val newRequest = UserSignupRequestV2(
      email = t.request.email,
      firstname = t.request.firstname,
      surname = t.request.surname,
      city = None,
      address = None
    )

    val newEvent = event.copy(
      name = classOf[UserSignupRequestV2].getName,
      data = Protocols.userSignupV2Format.write(newRequest).toString())

    eventMachine.execute(newEvent)
  }

  def parseEvent(data: Event): Either[String,UserSignupRequestEvent] = {
    import spray.json._

    Try(Protocols.userSignupFormat.read(data.data.parseJson))
      .toEither
      .left.map(_.toString)
      .right.map(UserSignupRequestEvent(_, data))
  }
}


class CreateUserEventV2Handler(usersDao: UsersDao) extends EventHandler[UserSignupRequestV2Event] {
  val eventName = classOf[UserSignupRequestV2].getName
  def handle(event: Event, t:UserSignupRequestV2Event)(implicit eventMachine: EventMachine) : Future[Unit] = {
    usersDao.insert(User(
      email = t.request.email,
      firstname = t.request.firstname,
      surname = t.request.surname,
      city = t.request.city,
      address = t.request.address
    ))
  }

  def parseEvent(data: Event): Either[String,UserSignupRequestV2Event] = {
    import spray.json._

    Try(Protocols.userSignupV2Format.read(data.data.parseJson))
      .toEither
      .left.map(_.toString)
      .right.map(UserSignupRequestV2Event(_, data))
  }
}