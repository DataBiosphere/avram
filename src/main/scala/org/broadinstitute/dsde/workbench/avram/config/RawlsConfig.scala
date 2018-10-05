package org.broadinstitute.dsde.workbench.avram.config

import net.ceedubs.ficus.readers.ValueReader

case class RawlsConfig(baseUrl: String) {
  implicit val rawlsConfigReader: ValueReader[RawlsConfig] = ValueReader.relative { config =>
    RawlsConfig(config.getString("baseUrl"))
  }
}
