package fun.domain

import fun.eventslib.Event
import org.joda.time.DateTime

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object LoaderApp extends scala.App {

  val event1 = Event(
    name = "UserSignupRequest",
    data = Protocols.userSignupFormat.write(UserSignupRequest(
      email = "foo@bar.com",
      firstname = "joe",
      surname = "cobbler"
    )).toString()
  )

  val events = Seq(event1)

  val results = events.map(MyEventMachine.process)

  Future.sequence(results)
    .onComplete { _ =>
      Core.session.getCluster.close()
    }


}

object EventReplay extends scala.App {
  MyEventMachine.replay(DateTime.now.minusYears(1))
    .onComplete { _ =>
      Core.session.getCluster.close()
    }
}