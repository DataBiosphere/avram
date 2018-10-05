package org.broadinstitute.dsde.workbench.avram.dataaccess

import org.broadinstitute.dsde.workbench.avram.util.ErrorResponse
import org.scalatest.{FreeSpec, Matchers}

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