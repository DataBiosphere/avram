package org.broadinstitute.dsde.workbench.avram.api

import java.util.logging.Logger

import com.google.api.server.spi.config.{Api, ApiMethod, Named}
import javax.servlet.http.HttpServletRequest
import org.broadinstitute.dsde.workbench.avram.Avram
import org.broadinstitute.dsde.workbench.avram.dataaccess.{Entity, EntityResponse}
import org.broadinstitute.dsde.workbench.avram.util.ErrorResponse
import slick.jdbc.PostgresProfile.api._
import io.circe.syntax._
import io.circe.generic.auto._

import scala.beans.BeanProperty
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

case class Pong()
case class Now(@BeanProperty message: String)
case class DbPoolStats(@BeanProperty numActive: Int, @BeanProperty numIdle: Int, @BeanProperty totalConnections: Int)


/**
  * Illustration of business logic living outside of the endpoint class.
  */
object PongService {
  private val log = Logger.getLogger(getClass.getName)

  def pong(userInfo: UserInfo): Either[ErrorResponse, Pong] = {
    log.info(userInfo.email)
    log.info(userInfo.subjectId)
    Right(Pong())
  }
}

case class AvramStringResponse(@BeanProperty response: String)

@Api(name = "avram", version = "v1", scopes = Array("https://www.googleapis.com/auth/userinfo.email"))
class AvramRoutes extends BaseEndpoint {

  private val log = Logger.getLogger(getClass.getName)
  private val database = Avram.database

  @ApiMethod(name = "ping", httpMethod = "get", path = "ping")
  def ping: Pong = {
    Pong()
  }

  @ApiMethod(name = "authPing", httpMethod = "get", path = "authPing")
  def authPing(request: HttpServletRequest): Pong = {
    handleAuthenticatedRequest(request) { userInfo => PongService.pong(userInfo) }
  }

  @ApiMethod(name = "getEntities", httpMethod = "get", path = "getEntities")
  def getEntities(request: HttpServletRequest,
                  @Named( "wsNamespace") wsNamespace: String,
                  @Named( "wsName") wsName: String,
                  @Named( "entityType") entityType: String): AvramStringResponse = {
    // Explicitly use a Future to make sure the implicit ExecutionContext is being used
    handleAuthenticatedRequest(request) { userInfo => Avram.rawlsDao.queryEntitiesOfType(wsNamespace, wsName, entityType, userInfo.token) map { x =>
      AvramStringResponse(x.asJson.toString)} }
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

  private def fetchTimestampFromDBWithSlick(): String = {
    val now = Await.result(database.run(sql"select now()".as[String]), Duration.Inf)
    now.head
  }
}
