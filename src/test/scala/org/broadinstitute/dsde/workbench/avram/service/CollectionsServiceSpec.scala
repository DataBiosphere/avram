package org.broadinstitute.dsde.workbench.avram.service

import org.broadinstitute.dsde.workbench.avram.db.TestComponent
import org.broadinstitute.dsde.workbench.avram.model.SamResource
import org.scalatest.FlatSpecLike

class CollectionsServiceSpec extends TestComponent with FlatSpecLike {

  val collectionsService = new CollectionsService()

  "CollectionsService" should "create and get a collection" in isolatedDbTest {
    val samResource = SamResource("samResourceTest")
    val testUser = "testUser1"

    // create a collection
    val createdResult = collectionsService.createCollection(samResource, testUser).futureValue.toOption.get
    createdResult.createdBy shouldBe testUser
    createdResult.samResource shouldBe samResource

    // get the collection
    val getResult = collectionsService.getCollection(createdResult.externalId).futureValue.toOption.get
    getResult shouldBe createdResult

  }
}
