package org.broadinstitute.dsde.workbench.avram

import org.broadinstitute.dsde.workbench.avram.dataaccess.SamUserInfoResponse

case class UserInfo(subjectId: String, email: String, enabled: Boolean, token: String)

object UserInfo {
  def apply(samUserInfo: SamUserInfoResponse, token: String): UserInfo =
    UserInfo(
      samUserInfo.userSubjectId,
      samUserInfo.userEmail,
      samUserInfo.enabled,
      token)
}