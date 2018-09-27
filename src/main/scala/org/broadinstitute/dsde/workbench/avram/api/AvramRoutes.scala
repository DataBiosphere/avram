package org.broadinstitute.dsde.workbench.avram.api

import java.util.logging.Logger

import mouse.all._
import com.google.api.server.spi.config.{Api, ApiMethod}
import javax.servlet.http.HttpServletRequest
import org.broadinstitute.dsde.workbench.avram.dataaccess.SamUserInfoResponse
import org.broadinstitute.dsde.workbench.avram.util.transformers._
import org.broadinstitute.dsde.workbench.avram.util.AvramError
import slick.dbio.{DBIOAction, NoStream}
import slick.jdbc.PostgresProfile.api._

import scala.beans.BeanProperty

case class Pong()
case class Now(@BeanProperty message: String)
case class DbPoolStats(@BeanProperty numActive: Int, @BeanProperty numIdle: Int, @BeanProperty totalConnections: Int)

@Api(name = "avram", version = "v1", scopes = Array("https://www.googleapis.com/auth/userinfo.email"))
class AvramRoutes {

  private val log = Logger.getLogger(getClass.getName)

  @ApiMethod(name = "ping", httpMethod = "get", path = "ping")
  def ping: Pong = {
    Pong()
  }

  @ApiMethod(name = "authPing", httpMethod = "get", path = "authPing")
  def authPing(r: HttpServletRequest): Pong = {
    val transformed = for {
      userInfo <- extractUserInfo(r)
    } yield {
      log.info(userInfo.userEmail)
      log.info(userInfo.userSubjectId)
      Pong()
    }

    unsafeRun(transformed)
  }

  // TODO: remove this endpoint when we have more meaningful ways to test database queries
  @ApiMethod(name = "now", httpMethod = "get", path = "now")
  def now: Now = {
    val transformed = for {
      now <- runQuery(sql"select now()".as[String])
    } yield Now(now.head)

    unsafeRun(transformed)
  }

  // TODO: move/merge this endpoint into a status API
  @ApiMethod(name = "dbPoolStats", httpMethod = "get", path = "dbPoolStats")
  def dbPoolStats: DbPoolStats = {
    val transformed = for {
      totalConnections <- runQuery(sql"select count(*) from pg_stat_activity where pid <> pg_backend_pid() and usename = current_user".as[Int])
      numActive <- withDependencies(_.dataSource.getNumActive)
      numIdle   <- withDependencies(_.dataSource.getNumIdle)
    } yield DbPoolStats(numActive, numIdle, totalConnections.head)

    unsafeRun(transformed)
  }

  // TODO move to DB layer
  private def runQuery[A](a: DBIOAction[A, NoStream, Nothing]): AvramResult[A] = {
    withDependenciesIO(deps => futureToIO(deps.database.run(a)))
  }

  // TODO make more robust
  private def getToken(req: HttpServletRequest): Either[AvramError, String] = {
    Option(req.getHeader("Authorization")) map {
      _.stripPrefix("Bearer ")
    } toRight(AvramError(401, "Could not obtain bearer token from request"))
  }

  private def extractUserInfo(r: HttpServletRequest): AvramResult[SamUserInfoResponse] = {
    for {
      token <- getToken(r) |> eitherToResult[AvramError, String]
      samDao <- withDependencies(_.samDAO)
      userInfo <- samDao.getUserStatus(token)
    } yield userInfo
  }
}
