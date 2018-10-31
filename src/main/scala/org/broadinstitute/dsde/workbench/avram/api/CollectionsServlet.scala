package org.broadinstitute.dsde.workbench.avram.api

import java.util.UUID

import io.circe.generic.auto._
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import org.broadinstitute.dsde.workbench.avram.model.{AvramException, SamResource}
import org.broadinstitute.dsde.workbench.avram.service.CollectionsService
import org.broadinstitute.dsde.workbench.avram.util.AvramResult

import scala.util.Try


class CollectionsServlet extends HttpServlet with AvramServlet {

  private val collectionsService = new CollectionsService()

  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    handleAuthenticatedRequest(request, response) { _ =>
      for {
        collectionExternalId <- extractCollectionId(request)
        collection <- collectionsService.getCollection(collectionExternalId)
      } yield collection
    }
  }

  override def doPost(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    handleAuthenticatedRequest(request, response) { userInfo =>
      for {
        samResource <- extractSamResource(request)
        collection <- collectionsService.createCollection(samResource, userInfo.userEmail)
      } yield collection
    }
  }

  private def extractCollectionId(request: HttpServletRequest): AvramResult[UUID] = {
    for {
      param <- extractPathInfo(request, "Missing collection ID path parameter")
      uuid <- AvramResult.fromTry(Try(UUID.fromString(param)), AvramException(HttpServletResponse.SC_BAD_REQUEST, "Malformed collection ID"))
    } yield uuid
  }

  private def extractSamResource(request: HttpServletRequest): AvramResult[SamResource] = {
    for {
      param <- extractPathInfo(request, "Missing sam resource ID path parameter")
    } yield SamResource(param)
  }

  private def extractPathInfo(request: HttpServletRequest, errorMessage: String): AvramResult[String] = {
    for {
      pathInfo <- AvramResult.fromOption(Option(request.getPathInfo), AvramException(HttpServletResponse.SC_BAD_REQUEST, errorMessage))
    } yield pathInfo.stripPrefix("/")
  }
}
