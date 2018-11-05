package org.broadinstitute.dsde.workbench.avram.service

import java.util.UUID

import javax.servlet.http.HttpServletResponse
import org.broadinstitute.dsde.workbench.avram.model.{AvramException, Collection, SamResource}
import org.broadinstitute.dsde.workbench.avram.util.AvramResult

import scala.concurrent.ExecutionContext


class CollectionsService(implicit executionContext: ExecutionContext) extends AvramService {

  def createCollection(samResource: SamResource, createdBy: String): AvramResult[Collection] = {
    val collectionExternalId = UUID.randomUUID()
    AvramResult.fromFuture(database.inTransaction(_.collectionQuery.save(collectionExternalId, samResource, createdBy)))
  }

  def getCollection(externalId: UUID): AvramResult[Collection] = {
    log.severe("externalId: " + externalId)
    for {
      result <- AvramResult.fromFuture(database.inTransaction(_.collectionQuery.getCollectionByExternalId(externalId)))
      collection <- AvramResult.fromOption(result, AvramException(HttpServletResponse.SC_NOT_FOUND, s"Collection ${externalId.toString} not found"))
    } yield collection
  }

}
