package org.broadinstitute.dsde.workbench.avram.dataaccess

import java.util.logging.Logger

import cats.implicits._
import io.circe.generic.auto._
import io.circe.parser._
import javax.servlet.http.HttpServletResponse
import org.broadinstitute.dsde.workbench.avram.model.{AvramException, SamResource}
import org.broadinstitute.dsde.workbench.avram.util.{AvramResult, RestClient}

class HttpSamDao(samUrl: String) extends SamDao with RestClient {
  private val log: Logger = Logger.getLogger(getClass.getName)

  override def getUserStatus(token: String): AvramResult[SamUserInfoResponse] = {
    val request = buildAuthenticatedGetRequest(samUrl, "/register/user/v2/self/info", token)
    for {
      response <- AvramResult.fromIO(request.send())
      content <- AvramResult.fromEither(response.body leftMap errorResponseToAvramException(response.code))
      json <- AvramResult.fromEither(parse(content) leftMap circeErrorToAvramException)
      userInfo <- AvramResult.fromEither(json.as[SamUserInfoResponse] leftMap circeErrorToAvramException)
    } yield userInfo
  }

  override def queryAction(samResource: SamResource, action: String, token: String): AvramResult[Boolean] = {
    val request = buildAuthenticatedGetRequest(samUrl, s"/api/resources/v1/entity-collection/${samResource.resourceName}/action/$action", token)
    for {
      response <- AvramResult.fromIO(request.send())
      content <- AvramResult.fromEither(response.body leftMap errorResponseToAvramException(response.code))
    } yield content.toBoolean
  }

  private def errorResponseToAvramException(responseCode: Int) = (body: String) => {
    responseCode match {
      case code @ HttpServletResponse.SC_UNAUTHORIZED => AvramException(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", new Exception(s"Sam responded with $code: $body"))
      // TODO: only translate 404 to 401 when checking user status
      case code @ HttpServletResponse.SC_NOT_FOUND => AvramException(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", new Exception(s"Sam responded with $code: $body"))
      case _ =>
        val cause = new Exception(s"Unhandled error from sam: $responseCode: $body")
        AvramException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error", cause)
    }
  }

  private def circeErrorToAvramException(e: io.circe.Error) = AvramException(500, "Server error", e)
}
