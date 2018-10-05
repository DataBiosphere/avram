package org.broadinstitute.dsde.workbench.avram.dataaccess

import io.circe.Json
import org.broadinstitute.dsde.workbench.avram.util.ErrorResponse

import scala.beans.BeanProperty
import scala.concurrent.Future

trait RawlsDao {

//  case class Entity(
//                     name: String,
//                     entityType: String,
//                     attributes: Map)

//  case class EntityQuery(page: Int, pageSize: Int, filterTerms: Option[String])

//  case class EntityQueryResultMetadata(unfilteredCount: Int, filteredCount: Int, filteredPageCount: Int)

//  case class EntityQueryResponse(parameters: EntityQuery, resultMetadata: EntityQueryResultMetadata, results: Seq[Seq])

  def queryEntitiesOfType(workspaceNamespace: String, workspaceName: String, entityType: String, token: String): Either[ErrorResponse,EntityResponse]

//  def getEntityTypes(workspaceNamespace: String, workspaceName: String)(implicit userToken: UserInfo): Future[Map[String, EntityTypeMetadata]]
//
//  def fetchAllEntitiesOfType(workspaceNamespace: String, workspaceName: String, entityType: String)(implicit userToken: UserInfo): Future[Seq[Entity]]
}

case class EntityResponse(@BeanProperty results: List[Entity])

case class Entity(@BeanProperty name: String, @BeanProperty entityType: String, @BeanProperty attributes: Map[String, String])

