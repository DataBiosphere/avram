package org.broadinstitute.dsde.workbench.avram.api

import com.google.api.server.spi.response.UnauthorizedException
import org.broadinstitute.dsde.workbench.avram.dataaccess.MockSam
import org.scalatest.{FreeSpec, Matchers}

class AuthenticationSpec extends FreeSpec with Matchers with MockSam {

  override def samPort = 9999

  //TODO - Change this once we decide on how to mock out our tests or potentially just remove this
//  val api = new AvramRoutes
//
//  "authorized ping" - {
//    "returns 401 when auth token is not given" in {
//      val r = new FakeHttpServletRequest()
//      an [UnauthorizedException] should be thrownBy api.authPing(r)
//    }
//
//    "pongs when auth token is valid" in {
//      val token = "test"
//      mockSam.addValidAuthentication(token, "123", "test@dummy.org")
//
//      val r = new FakeHttpServletRequest(headers = Map("Authorization" -> List(s"Bearer $token")))
//      api.authPing(r) shouldEqual Pong()
//    }
//  }
}
