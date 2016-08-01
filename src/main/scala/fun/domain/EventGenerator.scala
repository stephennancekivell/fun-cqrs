package fun.domain

import fun.domain.tables.User
import fun.eventslib.Event
import org.scalacheck.Gen

import scala.io.Source

object EventGenerator {
  val names: Seq[String] =
    Source.fromInputStream(getClass.getClassLoader.getResourceAsStream("names.txt"))
      .getLines()
      .toSeq

  val domains = Seq(
    "gmail.com",
    "hotmail.com",
    "yahoo.com"
  )

  val arbEmail: Gen[String] = {
    for {
      name <- Gen.oneOf(names)
      domain <- Gen.oneOf(domains)
    } yield {
      name.replaceAll(" ", ".") + "@" + domain
    }
  }

  def emailFromName(name: String): Gen[String] = {
    Gen.oneOf(domains).map { domain =>
      name.replaceAll(" ", ".") + "@" + domain
    }
  }

  val genUser: Gen[User] = {
    for {
      name <- Gen.oneOf(names)
      email <- emailFromName(name)
    } yield {
      val parts = name.split(" ")
      val (fname, surname) = (parts(0),parts(1))

      User(
        email = email,
        firstname = fname,
        surname = surname
      )
    }
  }

  val userSignupRequestGen: Gen[UserSignupRequest] = {
    for {
      name <- Gen.oneOf(names)
      email <- emailFromName(name)
    } yield {
      val parts = name.split(" ")
      val (fname, surname) = (parts(0),parts(1))

      UserSignupRequest(
        email = email,
        firstname = fname,
        surname = surname
      )
    }
  }

  import ToEventImplicits._

  val genEvent: Gen[Event] = for {
    usg <- userSignupRequestGen.map(_.toEvent())
    x <- Gen.oneOf(Seq(usg))
  } yield x
}
