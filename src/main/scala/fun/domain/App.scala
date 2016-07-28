package fun.domain

import com.datastax.driver.core.{Cluster, Session}
import fun.domain.tables.UsersDao
import fun.eventslib.{Event, EventMachine, EventStore}
import org.joda.time.DateTime

import scala.concurrent.Future

object App extends scala.App {
  //MyEventMachine.replay()

}

object MyEventMachine extends EventMachine {
  def handlers = Seq(
    Core.createUserEventHandler
  )

  val store = new CqlEventStore(Core.session)
}

object VoidEventStore extends EventStore {
  def store(event: Event) = Future.successful(())
  def find(from: DateTime) = Future.successful(Nil)
  def all() = Future.successful(Nil)
}

object Core {
  val session = createSession()

  def createSession(): Session = {
    val cluster = Cluster.builder()
      .withCredentials("cassandra", "cassandra")
      .addContactPoint("127.0.0.1")
      .withPort(9042)
      .build()
    cluster.connect()
  }

  val userDao = new UsersDao(session)
  val createUserEventHandler = new CreateUserEventHandler(userDao)
}