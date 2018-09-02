package org.broadinstitute.dsde.workbench.avram.api

import com.google.api.server.spi.config.{Api, ApiMethod}
import java.util.logging.Logger
import net.ceedubs.ficus.Ficus._
import com.typesafe.config.ConfigFactory
import org.broadinstitute.dsde.workbench.avram.config.DbcpDataSourceConfig
import org.broadinstitute.dsde.workbench.avram.util.DataSource

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import slick.jdbc.PostgresProfile.api._

import scala.beans.BeanProperty

case class Pong()
case class Now(@BeanProperty message: String)
case class DbPoolStats(@BeanProperty numActive: Int, @BeanProperty numIdle: Int, @BeanProperty totalConnections: Int)

@Api(name = "avram", version = "v1", scopes = Array("https://www.googleapis.com/auth/userinfo.email"))
class AvramRoutes {

  private val log = Logger.getLogger(getClass.getName)
  val configFactory = ConfigFactory.parseResources("app.conf") //s.withFallback(ConfigFactory.load())
  private val dbcpDataSourceConfig = configFactory.as[DbcpDataSourceConfig]("dbcpDataSource")
  val dataSource = new DataSource(dbcpDataSourceConfig)


  @ApiMethod(name = "ping", httpMethod = "get", path = "ping")
  def ping: Pong = {
    Pong()
  }

  // TODO: remove this endpoint when we have more meaningful ways to test database queries
  @ApiMethod(name = "now", httpMethod = "get", path = "now")
  def now: Now = {
    // Explicitly use a Future to make sure the implicit ExecutionContext is being used
    Now(Await.result(Future(fetchTimestampFromDBWithSlick()), Duration.Inf))
  }

  // TODO: move/merge this endpoint into a status API
  @ApiMethod(name = "dbPoolStats", httpMethod = "get", path = "dbPoolStats")
  def dbPoolStats: DbPoolStats = {
    val result = for {
      totalConnections <-  dataSource.database.run(
        sql"select count(*) from pg_stat_activity where pid <> pg_backend_pid() and usename = current_user".as[Int])
    } yield DbPoolStats(dataSource.ds.getNumActive, dataSource.ds.getNumIdle, totalConnections.head)
    Await.result(result, Duration.Inf)
  }

  private def fetchTimestampFromDBWithSlick(): String = {
    val now = Await.result(dataSource.database.run(sql"select now()".as[String]), Duration.Inf)
    now.head
  }
}
