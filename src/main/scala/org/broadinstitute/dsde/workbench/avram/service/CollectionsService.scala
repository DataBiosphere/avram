package org.broadinstitute.dsde.workbench.avram.service

import java.util.UUID

import org.broadinstitute.dsde.workbench.avram.api.BaseEndpoint
import org.broadinstitute.dsde.workbench.avram.model.{Collection, SamResource}
import org.broadinstitute.dsde.workbench.model.WorkbenchException

//import scala.concurrent.ExecutionContext


class CollectionsService extends BaseEndpoint {

  def createCollection(samResource: SamResource, createdBy: String): Collection = {
    inTransaction { dataAccess =>
      val collectionExternalId = UUID.randomUUID()
      dataAccess.collectionQuery.save(collectionExternalId, samResource, createdBy)
    }
  }

  def getCollection(externalId: UUID): Collection = {
    inTransaction{ dataAccess =>
      dataAccess.collectionQuery.getCollectionByExternalId(externalId)
    } match {
      case Some(collection) => collection
      case None => throw new WorkbenchException("Collection doesn't exist")
    }
  }

}
