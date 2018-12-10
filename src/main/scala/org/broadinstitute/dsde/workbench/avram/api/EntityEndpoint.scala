package org.broadinstitute.dsde.workbench.avram.api

import javax.ws.rs.Path

import org.broadinstitute.dsde.workbench.avram.Avram

@Path("/api/entities/v1")
class EntityEndpoint(avram: Avram) extends AvramEndpoint(avram) {
  def this() = this(Avram)

  private val entityService = avram.entityService

}
