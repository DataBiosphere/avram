package org.broadinstitute.dsde.workbench.avram.model

import java.time.Instant
import java.util.UUID


case class AvramException(status: Int, message: String) extends Throwable


case class Collection(externalId: UUID,
                      samResource: SamResource,
                      createdBy: String,
                      createdTimestamp: Instant,
                      updatedBy: String,
                      updatedTimestamp: Instant)

case class DbPoolStats(numActive: Int, numIdle: Int, totalConnections: Int)

case class Entity(externalId: UUID,
                  externalCollectionId: UUID,
                  entityBody: String,
                  createdBy: String,
                  createdTimestamp: Instant,
                  updatedBy: String,
                  updatedTimestamp: Instant)

case class SamResource(resourceName: String)

case class Status(databaseStatus: String) // add other dependencies as we need them -- TODO: add Sam status

