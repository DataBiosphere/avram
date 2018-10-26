package org.broadinstitute.dsde.workbench.avram.api

import java.util.logging.Logger

import cats.effect.IO
import com.google.api.server.spi.config.{Api, ApiMethod}
import javax.servlet.http.HttpServletRequest
import org.broadinstitute.dsde.workbench.avram.Avram
import org.broadinstitute.dsde.workbench.avram.dataaccess.SamUserInfoResponse
import org.broadinstitute.dsde.workbench.avram.model.{DbPoolStats, Status}
import org.broadinstitute.dsde.workbench.avram.util.AvramResult.AvramResult
import org.broadinstitute.dsde.workbench.avram.util.ErrorResponse


case class Pong()

/**
  * Illustration of business logic living outside of the endpoint class.
  */
object PongService {
  private val log = Logger.getLogger(getClass.getName)

  @deprecated("Service functions should return AvramResult[T]", "11/1/18")
  def unsafePong(userInfo: SamUserInfoResponse): Either[ErrorResponse, Pong] = {
    log.info(userInfo.userEmail)
    log.info(userInfo.userSubjectId)
    Right(Pong())
  }

  def pong(userInfo: SamUserInfoResponse): AvramResult[Pong] = {
    AvramResult {
      IO {
        log.info(s"Answering ping from ${userInfo.userEmail} (${userInfo.userSubjectId})")
        Pong()
      }
    }
  }
}

@Api(name = "avram", version = "v1", scopes = Array("https://www.googleapis.com/auth/userinfo.email"))
class AvramRoutes(avram: Avram) extends BaseEndpoint(avram) {
  def this() = this(Avram)

  @ApiMethod(name = "ping", httpMethod = "get", path = "ping")
  def ping: Pong = {
    Pong()
  }

  @ApiMethod(name = "authPing", httpMethod = "get", path = "authPing")
  def authPing(request: HttpServletRequest): Pong = {
    handleAuthenticatedRequest(request) { userInfo => PongService.unsafePong(userInfo) }
  }

  @ApiMethod(name = "authzPing", httpMethod = "get", path = "authzPing")
  def authzPing(request: HttpServletRequest, samResource: String, action: String): Pong = {
    handleAuthorizedRequest(request, samResource, action) { userInfo => PongService.pong(userInfo) }
  }

  @ApiMethod(name = "status", httpMethod = "get", path = "status")
  def status: Status = {
    //TODO: make this use HealthMoniter
    inTransaction(_.sqlDBStatus()).map(_ => Status("ok")).head
  }

  @ApiMethod(name = "dbPoolStats", httpMethod = "get", path = "dbPoolStats")
  def dbPoolStats: DbPoolStats = {
    getDbPoolStats
  }

}
