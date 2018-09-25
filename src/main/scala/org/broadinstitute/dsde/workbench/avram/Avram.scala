package org.broadinstitute.dsde.workbench.avram

import org.apache.commons.dbcp2.BasicDataSource
import org.broadinstitute.dsde.workbench.avram.config.AvramConfig
import org.broadinstitute.dsde.workbench.avram.dataaccess.{HttpSamDao, SamDao}
import org.broadinstitute.dsde.workbench.avram.util.SlickDatabaseFactory

/**
  * Object providing access to all services. This merges configuration and service code to provide
  * one-stop access for endpoint implementations.
  *
  * Service clients:
  * All code for which some form of dependency injection isn't possible should look here for
  * services instead of manually reading configs and creating service instances.
  *
  * Service implementations:
  * There are many possible techniques for providing access such as:
  *   - a val that references a singleton instance (instance must be thread-safe)
  *   - a def that creates new instances
  *   - a loan function that wraps lifecycle management around a client-provided function
  * The choice of technique depends on factors such as service thread-safety, instantiation cost,
  * and resource limits.
  *
  * The technique should force (or at least encourage) correct usage. When feasible, it should be
  * impossible for clients to misuse this access API. For example, while clients can't be stopped
  * from holding on to references longer than they should, they can be encouraged to ask for new
  * instances by providing a factory method that requires some appropriately scoped context data.
  *
  * The need for this is a consequence of Google Cloud Endpoints Framework controlling instance
  * creation of the API implementation objects. However, introducing Guice
  * (https://cloud.google.com/endpoints/docs/frameworks/java/using-guice) might give that control
  * back to us and might be worth exploring.
  */
object Avram {
  private val databaseFactory = new SlickDatabaseFactory(AvramConfig.dbcpDataSourceConfig)

  val database = databaseFactory.database
  val samDao: SamDao = new HttpSamDao(AvramConfig.sam.baseUrl)

  /**
    * DBCP data source provided only for introspection into the database pool statistics. If you're
    * not working with database monitoring, don't use this!
    */
  val dbcpDataSource: BasicDataSource = databaseFactory.dbcpDataSource
}