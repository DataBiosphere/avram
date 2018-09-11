package org.broadinstitute.dsde.workbench.avram

import com.typesafe.config.ConfigFactory
import org.broadinstitute.dsde.workbench.avram.config.DbcpDataSourceConfig
import org.broadinstitute.dsde.workbench.avram.util.DataSourceFactory
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

// values common to multiple tests, to reduce boilerplate

trait CommonTestData {

  val configFactory = ConfigFactory.parseResources("app.conf").withFallback(ConfigFactory.load())
  private val dbcpDataSourceConfig = configFactory.as[DbcpDataSourceConfig]("dbcpDataSource")
  val dataSource = new DataSourceFactory(dbcpDataSourceConfig)

  //val entity1 = ...

}


