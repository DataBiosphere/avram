package org.broadinstitute.dsde.workbench.avram.api

/**
  * Contains the client IDs and scopes for allowed clients consuming the helloworld API.
  */
object Constants {
  val WEB_CLIENT_ID = "908863053157-du7labuo25ljnguh9kbgp67dfk8po828.apps.googleusercontent.com"
//  val ANDROID_CLIENT_ID = "replace this with your Android client ID"
//  val IOS_CLIENT_ID = "replace this with your iOS client ID"
  val ANDROID_AUDIENCE: String = WEB_CLIENT_ID
  val EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email"
}
