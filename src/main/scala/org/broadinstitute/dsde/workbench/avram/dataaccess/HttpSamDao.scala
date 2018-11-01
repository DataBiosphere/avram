package org.broadinstitute.dsde.workbench.avram.dataaccess

import java.util.logging.Logger

import cats.implicits._
import com.softwaremill.sttp.Response
import io.circe.generic.auto._
import io.circe.parser._
import javax.servlet.http.HttpServletResponse
import org.broadinstitute.dsde.workbench.avram.model.AvramException
import org.broadinstitute.dsde.workbench.avram.util.{AvramResult, RestClient}

class HttpSamDao(samUrl: String) extends SamDao with RestClient {
  private val log: Logger = Logger.getLogger(getClass.getName)

  // This function is deprecated and no longer has test coverage. Migrate to getUserStatus after merge of PR #22
  override def getUserStatus_deprecated(token: String): Either[AvramException, SamUserInfoResponse] = {
    // Temporarily put sttp backend in implicit scope here instead of RestClient
    implicit val backend = sttpBackend

    val request = buildAuthenticatedGetRequest(samUrl, "/register/user/v2/self/info", token)
    for {
      response <- request.send()
      content <- response.body                 leftMap errorResponseToAvramException(response.code)
      json <- parse(content)                   leftMap circeErrorToAvramException
      userInfo <- json.as[SamUserInfoResponse] leftMap circeErrorToAvramException
    } yield userInfo
  }

  override def getUserStatus(token: String): AvramResult[SamUserInfoResponse] = {
    // Temporarily put sttp backend in implicit scope here instead of RestClient
    implicit val backend = catsSttpBackend

    val request = buildAuthenticatedGetRequest(samUrl, "/register/user/v2/self/info", token)
    for {
      response <- AvramResult.fromIO(request.send())
      content <- AvramResult.fromEither(response.body leftMap responseToErrorResponse(response))
      json <- AvramResult.fromEither(parse(content) leftMap circeErrorToErrorResponse)
      userInfo <- AvramResult.fromEither(json.as[SamUserInfoResponse] leftMap circeErrorToErrorResponse)
    } yield userInfo
  }

  override def queryAction(samResource: String, action: String, token: String): AvramResult[Boolean] = {
    // Temporarily put sttp backend in implicit scope here instead of RestClient
    implicit val backend = catsSttpBackend

    val request = buildAuthenticatedGetRequest(samUrl, s"/api/resources/v1/entity-collection/$samResource/action/$action", token)
    for {
      response <- AvramResult.fromIO(request.send())
      content <- AvramResult.fromEither(response.body leftMap errorResponseToAvramException(response.code))
    } yield content.toBoolean
  }

  private def errorResponseToAvramException(responseCode: Int) = (body: String) => {
    responseCode match {
      case HttpServletResponse.SC_UNAUTHORIZED => AvramException(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
      case _ =>
        val cause = new Exception(s"Unhandled error from sam: $responseCode: $body")
        AvramException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error", cause)
    }
  }

  private def circeErrorToAvramException(e: io.circe.Error) = AvramException(500, "Server error", e)
}
