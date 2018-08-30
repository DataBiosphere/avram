package org.broadinstitute.dsde.workbench.avram


import java.io.File

import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ValueReader
package object config {
  implicit val swaggerReader: ValueReader[GoogleConfig] = ValueReader.relative { config =>
    GoogleConfig(
      config.getString("googleClientId"),
      config.getString("realm")
    )
  }
}
