package fun.eventslib

/**
  * Created by snanceki on 28/07/2016.
  */
trait LoggingSupport {
  private val clazz = this.getClass
  object logger {
    def info(msg: String): Unit = log("INFO", msg)
    def warn(msg: String): Unit = log("WARN", msg)
    def error(msg: String): Unit = log("ERROR", msg)
    private def log(level: String, msg: String): Unit = println(s"${clazz.getCanonicalName} [$level] "+msg)
  }
}
