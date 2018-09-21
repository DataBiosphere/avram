package org.broadinstitute.dsde.workbench.avram.config

import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

/**
  * Loads all configuration and provides config objects for services.
  */
object AvramConfig {
  private val configFactory: Config = ConfigFactory.parseResources("app.conf").withFallback(ConfigFactory.load())

  val dbcpDataSourceConfig: DbcpDataSourceConfig = configFactory.as[DbcpDataSourceConfig]("dbcpDataSource")
}
