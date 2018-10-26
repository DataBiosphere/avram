package org.broadinstitute.dsde.workbench.avram.dataaccess

import org.broadinstitute.dsde.workbench.avram.util.AvramResult
import org.broadinstitute.dsde.workbench.avram.util.ErrorResponse

trait SamDao {
  def getUserStatus(token: String): Either[ErrorResponse, SamUserInfoResponse]
  def queryAction(samResource: String, action: String, token: String): AvramResult[Boolean]
}

case class SamUserInfoResponse(userSubjectId: String, userEmail: String, enabled: Boolean)
