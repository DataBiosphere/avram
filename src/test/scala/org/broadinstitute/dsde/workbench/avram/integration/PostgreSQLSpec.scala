package org.broadinstitute.dsde.workbench.avram.integration

import org.broadinstitute.dsde.workbench.avram.CommonTestData
import org.scalatest.FreeSpec
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class PostgreSQLSpec extends FreeSpec {

  val database = CommonTestData.localDatabase

  "should fetch" - {
    "using slick" in {
      val action: DBIO[Seq[String]] = sql"select now()".as[String]
      val value: Seq[String] = Await.result(database.run(action), Duration.Inf)
      value foreach { v => println(s"from slick: $v") }
    }
  }
}
