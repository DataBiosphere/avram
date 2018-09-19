package org.broadinstitute.dsde.workbench.avram.dataaccess

import org.broadinstitute.dsde.workbench.avram.util.ErrorResponse

trait SamDao {
  def getUserStatus(token: String): Either[ErrorResponse, SamUserInfoResponse]
}

case class SamUserInfoResponse(userSubjectId: String, userEmail: String, enabled: Boolean)
