package org.broadinstitute.dsde.workbench.avram.api

import com.google.api.server.spi.response.UnauthorizedException
import io.circe.generic.auto._
import io.circe.syntax._
import org.broadinstitute.dsde.workbench.avram.dataaccess.SamUserInfoResponse
import org.mockserver.client.MockServerClient
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.Header
import org.mockserver.model.HttpRequest._
import org.mockserver.model.HttpResponse._
import org.scalatest.{BeforeAndAfter, FreeSpec, Matchers}

class AuthenticationSpec extends FreeSpec with BeforeAndAfter with Matchers {

  val api = new AvramRoutes
  var mockSam: MockServerClient = _

  before {
    mockSam = new ClientAndServer(9999)
  }

  after {
    mockSam.stop()
  }

  "authorized ping" - {
    "returns 401 when auth token is not given" in {
      val r = new FakeHttpServletRequest()
      an [UnauthorizedException] should be thrownBy api.authPing(r)
    }

    "pongs when auth token is valid" in {
      mockSam.when(request()
        .withMethod("GET")
        .withPath("/register/user/v2/self/info")
        .withHeader(new Header("Authorization", "Bearer test")))
        .respond(response(SamUserInfoResponse("123", "test@dummy.org", enabled = true).asJson.toString))

      val r = new FakeHttpServletRequest(headers = Map("Authorization" -> List("Bearer test")))
      api.authPing(r) shouldEqual Pong()
    }
  }
}
