package org.broadinstitute.dsde.workbench.avram.api

import java.util.UUID

import io.circe.generic.auto._
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import org.broadinstitute.dsde.workbench.avram.model.SamResource
import org.broadinstitute.dsde.workbench.avram.service.CollectionsService


class CollectionsServlet extends HttpServlet with AvramServlet {

  private val collectionsService = new CollectionsService()

  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    handleAuthenticatedRequest(request, response) { _ =>
      val collectionExternalId = UUID.fromString(request.getPathInfo.stripPrefix("/"))
      collectionsService.getCollection(collectionExternalId)
    }
  }

  override def doPost(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    handleAuthenticatedRequest(request, response) { userInfo =>
      val samResource = SamResource(request.getPathInfo.stripPrefix("/"))
      collectionsService.createCollection(samResource, userInfo.userEmail)
    }
  }
}
