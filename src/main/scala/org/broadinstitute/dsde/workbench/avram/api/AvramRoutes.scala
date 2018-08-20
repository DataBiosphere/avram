package org.broadinstitute.dsde.workbench.avram.api

import com.google.api.server.spi.config.{Api, ApiMethod}
import org.broadinstitute.dsde.workbench.avram.util.Logger


case class Pong()


@Api(name = "avram", version = "v1", scopes = Array("https://www.googleapis.com/auth/userinfo.email"))
class AvramRoutes {

  private val log = new Logger

  @ApiMethod(name = "ping", httpMethod = "get", path = "ping")
  def ping: Pong = {
    log.log("HIII")
    Pong()
  }
}
