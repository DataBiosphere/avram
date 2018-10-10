package org.broadinstitute.dsde.workbench.avram.db


import org.broadinstitute.dsde.workbench.avram.CommonTestData
import org.broadinstitute.dsde.workbench.avram.api.Collection
import org.scalatest.FlatSpecLike

class EntityComponentSpec extends TestComponent with FlatSpecLike {

  "EntityComponent" should "save an entity" in isolatedDbTest {
    // save a collection
    dbFutureValue { _.collectionQuery.save(CommonTestData.collectionName, CommonTestData.samResource, CommonTestData.user1) }

    // get that collection's id
    val collectionId =  dbFutureValue { _.collectionQuery.getCollectionIdByName(CommonTestData.collectionName) }.get

    // save an entity
    dbFutureValue { _.entityQuery.save(CommonTestData.entityName, collectionId, CommonTestData.user1, CommonTestData.entityBody1) }

    // get the saved entity
    val saveResult = dbFutureValue { _.entityQuery.getEntityByName(CommonTestData.entityName, collectionId) }.get

    saveResult.name shouldEqual CommonTestData.entityName
    saveResult.collection shouldEqual collectionId
    saveResult.createdBy shouldEqual CommonTestData.user1
    saveResult.entityBody shouldEqual CommonTestData.entityBody1.noSpaces
  }

}