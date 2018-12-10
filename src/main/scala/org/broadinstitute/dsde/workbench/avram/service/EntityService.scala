package org.broadinstitute.dsde.workbench.avram.service

import org.broadinstitute.dsde.workbench.avram.dataaccess.SamDao

import scala.concurrent.ExecutionContext

class EntityService(val samDao: SamDao)(implicit executionContext: ExecutionContext) extends AvramService {


}
