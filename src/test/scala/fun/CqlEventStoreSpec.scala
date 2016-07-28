package fun

import fun.domain.CqlEventStore
import fun.eventslib.Event
import org.joda.time.DateTime
import org.scalatest.FreeSpec
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CqlEventStoreSpec extends FreeSpec with CassandraSpec with ScalaFutures with FutureSpecUtils {
  "CqlEventStore" - {
    "save and find" in new Fixtures {
      val found = for {
        _ <- store.store(event1)
        e1 <- store.find(event1.id)
      } yield e1

      assertResultFuture(Some(event1))(found)
    }

    "find all" in new Fixtures {
      val found = for {
        _ <- store.store(event1)
        e <- store.all()
      } yield e

      assertResultFuture(Seq(event1))(found)
    }

    "find all with nothing" in new Fixtures {
      val found = store.all()
      assertResultFuture(Nil)(found)
    }

    "find in range" in new Fixtures {
      val e1 = event1.copy(id = "1", created = DateTime.parse("2016-01-01"))
      val e2 = event1.copy(id = "2", created = DateTime.parse("2016-02-01"))
      val e3 = event1.copy(id = "3", created = DateTime.parse("2016-03-01"))

      val events = Seq(e1,e2,e3)

      val result = for {
        _ <- Future.sequence(events.map(store.store))
        find <- store.findIdsInRange(DateTime.parse("2016-01-20"), DateTime.parse("2016-02-02"))
      } yield find

      assertResultFuture(Seq(e2))(result)
    }
  }

  trait Fixtures extends WithSession {
    val store = new CqlEventStore(session)

    val event1 = Event(
      id = "1",
      name = "event",
      created = DateTime.parse("2016-02-02"),
      data = "bla")
  }
}




