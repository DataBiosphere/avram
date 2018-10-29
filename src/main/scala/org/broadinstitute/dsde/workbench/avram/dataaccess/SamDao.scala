package org.broadinstitute.dsde.workbench.avram.dataaccess

import org.broadinstitute.dsde.workbench.avram.model.AvramException

trait SamDao {
  def getUserStatus(token: String): Either[AvramException, SamUserInfoResponse]
}

case class SamUserInfoResponse(userSubjectId: String, userEmail: String, enabled: Boolean)
