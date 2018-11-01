package org.broadinstitute.dsde.workbench.avram.dataaccess

import org.broadinstitute.dsde.workbench.avram.model.AvramException
import org.broadinstitute.dsde.workbench.avram.util.AvramResult

trait SamDao {
  def getUserStatus(token: String): AvramResult[SamUserInfoResponse]
  def queryAction(samResource: String, action: String, token: String): AvramResult[Boolean]
}

case class SamUserInfoResponse(userSubjectId: String, userEmail: String, enabled: Boolean)
