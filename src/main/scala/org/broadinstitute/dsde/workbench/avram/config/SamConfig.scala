package org.broadinstitute.dsde.workbench.avram.config

import net.ceedubs.ficus.readers.ValueReader

case class SamConfig(baseUrl: String) {
  implicit val samConfigReader: ValueReader[SamConfig] = ValueReader.relative { config =>
    SamConfig(config.getString("baseUrl"))
  }
}
