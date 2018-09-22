package org.broadinstitute.dsde.workbench.avram.db

import java.sql.Timestamp
import java.time.Instant

import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

trait AvramComponent {
  val profile: JdbcProfile
  implicit val executionContext: ExecutionContext

  protected final val dummyDate: Instant = Instant.ofEpochMilli(1000)
}