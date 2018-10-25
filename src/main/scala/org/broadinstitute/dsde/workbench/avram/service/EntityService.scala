package org.broadinstitute.dsde.workbench.avram.service

import java.time.Instant
import java.util.UUID

import org.broadinstitute.dsde.workbench.avram.model.Entity

class EntityService {

  def getEntity(collectionId: UUID, entityId: UUID): Entity = {
    new Entity(UUID.fromString("8e56d95f-8326-42ce-81f4-909624095ad2"), UUID.fromString("816284a9-cca7-4bd4-808f-c430c5bb697e"), "json", "andrea", Instant.now, "andrea", Instant.now)
  }

}
