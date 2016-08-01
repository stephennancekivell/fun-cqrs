package fun.domain

import org.joda.time.DateTime

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object LoaderApp extends scala.App {

  val events = (0 to 20).flatMap { _ =>
    EventGenerator.genEvent.sample
  }

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