package org.broadinstitute.dsde.workbench.avram.dependencies

import org.apache.commons.dbcp2.BasicDataSource
import org.broadinstitute.dsde.workbench.avram.dataaccess.SamDao
import slick.jdbc.PostgresProfile.api._

case class AvramDependencies(database: Database,
                             dataSource: BasicDataSource,
                             samDAO: SamDao)
