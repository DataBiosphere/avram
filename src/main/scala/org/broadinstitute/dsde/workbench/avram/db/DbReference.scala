package org.broadinstitute.dsde.workbench.avram.db

import org.apache.commons.dbcp2.BasicDataSource
import org.broadinstitute.dsde.workbench.avram.config.DbcpDataSourceConfig
import slick.dbio.DBIO
import slick.jdbc.{JdbcProfile, TransactionIsolation}
import scala.concurrent.{ExecutionContext, Future}

import AvramPostgresProfile.api._

case class DbReference(private val dataSourceConfig: DbcpDataSourceConfig)(implicit val executionContext: ExecutionContext) {

  val dataAccess = new DataAccess(AvramPostgresProfile)

  val ds = new BasicDataSource()
  ds.setDriverClassName(dataSourceConfig.driverClassName)
  ds.setUrl(dataSourceConfig.url)
  ds.setUsername(dataSourceConfig.username)
  ds.setPassword(dataSourceConfig.password)
  ds.setMaxTotal(dataSourceConfig.maxTotal)

  val database = Database.forDataSource(ds, Option(ds.getMaxTotal), AsyncExecutor("Avram Executor", dataSourceConfig.slickNumThreads, dataSourceConfig.slickQueueSize))

  def inTransaction[T](f: (DataAccess) => DBIO[T], isolationLevel: TransactionIsolation = TransactionIsolation.RepeatableRead): Future[T] = {
    import dataAccess.profile.api._
    database.run(f(dataAccess).transactionally.withTransactionIsolation(isolationLevel))
  }
}

class DataAccess(val profile: JdbcProfile)(implicit val executionContext: ExecutionContext) extends AllComponents {

  def truncateAll(): DBIO[Int] = {
    import profile.api._

    // important to keep the right order for referential integrity !
    // if table X has a Foreign Key to table Y, delete table X first
    TableQuery[EntityTable].delete andThen TableQuery[CollectionTable].delete
  }

  def sqlDBStatus() = {
    import profile.api._

    sql"select version()".as[String]
  }
}