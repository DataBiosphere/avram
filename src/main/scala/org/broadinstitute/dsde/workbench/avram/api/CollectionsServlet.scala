package org.broadinstitute.dsde.workbench.avram.api

import io.circe.generic.auto._
import io.circe.syntax._
import java.util.UUID
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import org.broadinstitute.dsde.workbench.avram.model.{AvramException, SamResource}
import org.broadinstitute.dsde.workbench.avram.service.CollectionsService


class CollectionsServlet extends HttpServlet with AvramServlet {

  private val collectionsService = new CollectionsService()

  override def doGet(request: HttpServletRequest, response: HttpServletResponse) = {
    val collection = handleAuthenticatedRequest(request, response) { userInfo =>
      response.setContentType("application/json")
      val parameter = request.getPathInfo.stripPrefix("/")
      val collectionExternalId = UUID.fromString(parameter)
      val future = collectionsService.getCollection(collectionExternalId)
      registerOnComplete(future, request, response)
    }
    response.getWriter.write(collection.asJson.noSpaces)
  }

  override def doPost(request: HttpServletRequest, response: HttpServletResponse) = {
    val thing = handleAuthenticatedRequest(request, response) { userInfo =>
      response.setContentType("application/json")
      val parameter = request.getPathInfo.stripPrefix("/")
      val samResource = SamResource(parameter)
      val future = collectionsService.createCollection(samResource, userInfo.userEmail)
      registerOnComplete(future, request, response)
    }
    response.getWriter.write(thing.asJson.noSpaces)
  }


}
