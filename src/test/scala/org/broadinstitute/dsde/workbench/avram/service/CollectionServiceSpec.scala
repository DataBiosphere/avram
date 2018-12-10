package org.broadinstitute.dsde.workbench.avram.service

import java.util.UUID

import org.broadinstitute.dsde.workbench.avram.dataaccess.SamDao
import org.broadinstitute.dsde.workbench.avram.{Avram, AvramResultSupport, UserInfo}
import org.broadinstitute.dsde.workbench.avram.db.{DatabaseWipe, TestComponent}
import org.broadinstitute.dsde.workbench.avram.model.SamResource
import org.broadinstitute.dsde.workbench.avram.service.CollectionService
import org.broadinstitute.dsde.workbench.avram.util.AvramResult
import org.mockito.Mockito._
import org.scalatest.FlatSpecLike
import org.scalatest.mockito.MockitoSugar

class CollectionServiceSpec extends TestComponent with FlatSpecLike with DatabaseWipe with AvramResultSupport with MockitoSugar {

  def fixture(testCode: (CollectionService, SamDao) => Any): Any = {
    val mockSamDao = mock[SamDao]
    val collectionsService = new CollectionService(mockSamDao)
    testCode(collectionsService, mockSamDao)
  }

  val samResource = SamResource("samResourceTest")
  val testUser = UserInfo("12345", "test@user.org", enabled = true, "abc")

  def createCollection(collectionService: CollectionService, mockSam: SamDao) = {
    when(mockSam.queryAction(samResource, "write", testUser.token))
      .thenReturn(AvramResult.pure(true))
    unsafeRun(collectionService.createCollection(samResource, testUser))
  }

  def getCollection(externalId: UUID, collectionService: CollectionService, mockSam: SamDao) = {
    when(mockSam.queryAction(samResource, "read", testUser.token))
      .thenReturn(AvramResult.pure(true))
    unsafeRun(collectionService.getCollection(externalId, testUser))
  }

  "CollectionsService" should "create and get a collection" in fixture { (collectionService, mockSam) =>
    // create a collection
    val createdResult = createCollection(collectionService, mockSam)
    createdResult.createdBy shouldBe testUser.email
    createdResult.samResource shouldBe samResource

    // get the collection
    val getResult = getCollection(createdResult.externalId, collectionService, mockSam)
    getResult shouldBe createdResult
  }

  "CollectionsService" should "delete a collection when the user has write permission" in fixture { (collectionService, mockSam) =>
    // create a collection
    val createdResult = createCollection(collectionService, mockSam)

    // get the collection
    val getResult = getCollection(createdResult.externalId, collectionService, mockSam)
    getResult shouldBe createdResult

    // delete the collection
    when(mockSam.queryAction(samResource, "write", testUser.token))
      .thenReturn(AvramResult.pure(true))
    unsafeRun(collectionService.deleteCollection(createdResult.externalId, testUser))

    //ADD EXCEPTION CHECKING
    // make sure collection is deleted
    //val getDeletedResult = getCollection(createdResult.externalId, collectionService, mockSam)

  }
}
