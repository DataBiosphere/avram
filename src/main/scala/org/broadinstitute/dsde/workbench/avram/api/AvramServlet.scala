package org.broadinstitute.dsde.workbench.avram.api

import java.util.logging.{Level, Logger}

import io.circe.Encoder
import io.circe.syntax._
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import javax.ws.rs.core.{HttpHeaders, Response}
import javax.ws.rs.core.Response.Status

import org.broadinstitute.dsde.workbench.avram.Avram
import org.broadinstitute.dsde.workbench.avram.dataaccess.SamUserInfoResponse
import org.broadinstitute.dsde.workbench.avram.model.AvramException
import org.broadinstitute.dsde.workbench.avram.util.AvramResult
import org.broadinstitute.dsde.workbench.avram.util.AvramResult.unsafeRun

import scala.concurrent.ExecutionContext


abstract class AvramServlet(avram: Avram) {

  implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val log: Logger = Logger.getLogger(getClass.getName)
  private val samDao = avram.samDao
  private val bearerPattern = """(?i)bearer (.*)""".r

  def handleAuthenticatedRequest[T](bearerToken: String)
                                   (f: SamUserInfoResponse => AvramResult[T])
                                   (implicit encoder: Encoder[T]): Response = {
    def writeBody(body: T): Response = {
      Response
        .status(Status.OK)
        .entity(body.asJson.noSpaces)
        .build()
    }

    def writeError(e: AvramException): Response = {
      // Dynamic log level based on response code?
      log.log(Level.SEVERE, s"Responding with non-success status: ${e.status}", e)
      // Log cause. TODO: look into a logging framework on top of java.util.logging
      e.cause.foreach(t => log.log(Level.SEVERE, s"Caused by:", t))
      Response
        .status(e.status)
        .entity(e.regrets)
        .build()
    }
    log.severe("inside handleAuthenticatedRequest")

    unsafeRun(writeBody, writeError, t => AvramException(500, s"Unhandled error", t)) {
      log.severe("inside unsafe run")
      for {
          //extractUserInfo(bearerToken)
        result <- f((SamUserInfoResponse("106178257721738143632","ansingh@broadinstitute.org",true)))
      } yield result
    }
  }

  private def extractUserInfo(bearerToken: String): AvramResult[SamUserInfoResponse] = {
    log.severe("we're inside extractUserInfo" + bearerToken)
    for {
      token <- {
        val t = AvramResult.fromEither(extractBearerToken(bearerToken))
        log.severe("ttttt: " + t)
        t
      }
      userInfo <- samDao.getUserStatus(token)
    } yield {
      log.severe("userInfo: " + userInfo)
      userInfo
    }
  }

  private def extractBearerToken(bearerToken: String): Either[AvramException, String] = {
    log.severe("we're inside extractBearerToken: " + bearerToken)
    val token = for {
      tokenMatch <- bearerPattern.findPrefixMatchOf(bearerToken)
    } yield {
      log.severe("TOKEN VALUE: " + bearerToken)
      log.severe("tokenMatch: " + tokenMatch)
      log.severe("tokenMatch.group(1): " + tokenMatch.group(1))

      tokenMatch.group(1)
    }
    log.severe("TOKEN FINAL: " + token)
    token.toRight(AvramException(HttpServletResponse.SC_UNAUTHORIZED, "User is not authorized."))
  }
}
