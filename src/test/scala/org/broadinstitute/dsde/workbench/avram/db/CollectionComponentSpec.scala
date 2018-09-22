package org.broadinstitute.dsde.workbench.avram.db


import org.broadinstitute.dsde.workbench.avram.CommonTestData
import org.broadinstitute.dsde.workbench.avram.api.Collection
import org.scalatest.FlatSpecLike

class ClusterComponentSpec extends TestComponent with FlatSpecLike with CommonTestData {

  "CollectionComponent" should "save" in isolatedDbTest {

    val collection = Collection("collection1", "samResource1")
    dbFutureValue { _.collectionQuery.save(collection.name, collection.samResource) }

    val result = dbFutureValue { _.collectionQuery.getCollectionByName("collection1") }
    result shouldEqual Some(collection)

  }
}