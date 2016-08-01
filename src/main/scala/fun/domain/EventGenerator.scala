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

  val addresses: Seq[String] =
    Source.fromInputStream(getClass.getClassLoader.getResourceAsStream("Addresses.csv"))
      .getLines()
      .toSeq

  val domains = Seq(
    "gmail.com",
    "hotmail.com",
    "yahoo.com"
  )

  val cities = Seq(
    "Sydney",
    "Melbourne",
    "Brisbane",
    "Perth",
    "Adelaide"
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

  val genAddress: Gen[(String,String)] = for {
    city <- Gen.oneOf(cities)
    address <- Gen.oneOf(addresses)
  } yield (city, address)

  val genUser: Gen[User] = {
    for {
      name <- Gen.oneOf(names)
      email <- emailFromName(name)
      cityAddress <- Gen.option(genAddress)
    } yield {
      val parts = name.split(" ")
      val (fname, surname) = (parts(0),parts(1))

      User(
        email = email,
        firstname = fname,
        surname = surname,
        city = cityAddress.map(_._1),
        address = cityAddress.map(_._2)
      )
    }
  }

  val genUserWithAddress: Gen[User] = {
    for {
      user <- genUser
      addr <- genAddress
    } yield {
      user.copy(
        city = Some(addr._1),
        address = Some(addr._2)
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

  val userSignupRequestV2Gen: Gen[UserSignupRequestV2] = {
    for {
      name <- Gen.oneOf(names)
      email <- emailFromName(name)
      cityAddress <- Gen.option(genAddress)
    } yield {
      val parts = name.split(" ")
      val (fname, surname) = (parts(0), parts(1))

      UserSignupRequestV2(
        email = email,
        firstname = fname,
        surname = surname,
        city = cityAddress.map(_._1),
        address = cityAddress.map(_._2)
      )
    }
  }

  import ToEventImplicits._

  val genEvent: Gen[Event] = for {
    usg <- userSignupRequestGen.map(_.toEvent())
    usgV2 <- userSignupRequestV2Gen.map(_.toEvent())
    x <- Gen.oneOf(Seq(
      usg,
      usgV2))
  } yield x
}
