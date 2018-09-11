package org.broadinstitute.dsde.workbench.avram.util

import org.apache.commons.dbcp2.BasicDataSource
import org.broadinstitute.dsde.workbench.avram.config.DbcpDataSourceConfig
import slick.jdbc.PostgresProfile.api._


class DataSourceFactory(dataSourceConfig: DbcpDataSourceConfig) {

  // See https://commons.apache.org/proper/commons-dbcp/configuration.html for configuration options and defaults
  val ds = new BasicDataSource()
  ds.setDriverClassName(dataSourceConfig.driverClassName)
  ds.setUrl(dataSourceConfig.url)
  ds.setUsername(dataSourceConfig.username)
  ds.setPassword(dataSourceConfig.password)
  ds.setMaxTotal(dataSourceConfig.maxTotal)

  val database = Database.forDataSource(ds, Option(ds.getMaxTotal), AsyncExecutor("Avram Executor", dataSourceConfig.slickNumThreads, dataSourceConfig.slickQueueSize))
}
