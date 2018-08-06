package org.broadinstitute.dsde.workbench.avram.api

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.api.server.spi.config.{Api, ApiMethod}
import com.typesafe.config.ConfigFactory
import org.broadinstitute.dsde.workbench.avram.util.Logger

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import slick.jdbc.PostgresProfile.api._

import scala.beans.BeanProperty

case class Pong()
case class Now(@BeanProperty message: String)

@Api(name = "avram", version = "v1", scopes = Array("https://www.googleapis.com/auth/userinfo.email"), clientIds = Array(""), audiences = Array(""))
class AvramRoutes {
  val logger = new Logger

  @ApiMethod(name = "ping", httpMethod = "get", path = "ping")
  def ping: Pong = {
    Pong()
  }

  @ApiMethod(name = "now", httpMethod = "get", path = "now")
  def now: Now = {
    // Explicitly use a Future to make sure the implicit ExecutionContext is being used
    Now(Await.result(Future(fetchTimestampFromDBWithSlick()), Duration.Inf))
  }

  private def fetchTimestampFromDBWithSlick(): String = {
    // Should be using a connection pool instead of creating a connection for every request, but this works as a proof-of-concept
    val db = Database.forConfig("postgres")
    val now = Await.result(db.run(sql"select now()".as[String]), Duration.Inf)
    now.head
  }

  private def fetchTimestampFromDBWithJDBC(): String = {
    val config = ConfigFactory.load().getConfig("postgres")
    val conn: Connection = DriverManager.getConnection(config.getString("url"), config.getString("user"), config.getString("password"))
    val statement: PreparedStatement = conn.prepareStatement("select now()")
    val resultSet: ResultSet = statement.executeQuery()
    resultSet.next()
    resultSet.getString(1)
  }
}
