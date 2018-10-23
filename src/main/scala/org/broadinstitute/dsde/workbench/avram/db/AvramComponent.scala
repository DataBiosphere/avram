package org.broadinstitute.dsde.workbench.avram.db

import java.sql.Timestamp
import java.time.Instant

import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

trait AvramComponent {
  val profile: JdbcProfile
}