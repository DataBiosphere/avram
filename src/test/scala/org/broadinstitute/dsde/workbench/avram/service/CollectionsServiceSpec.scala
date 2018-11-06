package org.broadinstitute.dsde.workbench.avram.service

import org.broadinstitute.dsde.workbench.avram.dataaccess.SamDao
import org.broadinstitute.dsde.workbench.avram.{Avram, AvramResultSupport, UserInfo}
import org.broadinstitute.dsde.workbench.avram.db.{DatabaseWipe, TestComponent}
import org.broadinstitute.dsde.workbench.avram.model.SamResource
import org.broadinstitute.dsde.workbench.avram.util.AvramResult
import org.mockito.Mockito._
import org.scalatest.FlatSpecLike
import org.scalatest.mockito.MockitoSugar

class CollectionsServiceSpec extends TestComponent with FlatSpecLike with DatabaseWipe with AvramResultSupport with MockitoSugar {

  def fixture(testCode: (CollectionsService, SamDao) => Any): Any = {
    val mockSamDao = mock[SamDao]
    val collectionsService = new CollectionsService(mockSamDao)
    testCode(collectionsService, mockSamDao)
  }

  "CollectionsService" should "create and get a collection" in fixture { (collectionsService, mockSam) =>
    val samResource = SamResource("samResourceTest")
    val testUser = UserInfo("12345", "test@user.org", enabled = true, "abc")

    // create a collection
    when(mockSam.queryAction(samResource, "write", testUser.token))
      .thenReturn(AvramResult.pure(true))
    val createdResult = unsafeRun(collectionsService.createCollection(samResource, testUser))
    createdResult.createdBy shouldBe testUser.email
    createdResult.samResource shouldBe samResource

    // get the collection
    when(mockSam.queryAction(samResource, "read", testUser.token))
      .thenReturn(AvramResult.pure(true))
    val getResult = unsafeRun(collectionsService.getCollection(createdResult.externalId, testUser))
    getResult shouldBe createdResult
  }
}
