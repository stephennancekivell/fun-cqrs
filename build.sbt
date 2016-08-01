scalaVersion := "2.11.8"

name := "fun-cqrs"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
	"org.scalaz" %% "scalaz-core" % "7.2.4",
	"org.scalatest" %% "scalatest" % "2.2.5" % Test,
	"org.scalacheck" %% "scalacheck" % "1.13.2",
	"com.typesafe.akka" %% "akka-actor" % "2.4.8",
	"com.typesafe.akka" %% "akka-http-core" % "2.4.8",
	"com.typesafe.akka" %% "akka-http-experimental" % "2.4.8",
	"com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.8",
	"com.datastax.cassandra" % "cassandra-driver-core" % "2.1.10.2",
	"com.github.thurstonsand" %% "scalacass" % "0.3.4",
	"org.cassandraunit" % "cassandra-unit" % "2.1.9.2" % Test,
	"org.specs2" %% "specs2-core" % "3.8.4" % Test,
  "org.specs2" %% "specs2-scalacheck" % "3.8.4" % Test
)
resolvers += Resolver.jcenterRepo

fork in Test := true

parallelExecution in Test := false

scalacOptions in Test ++= Seq("-Yrangepos")
