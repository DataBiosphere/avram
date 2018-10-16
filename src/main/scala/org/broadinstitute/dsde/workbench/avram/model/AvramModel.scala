package org.broadinstitute.dsde.workbench.avram.model

import java.time.Instant
import java.util.UUID

import scala.beans.BeanProperty



case class Collection(@BeanProperty externalId: UUID,
                      @BeanProperty samResource: String,
                      @BeanProperty createdBy: String,
                      @BeanProperty createdTimestamp: Instant,
                      @BeanProperty updatedBy: String,
                      @BeanProperty updatedTimestamp: Instant)

case class Entity(@BeanProperty externalId: UUID,
                  @BeanProperty externalCollectionId: UUID,
                  @BeanProperty entityBody: String,
                  @BeanProperty createdBy: String,
                  @BeanProperty createdTimestamp: Instant,
                  @BeanProperty updatedBy: String,
                  @BeanProperty updatedTimestamp: Instant)

case class Status(@BeanProperty databaseStatus: String) // add other dependencies as we need them -- TODO: add Sam status

case class DbPoolStats(@BeanProperty numActive: Int, @BeanProperty numIdle: Int, @BeanProperty totalConnections: Int)