package fun.domain

import com.datastax.driver.core.querybuilder.{Insert, QueryBuilder}
import com.datastax.driver.core.{ResultSet, ResultSetFuture}
import com.google.common.util.concurrent.{FutureCallback, Futures}

import scala.concurrent.{Future, Promise}


class RichResultSetFuture(rs: ResultSetFuture) {
  def toFuture(): Future[ResultSet] = {
    val p = Promise[ResultSet]()
    Futures.addCallback(rs,
      new FutureCallback[ResultSet] {
        def onSuccess(r: ResultSet) = p success r
        def onFailure(t: Throwable) = p failure t
      })
    p.future
  }
}


object CassandraImplicits {
  implicit def toRichResultSet(rs: ResultSetFuture): RichResultSetFuture =
    new RichResultSetFuture(rs)


  /**
    * Adds a new easy way to add values to an insert statement.
    *
    * <pre>
    * val query = insertInto(table) values("id" -> id, "col1" -> x, "col2" -> y)
    * </pre>
    *
    * @param insert
    */
  implicit class insertValues(insert: Insert) {
    def values(vals: (String, Any)*) = {
      vals.foldLeft(insert)((i, v) => i.value(v._1, v._2))
    }
  }

  implicit class columnNameWrapper(col: String) {
    def ===(value: Any) = QueryBuilder.eq(col, value)

    def in(values: Seq[AnyRef]) = QueryBuilder.in(col, values: _*)

    def >(value: Any) = QueryBuilder.gt(col, value)

    def >=(value: Any) = QueryBuilder.gte(col, value)

    def <(value: Any) = QueryBuilder.lt(col, value)

    def <=(value: Any) = QueryBuilder.lte(col, value)

    def :=(value: Any) = QueryBuilder.set(col, value)

    def +=(value: Any) = QueryBuilder.add(col, value)

    def -=(value: Any) = QueryBuilder.remove(col, value)

    def ++ = QueryBuilder.incr(col)

    def -- = QueryBuilder.decr(col)

    def ++=(value: Long) = QueryBuilder.incr(col, value)

    def --=(value: Long) = QueryBuilder.decr(col, value)

    def desc = QueryBuilder.desc(col)

    def asc = QueryBuilder.asc(col)
  }
}