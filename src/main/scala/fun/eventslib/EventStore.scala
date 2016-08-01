package fun.eventslib

import java.util.UUID

import org.joda.time.DateTime


import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class Event(
                  id: String = UUID.randomUUID().toString,
                  name: String,
                  created: DateTime = DateTime.now,
                  data: String)

trait EventStore {
  def store(event: Event): Future[Unit]
  def find(from: DateTime): Future[Seq[Event]]
  def all(): Future[Seq[Event]]
}

trait EventHandler[T] {
  val eventName: String
  def handle(event: Event)(implicit eventMachine: EventMachine): Future[Either[String,Unit]] = {
    parseEvent(event).fold[Future[Either[String,Unit]]](
      s => Future.successful(Left(s)),
      e => handle(event, e).map(Right(_))
    )
  }

  def handle(event: Event, t:T)(implicit eventMachine: EventMachine): Future[Unit]
  def parseEvent(data: Event): Either[String,T]
}

trait EventMachine extends LoggingSupport {
  def handlers: Seq[EventHandler[_]]
  def store: EventStore

  def process(ev: Event): Future[Unit] = {
    logger.info(s"processing ${ev.name} $ev")

    for {
      _ <- store.store(ev)
      _ <- execute(ev)
    } yield ()
  }

  def execute(ev: Event): Future[Unit] = {
    logger.info(s"executing ${ev.name} $ev")
    Future.sequence(
      handlers
        .filter(_.eventName == ev.name)
        .map(_.handle(ev)(this)))
      .map(_ => ())
  }

  def replay(from:DateTime): Future[Unit] = {

    for {
      events <- store.find(from)
      _ <- Future.sequence(events.map(execute))
    } yield ()
  }
}

