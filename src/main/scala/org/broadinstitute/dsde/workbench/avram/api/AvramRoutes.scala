package org.broadinstitute.dsde.workbench.avram.api

import com.google.api.server.spi.config.{Api, ApiMethod}
import com.google.api.server.spi.response.UnauthorizedException
import com.typesafe.config.ConfigFactory
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest

import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import org.broadinstitute.dsde.workbench.avram.config.DbcpDataSourceConfig
import org.broadinstitute.dsde.workbench.avram.dataaccess.{HttpSamDao, UserInfo}
import org.broadinstitute.dsde.workbench.avram.util.DataSourceFactory
import org.broadinstitute.dsde.workbench.avram.util.{DataSource, ErrorResponse}
import org.broadinstitute.dsde.workbench.avram.{HttpSamDao, UserInfo}

import scala.beans.BeanProperty
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import slick.jdbc.PostgresProfile.api._


case class Pong()
case class Now(@BeanProperty message: String)
case class DbPoolStats(@BeanProperty numActive: Int, @BeanProperty numIdle: Int, @BeanProperty totalConnections: Int)

@Api(name = "avram", version = "v1", scopes = Array("https://www.googleapis.com/auth/userinfo.email"))
class AvramRoutes {

  private val log = Logger.getLogger(getClass.getName)

  val configFactory = ConfigFactory.parseResources("app.conf").withFallback(ConfigFactory.load())
  private val dbcpDataSourceConfig = configFactory.as[DbcpDataSourceConfig]("dbcpDataSource")
  val dataSource = new DataSourceFactory(dbcpDataSourceConfig)


  @ApiMethod(name = "ping", httpMethod = "get", path = "ping")
  def ping: Pong = {
    Pong()
  }

  @ApiMethod(name = "authPing", httpMethod = "get", path = "authPing")
  def authPing(r: HttpServletRequest): Pong = {
    getToken(r) match {
      case None => throw new UnauthorizedException("Missing access token")
      case Some(token) =>
        val result: Either[ErrorResponse, UserInfo] = new HttpSamDao().getUserStatus(token)
        result match {
          case Left(error) => throw error.exception
          case Right(userInfo) =>
            log.info(userInfo.userEmail)
            log.info(userInfo.userSubjectId)
            Pong()
        }
    }
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

  private def getToken(req: HttpServletRequest) = {
    Option(req.getHeader("Authorization")) map {
      _.stripPrefix("Bearer ")
    }
  }

  private def fetchTimestampFromDBWithSlick(): String = {
    val now = Await.result(dataSource.database.run(sql"select now()".as[String]), Duration.Inf)
    now.head
  }
}
