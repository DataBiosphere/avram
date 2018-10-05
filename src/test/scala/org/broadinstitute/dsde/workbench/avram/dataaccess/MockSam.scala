package org.broadinstitute.dsde.workbench.avram.dataaccess

import io.circe.generic.auto._
import io.circe.syntax._
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.Header
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.NottableString.not
import org.scalatest.{BeforeAndAfterEach, Suite}

trait MockSam extends BeforeAndAfterEach { self: Suite =>

  class MockSam(port: Int) extends ClientAndServer(port) {
    def addValidAuthentication(token: String, subjectId: String, email: String): Unit = {
      mockSam.when(request()
        .withMethod("GET")
        .withPath("/register/user/v2/self/info")
        .withHeader(new Header("Authorization", s"Bearer $token")))
        .respond(response(SamUserInfoResponse(subjectId, email, enabled = true).asJson.toString))
    }

    def baseUrl: String = s"http://localhost:$port"
  }

  def samPort: Int
  var mockSam: MockSam = _

  override def beforeEach() {
    mockSam = new MockSam(samPort)

    // 401 on missing authorization header
    mockSam.when(request()
      .withMethod("GET")
      .withPath("/register/user/v2/self/info")
      .withHeaders(new Header(not("Authorization")))
    ).respond(response("Unauthorized").withStatusCode(401))

    // 401 on missing bearer token
    mockSam.when(request()
      .withMethod("GET")
      .withPath("/register/user/v2/self/info")
      .withHeader(new Header("Authorization", "Bearer"))
    ).respond(response("Unauthorized").withStatusCode(401))

    super.beforeEach()
  }

  override def afterEach() {
    try super.afterEach()
    finally mockSam.stop()
  }
}
