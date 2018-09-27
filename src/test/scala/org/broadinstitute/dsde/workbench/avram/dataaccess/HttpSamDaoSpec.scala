package org.broadinstitute.dsde.workbench.avram.dataaccess

import io.circe.generic.auto._
import io.circe.syntax._
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.Header
import org.mockserver.model.HttpRequest._
import org.mockserver.model.HttpResponse._
import org.mockserver.model.NottableString.not
import org.scalatest.{BeforeAndAfter, FreeSpec, Matchers}

class HttpSamDaoSpec extends FreeSpec with BeforeAndAfter with Matchers {

  val samPort = 9999
  val samDao = new HttpSamDao(s"http://localhost:$samPort")
  var mockSam: MockSam = _

  before { mockSam = new MockSam(samPort) }
  after { mockSam.stop() }

  "HttpSamDao" - {
    "should return 401 error when token is empty" in {
      samDao.getUserStatus("") shouldEqual Left(ErrorResponse(401, "Unauthorized"))
    }

    "should return valid user response for authenticated user" in {
      val userInfo = SamUserInfoResponse("123", "test@dummy.org", enabled = true)
      mockSam.when(request()
        .withMethod("GET")
        .withPath("/register/user/v2/self/info")
        .withHeader(new Header("Authorization", "Bearer test")))
        .respond(response(userInfo.asJson.toString))

      samDao.getUserStatus("test") shouldEqual Right(userInfo)
    }
  }
}

class MockSam(port: Integer) extends ClientAndServer(port) {

  // 401 on missing authorization header
  when(request()
    .withMethod("GET")
    .withPath("/register/user/v2/self/info")
    .withHeaders(new Header(not("Authorization")))
  ).respond(response("Unauthorized").withStatusCode(401))

  // 401 on missing bearer token
  when(request()
    .withMethod("GET")
    .withPath("/register/user/v2/self/info")
    .withHeader(new Header("Authorization", "Bearer"))
  ).respond(response("Unauthorized").withStatusCode(401))
}
