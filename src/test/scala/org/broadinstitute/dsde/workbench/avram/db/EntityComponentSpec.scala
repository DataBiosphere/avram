package org.broadinstitute.dsde.workbench.avram.db


import java.util.UUID

import org.broadinstitute.dsde.workbench.avram.CommonTestData
import org.scalatest.FlatSpecLike

class EntityComponentSpec extends TestComponent with FlatSpecLike with DatabaseWipe {

  "EntityComponent" should "save an entity" in {
    // save a collection
    val externalCollectionId = UUID.randomUUID()
    dbFutureValue { _.collectionQuery.save(externalCollectionId, CommonTestData.samResource, CommonTestData.user1) }

    // save an entity
    val externalEntityId = UUID.randomUUID()
    dbFutureValue { _.entityQuery.save(externalEntityId, externalCollectionId, CommonTestData.user1, CommonTestData.entityBody1) }

    // get the saved entity
    val saveResult = dbFutureValue { _.entityQuery.getEntityByExternalId(externalEntityId, externalCollectionId) }.get

    saveResult.externalId shouldEqual externalEntityId
    saveResult.externalCollectionId shouldEqual externalCollectionId
    saveResult.createdBy shouldEqual CommonTestData.user1
    saveResult.entityBody shouldEqual CommonTestData.entityBody1.noSpaces
  }

}