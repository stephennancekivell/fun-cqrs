package fun.domain.tables

import com.datastax.driver.core.Session
import com.weather.scalacass.ScalaSession

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import scala.collection.JavaConverters._

import com.weather.scalacass._
import ScalaCass._
import fun.domain.CassandraImplicits._

case class User(email: String, firstname: String, surname: String)

class UsersDao(session: Session) {

  val ss = new ScalaSession("events")(session)

  def insert(user: User): Future[Unit] = {
    ss.insertAsync("users", user).map(_ => ())
  }

  def findByEmail(email: String): Future[Option[User]] = {
    val q = session.prepare("select * from events.users where email = ?").bind(email)
    session.executeAsync(q).toFuture().map(_.all().asScala.headOption.map(_.as[User]))
  }

  def all(): Future[Seq[User]] = {
    val q = session.prepare("select * from events.users").bind()
    session.executeAsync(q).toFuture().map(_.all().asScala.map(_.as[User]))
  }
}
