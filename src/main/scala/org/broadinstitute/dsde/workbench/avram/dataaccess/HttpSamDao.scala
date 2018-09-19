package org.broadinstitute.dsde.workbench.avram.dataaccess

import java.util.logging.Logger

import com.softwaremill.sttp._
import io.circe.Decoder.Result
import io.circe.{DecodingFailure, Json, ParsingFailure}
import io.circe.generic.auto._
import io.circe.parser._
import org.broadinstitute.dsde.workbench.avram.util.{ErrorResponse, RestClient}

class HttpSamDao(samUrl: String) extends SamDao with RestClient {
  private val log: Logger = Logger.getLogger(getClass.getName)

  override def getUserStatus(token: String): Either[ErrorResponse, SamUserInfoResponse] = {
    val request = buildAuthenticatedGetRequest(samUrl, "/register/user/v2/self/info", token)
    val response: Id[Response[String]] = request.send()
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
