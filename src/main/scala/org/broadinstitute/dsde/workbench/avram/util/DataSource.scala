package org.broadinstitute.dsde.workbench.avram.util

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.dbcp2.BasicDataSource
import slick.jdbc.PostgresProfile.api._

import scala.util.Try

object DataSource {
  private val config = ConfigFactory.load().getConfig("dbcpDataSource")


  // See https://commons.apache.org/proper/commons-dbcp/configuration.html for configuration options and defaults
  val ds = new BasicDataSource()
  ds.setDriverClassName(config.getString("driverClassName"))
  ds.setUrl(config.getString("url"))
  ds.setUsername(config.getString("username"))
  ds.setPassword(config.getString("password"))
  ds.setMaxTotal(config.getInt("maxTotal"))

  val database = Database.forDataSource(ds, Option(ds.getMaxTotal), AsyncExecutor("Avram Executor", config.getInt("slick.numThreads"), config.getInt("slick.queueSize")))
}
