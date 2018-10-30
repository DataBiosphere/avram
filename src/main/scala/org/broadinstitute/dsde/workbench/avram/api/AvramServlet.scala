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
      response.getWriter.write(body.asJson.noSpaces)
    }

    def writeError(e: AvramException): Unit = {
      log.log(Level.SEVERE, "Unhandled error", e)
      response.setStatus(e.status)
      response.getWriter.write(e.message)
    }

    unsafeRun(writeBody, writeError, t => AvramException(500, t.getMessage)) {
      for {
        userInfo <- AvramResult.fromEither(extractUserInfo(request))
        result <- f(userInfo)
      } yield result
    }
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
