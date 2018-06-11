package org.broadinstitute.dsde.workbench

import org.broadinstitute.dsde.workbench.model.ErrorReportSource

package object entityservice {
  implicit val errorReportSource = ErrorReportSource("entityService")
}