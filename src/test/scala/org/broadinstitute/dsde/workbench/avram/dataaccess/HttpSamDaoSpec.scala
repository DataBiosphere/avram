package org.broadinstitute.dsde.workbench.avram.dataaccess

import com.google.api.server.spi.ServiceException
import io.circe.generic.auto._
import io.circe.syntax._
import org.broadinstitute.dsde.workbench.avram.CommonTestData
import org.broadinstitute.dsde.workbench.avram.dependencies.{AvramDependencies, Global}
import org.broadinstitute.dsde.workbench.avram.util.AvramError
import org.broadinstitute.dsde.workbench.avram.util.transformers._
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.Header
import org.mockserver.model.HttpRequest._
import org.mockserver.model.HttpResponse._
import org.mockserver.model.NottableString.not
import org.scalatest.{BeforeAndAfter, FreeSpec, Matchers}

class HttpSamDaoSpec extends FreeSpec with BeforeAndAfter with Matchers with CommonTestData {

  val samPort = 9999
  val samDao = new HttpSamDao(s"http://localhost:$samPort")
  var mockSam: MockSam = _
  val testDependencies = AvramDependencies(dataSource.database, dataSource.dbcpDataSource, samDao)

  before { mockSam = new MockSam(samPort) }
  after { mockSam.stop() }

  "HttpSamDao" - {
    "should return 401 error when token is empty" in {
      val thrown = the [ServiceException] thrownBy unsafeRun(samDao.getUserStatus(""), testDependencies)
      thrown.getStatusCode shouldEqual 401
      thrown.getMessage shouldEqual "Unauthorized"
    }

    "should return valid user response for authenticated user" in {
      val userInfo = SamUserInfoResponse("123", "test@dummy.org", enabled = true)
      mockSam.when(request()
        .withMethod("GET")
        .withPath("/register/user/v2/self/info")
        .withHeader(new Header("Authorization", "Bearer test")))
        .respond(response(userInfo.asJson.toString))

      unsafeRun(samDao.getUserStatus("test")) shouldEqual userInfo
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
