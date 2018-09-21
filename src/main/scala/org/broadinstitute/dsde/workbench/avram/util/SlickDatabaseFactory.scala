package org.broadinstitute.dsde.workbench.avram.util

import com.zaxxer.hikari.HikariDataSource
import org.apache.commons.dbcp2.BasicDataSource
import org.broadinstitute.dsde.workbench.avram.config.DbcpDataSourceConfig
import slick.jdbc.PostgresProfile.api._

/**
  * Provides access to a slick Database object using the provided configuration.
  *
  * @param dataSourceConfig database and connection pool configuration
  */
class SlickDatabaseFactory(dataSourceConfig: DbcpDataSourceConfig) {

  // See https://commons.apache.org/proper/commons-dbcp/configuration.html for configuration options and defaults
  val dbcpDataSource = new BasicDataSource()
  dbcpDataSource.setDriverClassName(dataSourceConfig.driverClassName)
  dbcpDataSource.setUrl(dataSourceConfig.url)
  dbcpDataSource.setUsername(dataSourceConfig.username)
  dbcpDataSource.setPassword(dataSourceConfig.password)
  dbcpDataSource.setMaxTotal(dataSourceConfig.maxTotal)

  /**
    * Experimental code to use HikariCP. Currently exploring this with people at Google.
    */
  private def makeHikariDataSource: HikariDataSource = {
    import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

    def walkToRoot(t: ThreadGroup): ThreadGroup = Option(t.getParent) match {
      case Some(p) => walkToRoot(p)
      case None => t
    }

    val rootThreadGroup = walkToRoot(Thread.currentThread().getThreadGroup)

    val config = new HikariConfig
    config.setJdbcUrl(dataSourceConfig.url)
    config.setUsername(dataSourceConfig.username)
    config.setPassword(dataSourceConfig.password)
    config.setThreadFactory((r: Runnable) => new Thread(rootThreadGroup, r))
    new HikariDataSource(config)
  }

  val database = Database.forDataSource(dbcpDataSource, Option(dbcpDataSource.getMaxTotal), AsyncExecutor("Avram Executor", dataSourceConfig.slickNumThreads, dataSourceConfig.slickQueueSize))
}
