package org.broadinstitute.dsde.workbench.avram.dataaccess

import java.util.logging.Logger

import cats._
import cats.implicits._
import com.softwaremill.sttp.{Id, _}
import io.circe.Decoder.Result
import io.circe.{DecodingFailure, Json, ParsingFailure}
import io.circe.generic.auto._
import io.circe.parser._
import org.broadinstitute.dsde.workbench.avram.util.{AvramResult, ErrorResponse, RestClient}

class HttpSamDao(samUrl: String) extends SamDao with RestClient {
  private val log: Logger = Logger.getLogger(getClass.getName)

  override def getUserStatus(token: String): Either[ErrorResponse, SamUserInfoResponse] = {
    // Temporarily put sttp backend in implicit scope here instead of RestClient
    implicit val backend = sttpBackend

    val request = buildAuthenticatedGetRequest(samUrl, "/register/user/v2/self/info", token)
    for {
      response <- request.send()
      content <- response.body                 leftMap responseToErrorResponse(response)
      json <- parse(content)                   leftMap circeErrorToErrorResponse
      userInfo <- json.as[SamUserInfoResponse] leftMap circeErrorToErrorResponse
    } yield userInfo
  }

  override def queryAction(samResource: String, action: String, token: String): AvramResult[Boolean] = {
    // Temporarily put sttp backend in implicit scope here instead of RestClient
    implicit val backend = catsSttpBackend

    val request = buildAuthenticatedGetRequest(samUrl, s"/api/resources/v1/entity-collection/$samResource/action/$action", token)
    for {
      response <- AvramResult.fromIO(request.send())
      content <- AvramResult.fromEither(response.body leftMap responseToErrorResponse(response))
    } yield content.toBoolean
  }

  private def responseToErrorResponse(response: Response[String]): String => ErrorResponse = error => ErrorResponse(response.code, error)
  private def circeErrorToErrorResponse(e: io.circe.Error): ErrorResponse = ErrorResponse(500, e.getMessage)

  // For the sake of comparison, this is what getUserStatus used to look like
  private def longhandResponseToEitherErrorOrUserInfo(response: Id[Response[String]]): Either[ErrorResponse, SamUserInfoResponse] = {
    response.body match {
      case Left(error: String) => Left(ErrorResponse(response.code, error))
      case Right(content: String) =>
        parse(content) match {
          case Left(error: ParsingFailure) => Left(ErrorResponse(500, error.message))
          case Right(json: Json) =>
            val result: Result[SamUserInfoResponse] = json.as[SamUserInfoResponse]
            result match {
              case Left(error: DecodingFailure) => Left(ErrorResponse(500, error.message))
              case Right(userInfo: SamUserInfoResponse) => Right(userInfo)
            }
        }
    }
  }
}
