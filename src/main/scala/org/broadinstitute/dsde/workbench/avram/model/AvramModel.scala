package org.broadinstitute.dsde.workbench.avram.model

import java.time.Instant
import java.util.logging.Logger

import org.broadinstitute.dsde.workbench.avram.dataaccess.SamUserInfoResponse
import org.broadinstitute.dsde.workbench.avram.util.ErrorResponse

import scala.beans.BeanProperty



case class Collection(@BeanProperty name: String,
                      @BeanProperty samResource: String,
                      @BeanProperty createdBy: String,
                      @BeanProperty createdTimestamp: Instant,
                      @BeanProperty updatedBy: String,
                      @BeanProperty updatedTimestamp: Instant)
case class Entity(@BeanProperty name: Option[String],
                  @BeanProperty collection: Long,
                  @BeanProperty entityBody: String,
                  @BeanProperty createdBy: String,
                  @BeanProperty createdTimestamp: Instant,
                  @BeanProperty updatedBy: String,
                  @BeanProperty updatedTimestamp: Instant)
case class Status(@BeanProperty databaseStatus: String) // add other dependencies as we need them -- TODO: add Sam status
case class DbPoolStats(@BeanProperty numActive: Int, @BeanProperty numIdle: Int, @BeanProperty totalConnections: Int)