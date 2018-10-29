package org.broadinstitute.dsde.workbench.avram.api

import java.util.logging.Logger

import scala.concurrent.duration.Duration
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.broadinstitute.dsde.workbench.avram.Avram
import org.broadinstitute.dsde.workbench.avram.dataaccess.SamUserInfoResponse
import org.broadinstitute.dsde.workbench.avram.model.AvramException

import scala.concurrent.Await
import scala.concurrent.Future
import scala.util.{Failure, Success}


trait AvramServlet {

  implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global

  val log = Logger.getLogger(getClass.getName)
  private val samDao = Avram.samDao
  private val bearerPattern = """(?i)bearer (.*)""".r

  // TODO: Change the structures of registerOnComplete and handleAuthenticationRequest. Not doing it
  // TODO: for PR #18 because we're going to be switching to using IO with AvramResult and this will
  // TODO: likely be changed around anyway.

  def registerOnComplete[T](future: Future[T], request: HttpServletRequest, response: HttpServletResponse) = {
    future onComplete {
      case Success(result) => {
        response.setStatus(HttpServletResponse.SC_OK)
      }
      case Failure(e: AvramException) => {
        log.severe(e.getMessage)
        log.severe(e.getStackTrace.toString)
        response.setStatus(e.status)
        response.getWriter.write(e.message)
      }
      case Failure(e) => {
        log.severe(e.getMessage)
        log.severe(e.getStackTrace.toString)
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
        response.getWriter.write(e.getMessage)
      }
    }
    future
  }

  def handleAuthenticatedRequest[T]
  (request: HttpServletRequest, response: HttpServletResponse)
  (f: SamUserInfoResponse => Future[Either[AvramException, T]]): T = {
    unsafeRun {
      for {
        userInfo <- extractUserInfo(request)
        result <- Await.result(f(userInfo), Duration.apply(30, "second"))
      } yield {
        result
      }
    }
  }

  private def unsafeRun[T](f: => Either[AvramException, T]): T = {
    f.fold(e => throw e, identity)
  }

  private def extractUserInfo(r: HttpServletRequest): Either[AvramException, SamUserInfoResponse] = {
    for {
      token <- extractBearerToken(r)
      userInfo <- samDao.getUserStatus(token)
    } yield {
      userInfo
    }

  }

  private def extractBearerToken(r: HttpServletRequest): Either[AvramException, String] = {
    val token = for {
      value <- Option(r.getHeader("Authorization"))
      tokenMatch <- bearerPattern.findPrefixMatchOf(value)
    } yield {
      tokenMatch.group(1)
    }
    token.toRight(AvramException(HttpServletResponse.SC_UNAUTHORIZED, "User is not authorized."))
  }
}
