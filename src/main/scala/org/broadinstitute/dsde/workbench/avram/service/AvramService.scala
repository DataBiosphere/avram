package org.broadinstitute.dsde.workbench.avram.service

import java.util.logging.Logger

import javax.servlet.http.HttpServletResponse
import org.broadinstitute.dsde.workbench.avram.Avram
import org.broadinstitute.dsde.workbench.avram.dataaccess.SamDao
import org.broadinstitute.dsde.workbench.avram.model.{AvramException, SamResource}
import org.broadinstitute.dsde.workbench.avram.util.AvramResult


trait AvramService {
  val log = Logger.getLogger(getClass.getName)
  val database = Avram.database
  def samDao: SamDao

  def checkAuthorization(samResource: SamResource, action: String, token: String): AvramResult[Unit] = {
    for {
      authorized <- samDao.queryAction(samResource, action, token)
      result <- if (authorized) AvramResult.pure(()) else AvramResult.fromError[Unit](AvramException(HttpServletResponse.SC_FORBIDDEN, "Permission denied"))
    } yield result
  }
}
