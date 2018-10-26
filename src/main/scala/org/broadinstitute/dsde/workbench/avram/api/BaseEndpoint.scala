package org.broadinstitute.dsde.workbench.avram.api

import java.util.logging.Logger

import javax.servlet.http.HttpServletRequest
import org.broadinstitute.dsde.workbench.avram.Avram
import org.broadinstitute.dsde.workbench.avram.dataaccess.SamUserInfoResponse
import org.broadinstitute.dsde.workbench.avram.db.DataAccess
import org.broadinstitute.dsde.workbench.avram.model.DbPoolStats
import org.broadinstitute.dsde.workbench.avram.util.AvramResult.AvramResult
import org.broadinstitute.dsde.workbench.avram.util.ErrorResponse
import slick.dbio.DBIO

import scala.concurrent.Await
import scala.concurrent.duration.Duration



abstract class BaseEndpoint(avram: Avram) {

  private val log = Logger.getLogger(getClass.getName)
  private val database = avram.database
  private val samDao = avram.samDao
  private val bearerPattern = """(?i)bearer (.*)""".r

  def handleAuthenticatedRequest[T](request: HttpServletRequest)
                                   (f: SamUserInfoResponse => Either[ErrorResponse, T]): T = {
    unsafeRun {
      for {
        userInfo <- extractUserInfo(request)
        result <- f(userInfo)
      } yield result
    }
  }

  @deprecated("Migrate to unsafeRun[T](AvramResult[T])", "11/1/18")
  private def unsafeRun[T](f: => Either[ErrorResponse, T]): T = {
    f.fold(e => throw e.exception, identity)
  }

  def handleAuthorizedRequest[T](request: HttpServletRequest, samResource: String, action: String)
                                (f: SamUserInfoResponse => AvramResult[T]): T = {
    unsafeRun {
      for {
        token <- AvramResult(extractBearerToken(request))
        userInfo <- AvramResult(samDao.getUserStatus(token))
        _ <- checkActionAuthorization(samResource, action, token)
        result <- f(userInfo)
      } yield result
    }
  }

  private def unsafeRun[T](r: AvramResult[T]): T = {
    r.value.unsafeRunSync().fold(e => throw e.exception, identity)
  }

  private def extractUserInfo(r: HttpServletRequest): Either[ErrorResponse, SamUserInfoResponse] = {
    for {
      token <- extractBearerToken(r)
      userInfo <- samDao.getUserStatus(token)
    } yield userInfo
  }

  private def checkActionAuthorization(samResource: String, action: String, token: String): AvramResult[Unit] = {
    for {
      can <- samDao.queryAction(samResource, action, token)
      authorized <- AvramResult { if (can) Right(()) else Left(ErrorResponse(403, "Permission denied")) }
    } yield authorized
  }

  private def extractBearerToken(r: HttpServletRequest): Either[ErrorResponse, String] = {
    val token = for {
      value <- Option(r.getHeader("Authorization"))
      tokenMatch <- bearerPattern.findPrefixMatchOf(value)
    } yield tokenMatch.group(1)
    token.toRight(ErrorResponse(401, "Missing token"))
  }

  def inTransaction[T](f: (DataAccess) => DBIO[T]): T = {
    Await.result(database.inTransaction(f) , Duration.apply(30, "second"))
  }

  def getDbPoolStats: DbPoolStats = {
    inTransaction(_.dbTotalConnections()).map(DbPoolStats(database.dbcpDataSource.getNumActive, database.dbcpDataSource.getNumIdle, _)).head
  }

}
