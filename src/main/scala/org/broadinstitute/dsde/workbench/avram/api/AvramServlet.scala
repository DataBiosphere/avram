package org.broadinstitute.dsde.workbench.avram.api


import scala.concurrent.duration.Duration
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.broadinstitute.dsde.workbench.avram.Avram
import org.broadinstitute.dsde.workbench.avram.dataaccess.SamUserInfoResponse
import org.broadinstitute.dsde.workbench.avram.model.AvramException
import org.broadinstitute.dsde.workbench.avram.util.ErrorResponse

import scala.concurrent.Await
import scala.util.{Failure, Success}
//import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AvramServlet {

  implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global

  private val samDao = Avram.samDao
  private val bearerPattern = """(?i)bearer (.*)""".r

  def registerOnComplete[T]
  (future: Future[T], request: HttpServletRequest, response: HttpServletResponse)
  (f: T => Unit): Future[T] = {
    future onComplete {
      case Success(result) => f(result)
      case Failure(e: AvramException) => {
        response.setStatus(e.status)
        response.getWriter.write(e.message)
      }
      case Failure(e) => response.getWriter.write(e.getMessage)
    }
    future
  }


  def handleAuthenticatedRequest[T]
  (request: HttpServletRequest, response: HttpServletResponse)
  (f: SamUserInfoResponse => Future[Either[AvramException, T]]): T = {
    val thing = for {
      userInfo <- extractUserInfo(request)
      result <- f(userInfo)
    } yield userInfo
    unsafeRun(Await.result(f(thing), Duration.apply(30, "second")))
  }

  private def unsafeRun[T](f: => Either[AvramException, T]): T = {
    f.fold(e => throw e, identity)
  }

  private def extractUserInfo(r: HttpServletRequest): Either[AvramException, SamUserInfoResponse] = {
    for {
      token <- extractBearerToken(r)
      userInfo <- samDao.getUserStatus(token)
    } yield userInfo
  }

  private def extractBearerToken(r: HttpServletRequest): Either[AvramException, String] = {
    val token = for {
      value <- Option(r.getHeader("Authorization"))
      tokenMatch <- bearerPattern.findPrefixMatchOf(value)
    } yield tokenMatch.group(1)
    token.toRight(AvramException(HttpServletResponse.SC_NOT_FOUND, "Auth token not found."))
  }
}
