package fun

import fun.domain.EventGenerator
import fun.domain.tables.{User, UsersDao}
import org.scalacheck.Arbitrary
import org.specs2.ScalaCheck
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher.FutureMatchers
import org.specs2.mutable.Specification
import org.specs2.scalacheck.Parameters

class UserDaoPropsSpec extends Specification with ScalaCheck with FutureMatchers {

  val usersDao = new UsersDao(CassandraSpecSupport.session)
  implicit val parameters = Parameters(minTestsOk = 10)
  implicit val arbUser = Arbitrary(EventGenerator.genUser)

  "create read find" >> { implicit ee: ExecutionEnv =>
    prop { u: User =>

      usersDao.all().map(_ must beEmpty).await

      val result = for {
        _ <- usersDao.insert(u)
        findOne <- usersDao.findByEmail(u.email)
        all <- usersDao.all()
      } yield (findOne, all)

      result.map(_._1 must beSome(u)).await
      result.map(_._2 must contain(u)).await
    }.before(cleanup)
  }

  def cleanup = CassandraSpecSupport.truncateAllKeyspaces("events", CassandraSpecSupport.session)

}
