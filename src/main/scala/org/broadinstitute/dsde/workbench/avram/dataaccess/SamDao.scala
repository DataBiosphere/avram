package org.broadinstitute.dsde.workbench.avram.dataaccess

import org.broadinstitute.dsde.workbench.avram.util.ErrorResponse

case class UserInfo(userSubjectId: String, userEmail: String)

trait SamDao {
  def getUserStatus(token: String): Either[ErrorResponse, UserInfo]
}
