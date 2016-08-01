package fun

import fun.domain.CqlEventStore
import fun.eventslib.Event
import org.scalacheck.Arbitrary
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher.FutureMatchers
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification
import org.specs2.scalacheck.Parameters

class CqlEventStorePropsSpec extends Specification with ScalaCheck with FutureMatchers {

  val store = new CqlEventStore(CassandraSpecSupport.session)

  implicit val parameters = Parameters(minTestsOk = 10)

  implicit val genEvent = fun.domain.EventGenerator.genEvent
  implicit val arbEvent = Arbitrary(genEvent)

  "store" >> { implicit ee: ExecutionEnv =>
    prop { event: Event =>

      store.all().map(_ must beEmpty).await

      val result = for {
        _ <- store.store(event)
        all <- store.all()
        one <- store.find(event.id)
      } yield all -> one

      result.map(_._1 must contain(event)).await
      result.map(_._2 must beSome(event)).await
    }.before(cleanup)
  }

  def cleanup = CassandraSpecSupport.truncateAllKeyspaces("events", CassandraSpecSupport.session)
}
