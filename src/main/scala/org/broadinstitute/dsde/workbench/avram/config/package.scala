package org.broadinstitute.dsde.workbench.avram


import net.ceedubs.ficus.readers.ValueReader

package object config {
  implicit val avramReader: ValueReader[AvramConfig] = ValueReader.relative { config =>
    AvramConfig(
      config.getString("serviceVersion"),
      config.getString("clientId"),
      config.getString("googleProject")
    )
  }
}
