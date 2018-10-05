package org.broadinstitute.dsde.workbench.avram.util

import java.net.URI
import java.util.logging.Logger

import com.softwaremill.sttp.{HttpURLConnectionBackend, Request, Response, Uri, sttp}

/**
  * Helpful stuff for making rest calls using sttp (https://github.com/softwaremill/sttp).
  */
trait RestClient {
  private val log: Logger = Logger.getLogger(getClass.getName)

  implicit def sttpBackend = HttpURLConnectionBackend() // TODO: consider refactoring to allow use of SttpBackendStub in tests

  def responseToErrorResponse(response: Response[String]): String => ErrorResponse = error => ErrorResponse(response.code, error)
  def circeErrorToErrorResponse(e: io.circe.Error): ErrorResponse = ErrorResponse(500, e.getMessage)


  def buildAuthenticatedGetRequest(url: String, path: String, token: String): Request[String, Nothing] = {
    sttp.auth.bearer(token).get(buildUri(url, path))
  }

  def buildUri(url: String, path: String): Uri = {
    (url.stripSuffix("/"), path.stripPrefix("/")) match {
      case (u, p) =>
        // Make a java.net.URI because using the Uri interpolator causes the path to be URL-encoded
        Uri(new URI(s"$u/$p"))
    }
  }
}
