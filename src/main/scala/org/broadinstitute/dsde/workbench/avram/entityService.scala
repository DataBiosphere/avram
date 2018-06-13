package org.broadinstitute.dsde.workbench

import org.broadinstitute.dsde.workbench.model.ErrorReportSource

package object avram {
  implicit val errorReportSource = ErrorReportSource("avram")
}