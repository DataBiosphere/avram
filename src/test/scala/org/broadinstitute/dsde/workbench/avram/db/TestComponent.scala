package org.broadinstitute.dsde.workbench.avram.db

import org.broadinstitute.dsde.workbench.avram.TestExecutionContext
import org.broadinstitute.dsde.workbench.model.google.ServiceAccountKeyId
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

trait TestComponent extends Matchers with ScalaFutures with AvramComponent {
  override val profile: JdbcProfile = DbSingleton.ref.dataAccess.profile
  implicit val executionContext: ExecutionContext = TestExecutionContext.testExecutionContext
  implicit override val patienceConfig = PatienceConfig(timeout = scaled(Span(10, Seconds)))
  val defaultServiceAccountKeyId = ServiceAccountKeyId("123")

  def dbFutureValue[T](f: DataAccess => DBIO[T]): T = DbSingleton.ref.inTransaction(f).futureValue
  def dbFailure[T](f: DataAccess => DBIO[T]): Throwable = DbSingleton.ref.inTransaction(f).failed.futureValue
}
