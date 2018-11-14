package org.broadinstitute.dsde.workbench.avram.api

import io.circe.generic.auto._
import io.circe.parser._
import org.broadinstitute.dsde.workbench.avram.Avram
import org.broadinstitute.dsde.workbench.avram.dataaccess.{SamDao, SamUserInfoResponse}
import org.broadinstitute.dsde.workbench.avram.db.{DatabaseWipe, DbReference, TestComponent}
import org.broadinstitute.dsde.workbench.avram.model.{Collection, SamResource}
import org.broadinstitute.dsde.workbench.avram.service.CollectionsService
import org.broadinstitute.dsde.workbench.avram.util.AvramResult
import org.scalatest.FlatSpecLike
import org.scalatest.mockito.MockitoSugar
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._

class CollectionsEndpointSpec extends TestComponent with FlatSpecLike with DatabaseWipe with MockitoSugar {

  val mockSamDao: SamDao = mock[SamDao]
  val token = "ya.test-token"
  val authenticationHeader = s"Bearer $token"
  val subjectId = "123"
  val email = "test@dummy.org"

  object testAvram extends Avram {
    override def database: DbReference = Avram.database
    override def samDao: SamDao = mockSamDao
    override def collectionsService: CollectionsService = new CollectionsService(mockSamDao)
  }
  val collectionsEndpoint = new CollectionsEndpoint(testAvram)

  "CollectionsServlet" should "POST and GET a collection" in {
    val samResource = SamResource("samResourceTest")

    when(mockSamDao.getUserStatus(any[String])).thenReturn(AvramResult.pure(SamUserInfoResponse(subjectId, email, enabled = true)))

    // create collection
    when(mockSamDao.queryAction(samResource, "write", token)).thenReturn(AvramResult.pure(true))
    val createResponse = collectionsEndpoint.postCollection(authenticationHeader, samResource.resourceName)
    createResponse.getStatus should be (200)
    createResponse.hasEntity should be (true)

    // get the external Id of the created collection
    val collectionExternalId = decode[Collection](createResponse.getEntity.asInstanceOf[String])match {
      case Left(e) => throw e
      case Right(col) => col.externalId
    }

    // get collection
    when(mockSamDao.queryAction(samResource, "read", token)).thenReturn(AvramResult.pure(true))
    val getResponse = collectionsEndpoint.getCollection(authenticationHeader, collectionExternalId.toString)
    getResponse.getStatus should be (200)
    getResponse.getEntity should be (createResponse.getEntity)

  }
}
