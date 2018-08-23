package org.broadinstitute.dsde.workbench.avram.integration

import org.broadinstitute.dsde.workbench.avram.util.DataSource
import org.scalatest.FreeSpec
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class PostgreSQLSpec extends FreeSpec {

  "should fetch" - {
    "using slick" in {
      val db = DataSource.database
      try {
        val action: DBIO[Seq[String]] = sql"select now()".as[String]
        val value: Seq[String] = Await.result(db.run(action), Duration.Inf)
        value foreach { v => println(s"from slick: $v") }
      } finally db.close
    }
  }
}
