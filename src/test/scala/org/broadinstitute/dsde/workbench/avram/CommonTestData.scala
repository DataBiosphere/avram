package org.broadinstitute.dsde.workbench.avram

import org.broadinstitute.dsde.workbench.avram.config.DbcpDataSourceConfig
import org.broadinstitute.dsde.workbench.avram.util.SlickDatabaseFactory

// values common to multiple tests, to reduce boilerplate

object CommonTestData {

  /**
    * At the time of writing, this mirrors the values in src/test/resources/app.conf. It may not be
    * necessary to duplicate them here, but I wanted to illustrate test configuration can live in
    * code as long as we control the instance lifecycle of the module being tested (i.e., not @Api
    * annotated endpoints which are managed by the Google Cloud Endpoints servlet).
    */
  val localDataSourceConfig = DbcpDataSourceConfig(
    driverClassName = "org.postgresql.Driver",
    url = "jdbc:postgresql://127.0.0.1:5432/testdb",
    username = "avram",
    password = "test",
    maxTotal = 20,
    slickNumThreads = 10,
    slickQueueSize = 1000
  )

  val localDatabase = new SlickDatabaseFactory(localDataSourceConfig).database

  //val entity1 = ...

}
