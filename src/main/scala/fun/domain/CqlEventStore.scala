package fun.domain

import com.datastax.driver.core.Session
import fun.eventslib.{Event, EventStore}
import org.joda.time.DateTime

import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import com.weather.scalacass._
import ScalaCass._
import fun.domain.CassandraImplicits._


class CqlEventStore(session: Session) extends EventStore {
  val ss = new ScalaSession("events")(session)

  def store(event: Event): Future[Unit] = {
    ss.insertAsync("events", event).map(_ => ())
  }

  def find(from: DateTime): Future[Seq[Event]] =
    findIdsInRange(from, DateTime.now)

  def findIdsInRange(from: DateTime, to: DateTime): Future[Seq[Event]] = {
    all().map(_.filter(e => e.created.isAfter(from) && e.created.isBefore(to)))

    //val query = session.prepare("select * from events.events where created > ? and created < ? ").bind(from, to)

    //session.executeAsync(query) .toFuture().map(_.all().asScala.map(_.as[Event]))
  }

  def find(id: String): Future[Option[Event]] = {
    ss.selectOneAsync("select * from events.events where id = ?", id).map(_.map(_.as[Event]))
  }

  def all(): Future[Seq[Event]] = {
    session.executeAsync("select * from events.events").toFuture().map(_.all().asScala.map(_.as[Event]))
  }
}
