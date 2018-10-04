package org.broadinstitute.dsde.workbench.avram.db

import slick.jdbc.PostgresProfile
import com.github.tminglei.slickpg._
import slick.basic.Capability
import slick.jdbc.JdbcCapabilities

trait AvramPostgresProfile extends PostgresProfile with PgJsonSupport with PgCirceJsonSupport {
  override def pgjson = "jsonb"

  // Add back `capabilities.insertOrUpdate` to enable native `upsert` support; for postgres 9.5+
  override protected def computeCapabilities: Set[Capability] =
    super.computeCapabilities +  JdbcCapabilities.insertOrUpdate

  override val api = AvramPostgresAPI

  object AvramPostgresAPI extends API with JsonImplicits
}

object AvramPostgresProfile extends AvramPostgresProfile