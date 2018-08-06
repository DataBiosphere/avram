package org.broadinstitute.dsde.workbench.avram.integration

import java.sql.{Connection, PreparedStatement, ResultSet}

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

    "using JDBC" ignore {
      val db = Database.forConfig("postgres")
      try {
        val conn: Connection = Database.forConfig("postgres").source.createConnection()
        val statement: PreparedStatement = conn.prepareStatement("select now()")
        val resultSet: ResultSet = statement.executeQuery()
        resultSet.next()
        val result = resultSet.getString(1)
        println(s"from java: $result")
      } finally db.close
    }
  }
}
