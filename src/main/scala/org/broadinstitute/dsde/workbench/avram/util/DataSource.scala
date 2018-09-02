package org.broadinstitute.dsde.workbench.avram.util

import org.apache.commons.dbcp2.BasicDataSource
import org.broadinstitute.dsde.workbench.avram.config.DbcpDataSourceConfig
import slick.jdbc.PostgresProfile.api._


class DataSource(val config: DbcpDataSourceConfig) {
  // See https://commons.apache.org/proper/commons-dbcp/configuration.html for configuration options and defaults
  val ds = new BasicDataSource()
  ds.setDriverClassName(config.driverClassName)
  ds.setUrl(config.url)
  ds.setUsername(config.username)
  ds.setPassword(config.password)
  ds.setMaxTotal(config.maxTotal)

  val database = Database.forDataSource(ds, Option(ds.getMaxTotal), AsyncExecutor("Avram Executor", config.slickNumThreads, config.slickQueueSize))
}
