package org.broadinstitute.dsde.workbench.avram.api

import java.sql.Timestamp
import com.typesafe.config.ConfigFactory

import io.circe.Json
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import org.broadinstitute.dsde.workbench.avram.config.DbcpDataSourceConfig
import org.broadinstitute.dsde.workbench.avram.db.DbReference
import java.util.logging.Logger

import com.google.api.server.spi.config.{Api, ApiMethod}
import javax.servlet.http.HttpServletRequest
import org.broadinstitute.dsde.workbench.avram.Avram
import org.broadinstitute.dsde.workbench.avram.dataaccess.SamUserInfoResponse
import org.broadinstitute.dsde.workbench.avram.util.ErrorResponse

import scala.beans.BeanProperty
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.concurrent.{Await, Future}


case class Pong()
case class Now(@BeanProperty message: String)
case class Collection(@BeanProperty name: String,
                      @BeanProperty samResource: String,
                      @BeanProperty createdBy: String,
                      @BeanProperty createdTimestamp: Timestamp,
                      @BeanProperty updatedBy: Option[String],
                      @BeanProperty updatedTimestamp: Timestamp)
case class Entity(@BeanProperty name: String,
                  @BeanProperty collection: Long,
                  @BeanProperty createdBy: String,
                  @BeanProperty createdTimestamp: Timestamp,
                  @BeanProperty updatedBy: Option[String],
                  @BeanProperty updatedTimestamp: Timestamp,
                  @BeanProperty entityBody: String)
case class DbPoolStats(@BeanProperty numActive: Int, @BeanProperty numIdle: Int, @BeanProperty totalConnections: Int)

/**
  * Illustration of business logic living outside of the endpoint class.
  */
object PongService {
  private val log = Logger.getLogger(getClass.getName)

  def pong(userInfo: SamUserInfoResponse): Either[ErrorResponse, Pong] = {
    log.info(userInfo.userEmail)
    log.info(userInfo.userSubjectId)
    Right(Pong())
  }
}

@Api(name = "avram", version = "v1", scopes = Array("https://www.googleapis.com/auth/userinfo.email"))
class AvramRoutes extends BaseEndpoint {

  private val log = Logger.getLogger(getClass.getName)
  private val avram = new Avram()
  private val database = avram.database

  @ApiMethod(name = "ping", httpMethod = "get", path = "ping")
  def ping: Pong = {
    Pong()
  }

  @ApiMethod(name = "authPing", httpMethod = "get", path = "authPing")
  def authPing(request: HttpServletRequest): Pong = {
    handleAuthenticatedRequest(request) { userInfo => PongService.pong(userInfo) }
  }

//  // TODO: remove this endpoint when we have more meaningful ways to test database queries
//  @ApiMethod(name = "now", httpMethod = "get", path = "now")
//  def now: Now = {
//    // Explicitly use a Future to make sure the implicit ExecutionContext is being used
//    Now(Await.result(Future(fetchTimestampFromDBWithSlick()), Duration.Inf))////I'm going to delete this obvs but here's how we might use a Transaction
//  @ApiMethod(name = "addCollection", httpMethod = "get", path = "addCollection")
//  def addCollection: Collection = {
//    Await.result(
//      for {
//        _ <- database.inTransaction { dataAccess => dataAccess.collectionQuery.save("testResource", "samResource1", "anu") }
//      } yield {
//        Collection("testResource", "samResource2", )
//      }, Duration.Inf)
//  }
//
  ////I'm going to delete this obvs but here's how we might use a Transaction
//  @ApiMethod(name = "addEntity", httpMethod = "get", path = "addEntity")
//  def addEntity: Entity = {
//    val fieldList = Json.fromFields(List(("key1", Json.fromString("value1")), ("key2", Json.fromInt(1))))
//    Await.result(
//      for {
//        _ <- database.inTransaction { dataAccess => dataAccess.entityQuery.save("testEntity5", 1, "anu", fieldList) }
//      } yield {
//        Entity("testEntity5", 1, fieldList.noSpaces)
//      }, Duration.Inf)
//  }

//  //I'm going to delete this obvs but here's how we might use a Transaction
//  @ApiMethod(name = "getEntity", httpMethod = "get", path = "getEntity")
//  def getEntity: EntityAlt = {
//    //val fieldList = Json.fromFields(List(("key1", Json.fromString("value1")), ("key2", Json.fromInt(1))))
//    val entity = Await.result(
//      for {
//         entity <- database.inTransaction { dataAccess => dataAccess.entityQuery.getEntityByName("testEntity5", 1)}
//      } yield {
//        entity
//      }, Duration.Inf)
//
//    entity match {
//      case Some(ent) => EntityAlt("testEntity5", 1, ent.entityBody.noSpaces)
//      case None => EntityAlt("NOPE", 1, "no")
//    }
//  }

//  // TODO: remove this endpoint when we have more meaningful ways to test database queries
//  @ApiMethod(name = "now", httpMethod = "get", path = "now")
//  def now: Now = {
//    // Explicitly use a Future to make sure the implicit ExecutionContext is being used
//    Now(Await.result(Future(fetchTimestampFromDBWithSlick()), Duration.Inf))
//  }
//
//  // TODO: move/merge this endpoint into a status API
//  @ApiMethod(name = "dbPoolStats", httpMethod = "get", path = "dbPoolStats")
//  def dbPoolStats: DbPoolStats = {
//    val result = for {
//      totalConnections <-  dataSource.database.run(
//        sql"select count(*) from pg_stat_activity where pid <> pg_backend_pid() and usename = current_user".as[Int])
//    } yield DbPoolStats(dataSource.ds.getNumActive, dataSource.ds.getNumIdle, totalConnections.head)
//    Await.result(result, Duration.Inf)
//  }
//
//  private def fetchTimestampFromDBWithSlick(): String = {
//    val now = Await.result(database.run(sql"select now()".as[String]), Duration.Inf)
//    now.head
//  }
}
