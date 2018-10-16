package org.broadinstitute.dsde.workbench.avram.db

import org.apache.commons.dbcp2.BasicDataSource
import org.broadinstitute.dsde.workbench.avram.config.DbcpDataSourceConfig
import slick.dbio.DBIO
import slick.jdbc.{JdbcProfile, TransactionIsolation}

import scala.concurrent.Future
import AvramPostgresProfile.api._

case class DbReference(private val dataSourceConfig: DbcpDataSourceConfig) {

  val dataAccess = new DataAccess(AvramPostgresProfile)

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

  private val database = Database.forDataSource(dbcpDataSource, Option(dbcpDataSource.getMaxTotal), AsyncExecutor("Avram Executor", dataSourceConfig.slickNumThreads, dataSourceConfig.slickQueueSize))

  def inTransaction[T](f: (DataAccess) => DBIO[T]): Future[T] = {
    database.run(f(dataAccess).transactionally)
  }
}

class DataAccess(val profile: JdbcProfile) extends AllComponents {

  def truncateAll(): DBIO[Int] = {

    // important to keep the right order for referential integrity !
    // if table X has a Foreign Key to table Y, delete table X first
    TableQuery[EntityTable].delete andThen TableQuery[CollectionTable].delete
  }

  def sqlDBStatus() = {
    sql"select version()".as[String]
  }

  def sqlDbFetchTimestamp() = {
    sql"select now()".as[String]
  }

  def dbTotalConnections() = {
    sql"select count(*) from pg_stat_activity where pid <> pg_backend_pid() and usename = current_user".as[Int]
  }
}