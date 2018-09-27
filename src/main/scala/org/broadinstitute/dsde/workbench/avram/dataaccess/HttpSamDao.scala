package org.broadinstitute.dsde.workbench.avram.dataaccess

import java.util.logging.Logger

import mouse.all._
import cats.implicits._
import io.circe.{DecodingFailure, Json, ParsingFailure}
import io.circe.generic.auto._
import io.circe.parser._
import org.broadinstitute.dsde.workbench.avram.util.transformers._
import org.broadinstitute.dsde.workbench.avram.util.{AvramError, RestClient}

class HttpSamDao(samUrl: String) extends SamDao with RestClient {
  private val log: Logger = Logger.getLogger(getClass.getName)

  override def getUserStatus(token: String): AvramResult[SamUserInfoResponse] = {
    val request = buildAuthenticatedGetRequest(samUrl, "/register/user/v2/self/info", token)
    for {
      response <- request.send()                        |> ioToResult
      content  <- response.body.leftMap(msg =>
                    AvramError(response.code, msg))     |> eitherToResult[AvramError, String]
      json     <- parse(content)                        |> eitherToResult[ParsingFailure, Json]
      userInfo <- json.as[SamUserInfoResponse]          |> eitherToResult[DecodingFailure, SamUserInfoResponse]
    } yield userInfo
  }
}
