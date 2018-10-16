package org.broadinstitute.dsde.workbench.avram.db

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import org.broadinstitute.dsde.workbench.avram.TestExecutionContext
import org.broadinstitute.dsde.workbench.avram.config.DbcpDataSourceConfig


// initialize database tables and connection pool only once
object DbSingleton {
  import TestExecutionContext.testExecutionContext

  val configFactory = ConfigFactory.parseResources("app.conf").withFallback(ConfigFactory.load())
  private val dbcpDataSourceConfig = configFactory.as[DbcpDataSourceConfig]("dbcpDataSource")
  val ref: DbReference = DbReference(dbcpDataSourceConfig)
}
