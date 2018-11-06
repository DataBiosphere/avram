package org.broadinstitute.dsde.workbench.avram.service

import java.util.UUID

import javax.servlet.http.HttpServletResponse
import org.broadinstitute.dsde.workbench.avram.UserInfo
import org.broadinstitute.dsde.workbench.avram.dataaccess.SamDao
import org.broadinstitute.dsde.workbench.avram.model.{AvramException, Collection, SamResource}
import org.broadinstitute.dsde.workbench.avram.util.AvramResult

import scala.concurrent.ExecutionContext


class CollectionsService(val samDao: SamDao)(implicit executionContext: ExecutionContext) extends AvramService {

  def createCollection(samResource: SamResource, createdBy: UserInfo): AvramResult[Collection] = {
    for {
      _ <- checkAuthorization(samResource, "write", createdBy.token)
      collection <- saveCollection(samResource, createdBy)
    } yield collection
/*
    ifAuthorized(samResource, "write", createdBy.token) {
      saveCollection(samResource, createdBy)
    }
*/
  }

  def getCollection(externalId: UUID, userInfo: UserInfo): AvramResult[Collection] = {
    for {
      collection <- fetchCollection(externalId)
      _ <- checkAuthorization(collection.samResource, "read", userInfo.token)
    } yield collection
/*
    for {
      collection <- fetchCollection(externalId)
      result <- ifAuthorized(collection.samResource, "read", userInfo.token) { AvramResult.pure(collection) }
    } yield result
*/
  }

  private def fetchCollection(externalId: UUID): AvramResult[Collection] = {
    for {
      result <- AvramResult.fromFuture(database.inTransaction(_.collectionQuery.getCollectionByExternalId(externalId)))
      collection <- AvramResult.fromOption(result, AvramException(HttpServletResponse.SC_NOT_FOUND, s"Collection ${externalId.toString} not found"))
    } yield collection
  }

  private def saveCollection(samResource: SamResource, createdBy: UserInfo): AvramResult[Collection] = {
    val collectionExternalId = UUID.randomUUID()
    AvramResult.fromFuture(database.inTransaction(_.collectionQuery.save(collectionExternalId, samResource, createdBy.email)))
  }
}
