package org.broadinstitute.dsde.workbench.avram.dataaccess

import org.broadinstitute.dsde.workbench.avram.AvramResult

trait SamDao {
  def getUserStatus(token: String): AvramResult[SamUserInfoResponse]
}

case class SamUserInfoResponse(userSubjectId: String, userEmail: String, enabled: Boolean)
