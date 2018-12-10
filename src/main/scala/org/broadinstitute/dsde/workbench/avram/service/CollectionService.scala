package org.broadinstitute.dsde.workbench.avram.service

import java.util.UUID

import javax.servlet.http.HttpServletResponse
import org.broadinstitute.dsde.workbench.avram.UserInfo
import org.broadinstitute.dsde.workbench.avram.dataaccess.SamDao
import org.broadinstitute.dsde.workbench.avram.model.{AvramException, Collection, SamResource}
import org.broadinstitute.dsde.workbench.avram.util.AvramResult

import scala.concurrent.ExecutionContext


class CollectionService(val samDao: SamDao)(implicit executionContext: ExecutionContext) extends AvramService {

  def createCollection(samResource: SamResource, userInfo: UserInfo): AvramResult[Collection] = {
    for {
      authed <- checkAuthorization(samResource, "write", userInfo.token)
      collection <- saveCollection(samResource, userInfo)
    } yield collection
  }

  def getCollection(externalId: UUID, userInfo: UserInfo): AvramResult[Collection] = {
    for {
      collection <- fetchCollection(externalId)
      _ <- checkAuthorization(collection.samResource, "read", userInfo.token)
    } yield collection
  }

  def deleteCollection(externalId: UUID, userInfo: UserInfo): AvramResult[Unit] = {
    for {
      collection <- fetchCollection(externalId)
      result <- checkAuthorization(collection.samResource, "write", userInfo.token) //check whether user has delete OR write permission
      _ <- deleteCollectionInternal(externalId)
    } yield {()}
  }


  def patchCollection(externalId: UUID, newSamResource: SamResource, userInfo: UserInfo): AvramResult[Unit] = {
    for{
      collection <- fetchCollection(externalId)
       thing <- checkAuthorization(collection.samResource, "write", userInfo.token)
       _ <- changeCollectionSamResource(externalId, newSamResource)
    } yield {()}
  }



  private def changeCollectionSamResource(externalId: UUID, newSamResource: SamResource): AvramResult[Unit] = {
    for {
      _ <- AvramResult.fromFuture(database.inTransaction(_.collectionQuery.changeSamResource(externalId, newSamResource)))
    } yield {()}
  }

  private def deleteCollectionInternal(externalId: UUID) = {
    for {
      // add a check here later to delete any entities in the  collection (OR to throw if there are existing entities)
      _ <- AvramResult.fromFuture(database.inTransaction(_.collectionQuery.deleteCollectionByExternalId(externalId)))
    } yield {()}
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
