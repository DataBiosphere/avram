package org.broadinstitute.dsde.workbench.avram.api

import java.util.UUID
import javax.servlet.http.{HttpServletResponse}

import io.circe.generic.auto._
import javax.ws.rs._
import javax.ws.rs.core.{HttpHeaders, Response}

import org.broadinstitute.dsde.workbench.avram.model.{AvramException, SamResource}
import org.broadinstitute.dsde.workbench.avram.service.CollectionsService
import org.broadinstitute.dsde.workbench.avram.util.AvramResult
import org.broadinstitute.dsde.workbench.avram.Avram

import scala.util.Try

@Path("/api/collections/v1")
class CollectionsServlet(avram: Avram) extends AvramServlet(avram) {
  def this() = this(Avram)

  private val collectionsService = new CollectionsService()

  @GET
  @Produces(Array("text/plain"))
  def getTest(): String = {
   "Yohoho it's a pirate's life for me."
  }

  @GET
  @Path("/{externalId}")
  def getCollection(@HeaderParam(HttpHeaders.AUTHORIZATION) bearerToken: String, @PathParam("externalId") externalId: String): Response = {
    handleAuthenticatedRequest(bearerToken) { _ =>
      for {
        collectionExternalUUID <- AvramResult.fromTry(Try(UUID.fromString(externalId)), AvramException(HttpServletResponse.SC_BAD_REQUEST, "Malformed collection ID"))
        collection <- collectionsService.getCollection(collectionExternalUUID)
      } yield collection
    }
  }

  @POST
  @Path("/{samResource}")
  def postCollection(@HeaderParam(HttpHeaders.AUTHORIZATION) bearerToken: String, @PathParam("samResource") samResource: String): Response = {
    handleAuthenticatedRequest(bearerToken) { userInfo =>
      for {
        collection <- collectionsService.createCollection(SamResource(samResource), userInfo.userEmail)
      } yield collection
    }
  }
}
