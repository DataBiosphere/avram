package org.broadinstitute.dsde.workbench.avram.util

import org.apache.commons.dbcp2.BasicDataSource
import org.broadinstitute.dsde.workbench.avram.config.DbcpDataSourceConfig
import slick.jdbc.PostgresProfile.api._

/**
  * Provides access to a slick Database object using the provided configuration.
  *
  * @param dataSourceConfig database and connection pool configuration
  */
class SlickDatabaseFactory(dataSourceConfig: DbcpDataSourceConfig) {

  def makeDbcpDataSource: BasicDataSource = {
    // See https://commons.apache.org/proper/commons-dbcp/configuration.html for configuration options and defaults
    val ds = new BasicDataSource()
    ds.setDriverClassName(dataSourceConfig.driverClassName)
    ds.setUrl(dataSourceConfig.url)
    ds.setUsername(dataSourceConfig.username)
    ds.setPassword(dataSourceConfig.password)
    ds.setMaxTotal(dataSourceConfig.maxTotal)
    ds
  }

  val dbcpDataSource: BasicDataSource = makeDbcpDataSource
  val database: Database = Database.forDataSource(dbcpDataSource, Option(dbcpDataSource.getMaxTotal), AsyncExecutor("Avram Executor", dataSourceConfig.slickNumThreads, dataSourceConfig.slickQueueSize))
}
