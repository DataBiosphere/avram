package org.broadinstitute.dsde.workbench.avram.db

import org.scalatest.concurrent.ScalaFutures
import org.scalatest._

trait DatabaseWipe extends TestSuiteMixin with ScalaFutures with AvramComponent { self: TestSuite =>

  def wipeDatabase(): Unit = {
    DbSingleton.ref.inTransaction(_ => DbSingleton.ref.dataAccess.truncateAll()).futureValue
  }

  abstract override protected def withFixture(testCode: NoArgTest): Outcome = {
    try {
      wipeDatabase()
      super.withFixture(testCode)
    } catch {
      case t: Throwable => t.printStackTrace(); throw t
    } finally {
      wipeDatabase()
    }
  }
}
