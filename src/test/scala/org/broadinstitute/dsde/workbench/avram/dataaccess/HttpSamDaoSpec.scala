package org.broadinstitute.dsde.workbench.avram.dataaccess

import org.broadinstitute.dsde.workbench.avram.AvramResultSupport
import org.broadinstitute.dsde.workbench.avram.model.AvramException
import org.mockserver.model.HttpResponse.response
import org.scalatest.{FreeSpec, Matchers}

/**
  * Test cases for making requests to Sam and correctly returning responses to Avram.
  *
  * This only tests that HttpSamDao can correctly handle the possible result _types_ (JSON, errors,
  * etc.) from Sam, not every possible result. Once we know that Avram knows how to communicate with
  * Sam, we can feed interesting test cases to Avram ourselves without using a mock server.
  */
class HttpSamDaoSpec extends FreeSpec with Matchers with AvramResultSupport with MockSam {
  override def samPort = 9999
  lazy val samDao: SamDao = new HttpSamDao(mockSam.baseUrl)

  "HttpSamDao" - {
    "getUserStatus" - {
      "should handle 401 response when authorization fails" in {
        val token = "test-token"
        mockSam.when(buildUserStatusRequest(token)).respond(response("Sam says go away").withStatusCode(401))

        // Sam response body is logged, not passed through to the Avram response
        the [AvramException] thrownBy unsafeRun(samDao.getUserStatus(token)) should have (
          'status (401),
          'message ("Unauthorized")
        )
      }

      "should handle user info response when authentication succeeds" in {
        val token = "test-token"
        val subjectId = "123"
        val email = "test@dummy.org"
        mockSam.when(buildUserStatusRequest(token)).respond(buildUserStatusResponse(subjectId, email))

        unsafeRun(samDao.getUserStatus(token)) shouldEqual SamUserInfoResponse(subjectId, email, enabled = true)
      }
    }

    "queryAction" - {
      "should query sam and translate the response to a boolean" in {
        val request = buildQueryActionRequest("sam-resource-123", "read", "token")
        mockSam.when(request).respond(response("true"))
        val result = samDao.queryAction("sam-resource-123", "read", "token")
        unsafeRun(result) shouldEqual true
        mockSam.verify(request)
      }
    }
  }
}