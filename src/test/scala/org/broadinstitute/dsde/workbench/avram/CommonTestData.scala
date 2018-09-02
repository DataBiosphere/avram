package org.broadinstitute.dsde.workbench.avram

import java.time.Instant
import java.util.UUID
import java.util.logging.Logger

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import org.broadinstitute.dsde.workbench.avram.config.DbcpDataSourceConfig
import org.broadinstitute.dsde.workbench.avram.util.DataSource
import org.broadinstitute.dsde.workbench.model.{UserInfo, WorkbenchEmail, WorkbenchUserId}
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.ExecutionContext

// values common to multiple tests, to reduce boilerplate

trait CommonTestData { this: ScalaFutures =>

  val configFactory = ConfigFactory.parseResources("app.conf").withFallback(ConfigFactory.load())
  private val dbcpDataSourceConfig = configFactory.as[DbcpDataSourceConfig]("dbcpDataSource")
  val dataSource = new DataSource(dbcpDataSourceConfig)


  //val entity1 = ...

}


