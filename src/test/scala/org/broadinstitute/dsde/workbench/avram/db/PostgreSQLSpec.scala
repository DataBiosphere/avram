package org.broadinstitute.dsde.workbench.avram.db

import org.broadinstitute.dsde.workbench.avram.CommonTestData
import org.scalatest.FreeSpec
import AvramPostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class PostgreSQLSpec extends FreeSpec {

  val database = CommonTestData.dataSource.database

  "should fetch" - {
    "using slick" in {
      val action: DBIO[Seq[String]] = sql"select now()".as[String]
      val value: Seq[String] = Await.result(database.run(action), Duration.Inf)
      value foreach { v => println(s"from slick: $v") }
    }
  }
}
