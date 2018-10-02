package org.broadinstitute.dsde.workbench.avram.api

import java.util.logging.Logger

import com.google.api.server.spi.config.{Api, ApiMethod}
import com.google.api.server.spi.response.UnauthorizedException
import javax.servlet.http.HttpServletRequest
import org.broadinstitute.dsde.workbench.avram.Avram
import org.broadinstitute.dsde.workbench.avram.dataaccess.SamUserInfoResponse
import org.broadinstitute.dsde.workbench.avram.util.ErrorResponse
import slick.jdbc.PostgresProfile.api._

import scala.beans.BeanProperty
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


case class Pong()
case class Now(@BeanProperty message: String)
case class DbPoolStats(@BeanProperty numActive: Int, @BeanProperty numIdle: Int, @BeanProperty totalConnections: Int)

@Api(name = "avram", version = "v1", scopes = Array("https://www.googleapis.com/auth/userinfo.email"))
class AvramRoutes {

  private val log = Logger.getLogger(getClass.getName)
  private val samDao = Avram.samDao
  private val database = Avram.database

  @ApiMethod(name = "ping", httpMethod = "get", path = "ping")
  def ping: Pong = {
    Pong()
  }

  @ApiMethod(name = "authPing", httpMethod = "get", path = "authPing")
  def authPing(r: HttpServletRequest): Pong = {
    getToken(r) match {
      case None => throw new UnauthorizedException("Missing access token")
      case Some(token) =>
        val result: Either[ErrorResponse, SamUserInfoResponse] = samDao.getUserStatus(token)
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
    val dataSource = Avram.dbcpDataSource
    val result = for {
      totalConnections <- database.run(
        sql"select count(*) from pg_stat_activity where pid <> pg_backend_pid() and usename = current_user".as[Int])
    } yield DbPoolStats(dataSource.getNumActive, dataSource.getNumIdle, totalConnections.head)
    Await.result(result, Duration.Inf)
  }

  private def getToken(req: HttpServletRequest) = {
    Option(req.getHeader("Authorization")) map {
      _.stripPrefix("Bearer ")
    }
  }

  private def fetchTimestampFromDBWithSlick(): String = {
    val now = Await.result(database.run(sql"select now()".as[String]), Duration.Inf)
    now.head
  }
}
