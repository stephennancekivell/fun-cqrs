package fun.domain.tables

import com.datastax.driver.core.Session
import com.weather.scalacass.ScalaSession

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConverters._
import com.weather.scalacass._
import ScalaCass._
import fun.domain.CassandraImplicits._
import fun.eventslib.LoggingSupport

case class User(
  email: String,
  firstname: String,
  surname: String,
  address: Option[String],
  city: Option[String])

class UsersDao(session: Session) extends LoggingSupport {

  val ss = new ScalaSession("events")(session)

  def insert(user: User): Future[Unit] = {
    val f1 = ss.insertAsync("users", user).map(_ => ())

    val f2 = user.city.map { city =>
      insertByCity(UserCity(user.email, city))
    }.getOrElse(Future.successful(()))

    Future.sequence(Seq(f1,f2)).map(_ => ())
  }

  def findByEmail(email: String): Future[Option[User]] = {
    val q = session.prepare("select * from events.users where email = ?").bind(email)
    session.executeAsync(q).toFuture().map(_.all().asScala.headOption.map(_.as[User]))
  }

  def all(): Future[Seq[User]] = {
    val q = "select * from events.users"
    session.executeAsync(q).toFuture().map(_.all().asScala.map(_.as[User]))
  }

  case class UserCity(email: String, city: String)

  def insertByCity(u: UserCity): Future[Unit] = {
    ss.insertAsync("users_by_city", u).map(_ => ())
  }

  def findByCity(city: String):Future[Seq[User]] = {
    val q = session.prepare("select email from events.users_by_city where city = ?").bind(city)
    val found = session.executeAsync(q)
      .toFuture().map(_.asScala.map(_.getString("email")))
      .map(_.map(findByEmail).toSeq)

    found.flatMap { fx =>
      Future.sequence(fx).map(_.flatten)
    }
  }
}
