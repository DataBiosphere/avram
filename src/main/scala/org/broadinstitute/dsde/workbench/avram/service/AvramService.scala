package org.broadinstitute.dsde.workbench.avram.service

import java.util.logging.Logger

import org.broadinstitute.dsde.workbench.avram.Avram


trait AvramService {
  private val log = Logger.getLogger(getClass.getName)
  val database = Avram.database
}
