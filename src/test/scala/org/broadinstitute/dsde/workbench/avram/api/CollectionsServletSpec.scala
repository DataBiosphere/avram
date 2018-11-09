package org.broadinstitute.dsde.workbench.avram.api

import io.circe.generic.auto._
import io.circe.parser._
import org.broadinstitute.dsde.workbench.avram.Avram
import org.broadinstitute.dsde.workbench.avram.dataaccess.{SamDao, SamUserInfoResponse}
import org.broadinstitute.dsde.workbench.avram.db.{DbReference, TestComponent}
import org.broadinstitute.dsde.workbench.avram.model.{Collection, SamResource}
import org.broadinstitute.dsde.workbench.avram.util.AvramResult
import org.scalatest.FlatSpecLike
import org.scalatest.mockito.MockitoSugar
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._

class CollectionsEndpointSpec extends TestComponent with FlatSpecLike with MockitoSugar {

  val mockSamDao: SamDao = mock[SamDao]
  val token = "Bearer ya.test-token"
  val subjectId = "123"
  val email = "test@dummy.org"
  when(mockSamDao.getUserStatus(any[String])).thenReturn(AvramResult.pure((SamUserInfoResponse(subjectId, email, enabled = true))))

  object testAvram extends Avram {
    override def database: DbReference = Avram.database
    override def samDao: SamDao = mockSamDao
  }
  val collectionsEndpoint = new CollectionsEndpoint(testAvram)

  "CollectionsServlet" should "POST and GET a collection" in isolatedDbTest {

    // create collection
    val samResource = SamResource("samResourceTest")
    val createResponse = collectionsEndpoint.postCollection(token, samResource.resourceName)
    createResponse.getStatus should be (200)
    createResponse.hasEntity should be (true)

    // get the external Id of the created collection
    val collectionExternalId = decode[Collection](createResponse.getEntity.asInstanceOf[String])match {
      case Left(e) => throw e
      case Right(col) => col.externalId
    }

    // get collection
    val getResponse = collectionsEndpoint.getCollection(token, collectionExternalId.toString)
    getResponse.getStatus should be (200)
    getResponse.getEntity should be (createResponse.getEntity)

  }
}
