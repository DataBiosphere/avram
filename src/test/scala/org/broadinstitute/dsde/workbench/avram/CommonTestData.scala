package org.broadinstitute.dsde.workbench.avram


import com.typesafe.config.ConfigFactory
import io.circe.Json
import org.broadinstitute.dsde.workbench.avram.config.DbcpDataSourceConfig
import org.broadinstitute.dsde.workbench.avram.db.DbReference
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Values common to multiple tests, to reduce boilerplate.
  */
object CommonTestData {

  /**
    * At the time of writing, this mirrors the values in src/test/resources/app.conf. It may not be
    * necessary to duplicate them here, but I wanted to illustrate that test configuration can live
    * in code as long as we control the instance lifecycle of the module being tested (i.e., not
    * @Api-annotated endpoints which are managed by the Google Cloud Endpoints servlet). The
    * benefits of config-in-code are less indirection and improved clarity.
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

  val configFactory = ConfigFactory.parseResources("app.conf").withFallback(ConfigFactory.load())
  private val dbcpDataSourceConfig = configFactory.as[DbcpDataSourceConfig]("dbcpDataSource")
  val localDatabase = new DbReference(dbcpDataSourceConfig)

  val collectionName = "collection1"
  val samResource = "samResource1"
  val user1 = "user1"

  val entityName = "entity1"
  val entityBody1 = Json.fromFields(List(("key1", Json.fromString("value1")), ("key2", Json.fromInt(1))))

}