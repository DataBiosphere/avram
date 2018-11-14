package org.broadinstitute.dsde.workbench.avram.api

import java.util.UUID

import io.circe.generic.auto._
import javax.ws.rs._
import javax.ws.rs.core.Response.Status
import javax.ws.rs.core.{HttpHeaders, Response}

import org.broadinstitute.dsde.workbench.avram.model.{AvramException, SamResource}
import org.broadinstitute.dsde.workbench.avram.util.AvramResult
import org.broadinstitute.dsde.workbench.avram.Avram

import scala.util.Try

@Path("/api/collections/v1")
class CollectionsEndpoint(avram: Avram) extends AvramEndpoint(avram) {
  def this() = this(Avram)

  private val collectionsService = avram.collectionsService

  @GET
  @Produces(Array("application/json"))
  @Path("/{externalId}")
  def getCollection(@HeaderParam(HttpHeaders.AUTHORIZATION) bearerToken: String, @PathParam("externalId") externalId: String): Response = {
    handleAuthenticatedRequest(bearerToken) { userInfo =>
      for {
        collectionExternalUUID <- AvramResult.fromTry(Try(UUID.fromString(externalId)), AvramException(Status.BAD_REQUEST.getStatusCode, "Malformed collection ID"))
        collection <- collectionsService.getCollection(collectionExternalUUID, userInfo)
      } yield collection
    }
  }

  @POST
  @Produces(Array("application/json"))
  @Path("/{samResource}")
  def postCollection(@HeaderParam(HttpHeaders.AUTHORIZATION) bearerToken: String, @PathParam("samResource") samResource: String): Response = {
    handleAuthenticatedRequest(bearerToken) { userInfo =>
      for {
        collection <- collectionsService.createCollection(SamResource(samResource), userInfo)
      } yield collection
    }
  }
}
