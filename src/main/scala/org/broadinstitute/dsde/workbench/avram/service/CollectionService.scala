package org.broadinstitute.dsde.workbench.avram.service

import java.util.UUID

import org.broadinstitute.dsde.workbench.avram.model.Collection
import java.time.Instant

object CollectionService {

  def getCollection(id: String): Collection = {

    return Collection(UUID.fromString("C1"), "SamResource", "andrea", Instant.now, "andrea", Instant.now)

  }

}
