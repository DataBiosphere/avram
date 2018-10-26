package org.broadinstitute.dsde.workbench.avram.dataaccess

import org.broadinstitute.dsde.workbench.avram.util.AvramResult.unsafeRun
import org.broadinstitute.dsde.workbench.avram.util.ErrorResponse
import org.mockserver.model.HttpResponse.response
import org.scalatest.{FreeSpec, Matchers}

/**
  * These tests rely on the current MockSam which I'd like to deprecate. Understanding the tests
  * requires understanding the behavior currently baked in to MockSam. While that behavior is pretty
  * simple right now, I think it will become too tempting to reimplement somewhat complex Sam
  * behavior in our mock. We should only be using a mock to test that Avram can make requests to Sam
  * and correctly decode responses.
  *
  * The test cases in here should be migrated to a spec that uses a simpler mock Sam.
  */
@deprecated("TODO: Migrate tests to NewHttpSamDaoSpec", "11/1/18")
class HttpSamDaoSpec extends FreeSpec with Matchers with MockSam {

  override def samPort = 9999
  lazy val samDao = new HttpSamDao(mockSam.baseUrl)

  "HttpSamDao" - {
    "should return 401 error when token is empty" in {
      samDao.getUserStatus("") shouldEqual Left(ErrorResponse(401, "Unauthorized"))
    }

    "should return valid user response for authenticated user" in {
      val token = "test"
      val subjectId = "123"
      val email = "test@dummy.org"
      mockSam.addValidAuthentication(token, subjectId, email)

      val expectedResponse = SamUserInfoResponse(subjectId, email, enabled = true)
      samDao.getUserStatus(token) shouldEqual Right(expectedResponse)
    }
  }
}

/**
  * Test cases for making requests to Sam and correctly returning responses to Avram.
  *
  * TODO: Migrate test cases from HttpSamDaoSpec to here, remove existing HttpSamDaoSpec, and rename NewHttpSamDaoSpec to HttpSamDaoSpec
  */
class NewHttpSamDaoSpec extends FreeSpec with Matchers with SimpleMockSam {
  override def samPort = 9999
  lazy val samDao: SamDao = new HttpSamDao(mockSam.baseUrl)

  "HttpSamDao" - {
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