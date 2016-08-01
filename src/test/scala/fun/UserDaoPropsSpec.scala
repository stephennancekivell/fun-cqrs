package fun

import fun.domain.EventGenerator
import fun.domain.tables.{User, UsersDao}
import org.scalacheck.Arbitrary
import org.specs2.ScalaCheck
import org.specs2.codata.Process.Await
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher.FutureMatchers
import org.specs2.mutable.Specification
import org.specs2.scalacheck.Parameters

class UserDaoPropsSpec extends Specification with ScalaCheck with FutureMatchers {
  sequential

  val usersDao = new UsersDao(CassandraSpecSupport.session)
  implicit val parameters = Parameters(minTestsOk = 10)
  implicit val arbUser = Arbitrary(EventGenerator.genUser)

  "create read find" >> { implicit ee: ExecutionEnv =>
    prop { u: User =>
      usersDao.all()
        .map(_ must beEmpty).await
      usersDao.insert(u)
        .map(_ must be_==(())).await
      usersDao.findByEmail(u.email)
        .map(_ must beSome(u)).await
      usersDao.all()
        .map(_ must contain(u)).await
    }.before(cleanup)
  }

  "create and find by city" >> { implicit ee: ExecutionEnv =>
    implicit val arbUser = Arbitrary(EventGenerator.genUserWithAddress)
    prop { u: User =>
      u.city must beSome
      usersDao.insert(u).map(_ must be_==(())).await

      usersDao.findByCity(u.city.get)
        .map(_ must contain(u)).await
    }.before(cleanup)

  }

  def cleanup = CassandraSpecSupport.truncateAllKeyspaces("events", CassandraSpecSupport.session)
}
