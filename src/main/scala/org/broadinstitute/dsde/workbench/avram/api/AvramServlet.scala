package org.broadinstitute.dsde.workbench.avram.api

import java.util.logging.{Level, Logger}

import io.circe.Encoder
import io.circe.syntax._
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.broadinstitute.dsde.workbench.avram.Avram
import org.broadinstitute.dsde.workbench.avram.dataaccess.SamUserInfoResponse
import org.broadinstitute.dsde.workbench.avram.model.AvramException
import org.broadinstitute.dsde.workbench.avram.util.AvramResult
import org.broadinstitute.dsde.workbench.avram.util.AvramResult.unsafeRun

import scala.concurrent.ExecutionContext


trait AvramServlet {

  implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val log: Logger = Logger.getLogger(getClass.getName)
  private val samDao = Avram.samDao
  private val bearerPattern = """(?i)bearer (.*)""".r

  def handleAuthenticatedRequest[T](request: HttpServletRequest, response: HttpServletResponse)
                                   (f: SamUserInfoResponse => AvramResult[T])
                                   (implicit encoder: Encoder[T]): Unit = {
    def writeBody(body: T): Unit = {
      response.setStatus(HttpServletResponse.SC_OK)
      response.setContentType("application/json")
      // CORS?
      response.getWriter.write(body.asJson.noSpaces)
    }

    def writeError(e: AvramException): Unit = {
      // Dynamic log level based on response code?
      log.log(Level.SEVERE, s"Responding with non-success status: ${e.status}", e)
      // Log cause. TODO: look into a logging framework on top of java.util.logging
      e.cause.foreach(t => log.log(Level.SEVERE, s"Caused by:", t))

      response.setStatus(e.status)
      response.setContentType("text/plain")
      // CORS?
      response.getWriter.write(e.regrets)
    }

    unsafeRun(writeBody, writeError, t => AvramException(500, s"Unhandled error", t)) {
      for {
        userInfo <- extractUserInfo(request)
        result <- f(userInfo)
      } yield result
    }
  }

  private def extractUserInfo(r: HttpServletRequest): AvramResult[SamUserInfoResponse] = {
    for {
      token <- AvramResult.fromEither(extractBearerToken(r))
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
