package org.broadinstitute.dsde.workbench.avram.util

import java.net.URI
import java.util.logging.Logger

import cats.effect.IO
import com.softwaremill.sttp.asynchttpclient.cats.AsyncHttpClientCatsBackend
import com.softwaremill.sttp.{HttpURLConnectionBackend, Id, Request, SttpBackend, Uri, sttp}

/**
  * Helpful stuff for making rest calls using sttp (https://github.com/softwaremill/sttp).
  */
trait RestClient {
  private val log: Logger = Logger.getLogger(getClass.getName)

  implicit def catsSttpBackend: SttpBackend[IO, Nothing] = AsyncHttpClientCatsBackend[IO]()

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
