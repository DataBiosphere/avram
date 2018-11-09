package org.broadinstitute.dsde.workbench.avram.api

import java.util.logging.{Level, Logger}

import io.circe.Encoder
import io.circe.syntax._
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status

import org.broadinstitute.dsde.workbench.avram.Avram
import org.broadinstitute.dsde.workbench.avram.dataaccess.SamUserInfoResponse
import org.broadinstitute.dsde.workbench.avram.model.AvramException
import org.broadinstitute.dsde.workbench.avram.util.AvramResult
import org.broadinstitute.dsde.workbench.avram.util.AvramResult.unsafeRun

import scala.concurrent.ExecutionContext


abstract class AvramEndpoint(avram: Avram) {

  implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val log: Logger = Logger.getLogger(getClass.getName)
  private val samDao = avram.samDao
  private val bearerPattern = """(?i)bearer (.*)""".r

  def handleAuthenticatedRequest[T](authorizationHeader: String)
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

    unsafeRun(writeBody, writeError, t => AvramException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode, s"Unhandled error", t)) {
      for {
        userInfo <-  extractUserInfo(authorizationHeader)
        result <- f(userInfo)
      } yield result
    }
  }

  private def extractUserInfo(authorizationHeader: String): AvramResult[SamUserInfoResponse] = {
    for {
      token <- AvramResult.fromEither(extractBearerToken(authorizationHeader))
      userInfo <- samDao.getUserStatus(token)
    } yield {
      userInfo
    }
  }

  private def extractBearerToken(authorizationHeader: String): Either[AvramException, String] = {
    val token = for {
      tokenMatch <- bearerPattern.findPrefixMatchOf(authorizationHeader)
    } yield {
      tokenMatch.group(1)
    }
    token.toRight(AvramException(Response.Status.UNAUTHORIZED.getStatusCode, "User is not authorized."))
  }
}
