package fun

import com.datastax.driver.core.{Cluster, Session}
import org.cassandraunit.CQLDataLoader
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet
import org.cassandraunit.utils.EmbeddedCassandraServerHelper
import org.scalatest.{BeforeAndAfterEach, Suite}

import scala.collection.JavaConverters._

object CassandraSpecSupport {
  timeIt("start cassandra") {
    EmbeddedCassandraServerHelper.startEmbeddedCassandra()
  }

  val session = createSession()

  timeIt("drop tables"){
    EmbeddedCassandraServerHelper.cleanEmbeddedCassandra()
  }

  timeIt("create tables") {
    loadCQL("db.migrations/1.cql", session)
  }

  def timeIt[A](label: String)(thunk: => A): A = {
    val start = System.currentTimeMillis()
    val re = thunk
    //println(s"[timeit - $label] ${System.currentTimeMillis() - start}ms")
    re
  }

  def createSession(): Session = timeIt("create session"){
    val host = EmbeddedCassandraServerHelper.getHost
    val port = EmbeddedCassandraServerHelper.getNativeTransportPort
    println(s"creating session to $host:$port")
    val cluster = Cluster.builder().addContactPoint(host)
      .withPort(port)
      .withCredentials("cassandra", "cassandra")
      .withClusterName("Test Cluster")
      .build()
    cluster.connect()
  }

  def loadCQL(file: String, session: Session): Unit = {
    val cql = new ClassPathCQLDataSet(file)
    val cqlLoader = new CQLDataLoader(session)
    cqlLoader.load(cql)
  }

  def truncateAllKeyspaces(keyspace: String, session: Session): Unit = timeIt("truncate all"){
    listTables(keyspace, session).foreach { table =>
      truncate(keyspace, table, session)
    }
  }

  def listTables(keyspace: String, session: Session): Seq[String] = timeIt("list tables in keyspace"){
    session.getCluster.getMetadata.getKeyspace(keyspace).getTables.asScala.map(_.getName).toSeq
  }

  def truncate(keyspace: String, table: String, session: Session): Unit = timeIt("truncate table") {
    session.execute(s"truncate $keyspace.$table")
  }
}

trait CassandraSpec extends Suite with BeforeAndAfterEach {
  override def afterEach(): Unit = {
    CassandraSpecSupport.truncateAllKeyspaces("events", CassandraSpecSupport.session)
  }

  trait WithSession {
    val session = CassandraSpecSupport.session
  }
}
