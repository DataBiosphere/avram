package org.broadinstitute.dsde.workbench.avram.service

import java.util.UUID
import javax.servlet.http.HttpServletResponse

import org.broadinstitute.dsde.workbench.avram.model.{AvramException, Collection, SamResource}
import scala.concurrent.{ExecutionContext, Future}


class CollectionsService(implicit executionContext: ExecutionContext) extends AvramService {

  def createCollection(samResource: SamResource, createdBy: String): Future[Either[AvramException, Collection]] = {
    database.inTransaction { dataAccess =>
      val collectionExternalId = UUID.randomUUID()
      dataAccess.collectionQuery.save(collectionExternalId, samResource, createdBy)
    } map(Right(_))
  }

  def getCollection(externalId: UUID): Future[Either[AvramException, Collection]] = {
    database.inTransaction { dataAccess =>
      dataAccess.collectionQuery.getCollectionByExternalId(externalId)
    } map (_.toRight(AvramException(HttpServletResponse.SC_NOT_FOUND, s"Collection ${externalId.toString} not found")))
  }

}
