package org.broadinstitute.dsde.workbench.avram.integration

import slick.jdbc.PostgresProfile.api._
import org.scalatest.FreeSpec

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class PostgreSQLSpec extends FreeSpec {

  "should fetch" - {
    "using slick" in {
      val db = Database.forConfig("postgres")
      try {
        val action: DBIO[Seq[String]] = sql"select now()".as[String]
        val value: Seq[String] = Await.result(db.run(action), Duration.Inf)
        value foreach { v => println(s"from slick: $v") }
      } finally db.close
    }
  }
}
