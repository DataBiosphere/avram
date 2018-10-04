package org.broadinstitute.dsde.workbench.avram.db

import org.broadinstitute.dsde.workbench.avram.CommonTestData
import org.broadinstitute.dsde.workbench.avram.api.Collection
import org.scalatest.FlatSpecLike

class ClusterComponentSpec extends TestComponent with FlatSpecLike {

  "CollectionComponent" should "save, get and delete collections" in isolatedDbTest {
    dbFutureValue { _.collectionQuery.save(CommonTestData.collectionName, CommonTestData.samResource, CommonTestData.user1) }

    val saveResult = dbFutureValue { _.collectionQuery.getCollectionByName(CommonTestData.collectionName) }.get

    saveResult.name shouldEqual CommonTestData.collectionName
    saveResult.samResource shouldEqual CommonTestData.samResource
    saveResult.createdBy shouldEqual CommonTestData.user1

    dbFutureValue { _.collectionQuery.deleteCollectionByName(CommonTestData.collectionName )}

    val deleteResult = dbFutureValue { _.collectionQuery.getCollectionByName(CommonTestData.collectionName) }

    deleteResult shouldEqual None
  }
}