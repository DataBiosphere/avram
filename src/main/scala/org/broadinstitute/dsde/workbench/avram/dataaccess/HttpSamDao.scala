package org.broadinstitute.dsde.workbench.avram.dataaccess

import java.util.logging.Logger

import mouse.all._
import cats.implicits._
import io.circe.generic.auto._
import io.circe.parser._
import org.broadinstitute.dsde.workbench.avram._
import org.broadinstitute.dsde.workbench.avram.util.{AvramException, RestClient}

class HttpSamDao(samUrl: String) extends SamDao with RestClient {
  private val log: Logger = Logger.getLogger(getClass.getName)

  override def getUserStatus(token: String): AvramResult[SamUserInfoResponse] = {
    val request = buildAuthenticatedGetRequest(samUrl, "/register/user/v2/self/info", token)
    for {
      response <- request.send()                        |> ioToResult
      content  <- response.body.leftMap(msg =>
                    AvramException(response.code, msg)) |> eitherToResult
      json     <- parse(content)                        |> eitherToResult
      userInfo <- json.as[SamUserInfoResponse]          |> eitherToResult
    } yield userInfo
  }
}
