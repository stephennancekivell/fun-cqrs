package fun

import fun.domain.tables.{User, UsersDao}
import org.scalatest.FreeSpec
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.ExecutionContext.Implicits.global

class UserDaoSpec extends FreeSpec with CassandraSpec with ScalaFutures with FutureSpecUtils {

  "UserDao" - {
    "create and find" in new Fixtures {
      val findResult = for {
        _ <- userDao.insert(user1)
        u1 <- userDao.findByEmail("foo@bar")
      } yield u1

      assertResultFuture(Some(user1))(findResult)
    }
  }

  trait Fixtures extends WithSession {
    val userDao = new UsersDao(session)

    val user1 = User(
      email = "foo@bar",
      firstname = "bill",
      surname = "cobbler"
    )
  }
}
