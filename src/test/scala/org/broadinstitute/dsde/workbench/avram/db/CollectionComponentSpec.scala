package org.broadinstitute.dsde.workbench.avram.db

import java.util.UUID

import org.broadinstitute.dsde.workbench.avram.CommonTestData
import org.scalatest.FlatSpecLike

class CollectionComponentSpec extends TestComponent with FlatSpecLike {

  "CollectionComponent" should "save, get and delete collections" in isolatedDbTest {
    val externalId = UUID.randomUUID()
    dbFutureValue { _.collectionQuery.save(externalId, CommonTestData.samResource, CommonTestData.user1) }

    val saveResult = dbFutureValue { _.collectionQuery.getCollectionByExternalId(externalId) }.get

    saveResult.externalId shouldEqual externalId
    saveResult.samResource shouldEqual CommonTestData.samResource
    saveResult.createdBy shouldEqual CommonTestData.user1

    dbFutureValue { _.collectionQuery.deleteCollectionByExternalId(externalId)}

    val deleteResult = dbFutureValue { _.collectionQuery.getCollectionByExternalId(externalId) }

    deleteResult shouldEqual None
  }
}