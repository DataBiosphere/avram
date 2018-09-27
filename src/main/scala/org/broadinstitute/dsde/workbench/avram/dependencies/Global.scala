package org.broadinstitute.dsde.workbench.avram.dependencies

import org.broadinstitute.dsde.workbench.avram.config.AvramConfig
import org.broadinstitute.dsde.workbench.avram.dataaccess.HttpSamDao
import org.broadinstitute.dsde.workbench.avram.util.SlickDatabaseFactory

object Global {
  private val databaseFactory = new SlickDatabaseFactory(AvramConfig.dbcpDataSourceConfig)

  val dependencies = AvramDependencies(
    databaseFactory.database,
    databaseFactory.dbcpDataSource,
    new HttpSamDao(AvramConfig.sam.baseUrl))
}
