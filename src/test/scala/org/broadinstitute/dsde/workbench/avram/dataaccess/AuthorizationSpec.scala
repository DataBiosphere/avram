package org.broadinstitute.dsde.workbench.avram.dataaccess

import com.google.api.server.spi.response.ForbiddenException
import org.broadinstitute.dsde.workbench.avram.Avram
import org.broadinstitute.dsde.workbench.avram.api.{AvramRoutes, FakeHttpServletRequest, Pong}
import org.broadinstitute.dsde.workbench.avram.db.DbReference
import org.broadinstitute.dsde.workbench.avram.util.AvramResult.AvramResult
import org.broadinstitute.dsde.workbench.avram.util.MockitoSafety
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.{FreeSpec, Matchers}

class AuthorizationSpec extends FreeSpec with Matchers with MockitoSafety {

  val mockSamDao: SamDao = mock[SamDao]

  // Only testing authorization. Assume user is always authenticated.
  when(mockSamDao.getUserStatus(any[String])).thenReturn(Right(SamUserInfoResponse("123", "test@dummy.org", enabled = true)))

  // Avram environment with a mock SamDao
  object testAvram extends Avram {
    override def database: DbReference = Avram.database
    override def samDao: SamDao = mockSamDao
  }

  val api = new AvramRoutes(testAvram)

  "authorization test endpoint" - {
    "should return 401 if user is unauthorized" in {
      val token = "test-token"
      val request = FakeHttpServletRequest(headers = Map("Authorization" -> List(s"Bearer $token")))

      when(mockSamDao.queryAction("avram", "ping", token)).thenReturn(AvramResult(Right(false)))

      an [ForbiddenException] should be thrownBy api.authzPing(request, "avram", "ping")
    }

    "should return a ping if user is authorized" in {
      val token = "test-token"

      when(mockSamDao.queryAction("avram", "ping", token)).thenReturn(AvramResult(Right(true)))

      val request = FakeHttpServletRequest(headers = Map("Authorization" -> List(s"Bearer $token")))
      api.authzPing(request, "avram", "ping") shouldEqual Pong()
    }
  }
}
