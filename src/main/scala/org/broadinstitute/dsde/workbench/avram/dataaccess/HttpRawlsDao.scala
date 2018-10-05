package org.broadinstitute.dsde.workbench.avram.dataaccess

import java.util.logging.Logger

import cats._
import cats.implicits._
import com.softwaremill.sttp.{Id, _}
import io.circe.Decoder.Result
import io.circe.{DecodingFailure, Json, ParsingFailure}
import io.circe.generic.auto._
import io.circe.parser._
import org.broadinstitute.dsde.workbench.avram.util.{ErrorResponse, RestClient}

class HttpRawlsDao(rawlsUrl: String) extends RawlsDao with RestClient  {
  private val log: Logger = Logger.getLogger(getClass.getName)

  override def queryEntitiesOfType(workspaceNamespace: String, workspaceName: String, entityType: String, token: String): Either[ErrorResponse,EntityResponse] = {
    val request: Request[String, Nothing] = buildAuthenticatedGetRequest(rawlsUrl, s"/workspaces/$workspaceNamespace/$workspaceName/$entityType", token)
    for {
      response <- request.send()
      content <- response.body                 leftMap responseToErrorResponse(response)
      json <- parse(content)                   leftMap circeErrorToErrorResponse
      entities <- json.as[EntityResponse] leftMap circeErrorToErrorResponse
    } yield entities

  }
}
