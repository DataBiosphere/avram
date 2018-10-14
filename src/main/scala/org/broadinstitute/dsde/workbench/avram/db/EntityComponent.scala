package org.broadinstitute.dsde.workbench.avram.db


import org.broadinstitute.dsde.workbench.avram.api.Entity

import AvramPostgresProfile.api._
import io.circe.Json
import java.sql.Timestamp
import java.time.Instant

import scala.concurrent.ExecutionContext

case class EntityRecord(id: Long,
                        name: Option[String],
                        collectionId: Long,
                        entityBody: Json,
                        createdBy: String,
                        createdTimestamp: Timestamp,
                        updatedBy: String,
                        updatedTimestamp: Timestamp)

trait EntityComponent extends AvramComponent  {
  this: CollectionComponent =>

  class EntityTable(tag: Tag) extends Table[EntityRecord](tag, "entity") {
    def id =                     column[Long]                  ("id",                O.PrimaryKey, O.AutoInc)
    def name =                   column[Option[String]]        ("name",              O.Length(1000))
    def collectionId =           column[Long]                  ("collection_id")
    def entityBody =             column[Json]                  ("entity_body",       O.SqlType("JSONB"))
    def createdBy =              column[String]                ("created_by",        O.Length(1000))
    def createdTimestamp =       column[Timestamp]             ("created_timestamp", O.SqlType("TIMESTAMP(6)"))
    def updatedBy =              column[String]                ("updated_by",        O.Length(1000))
    def updatedTimestamp =       column[Timestamp]             ("updated_timestamp", O.SqlType("TIMESTAMP(6)"))

    def collectionForeignKey = foreignKey("FK_COLLECTION", collectionId, collectionQuery)(_.id)
    def uniqueKey = index("IDX_NAME_COLLECTION_UNIQUE", (name, collectionId), unique = true)

    //we use liquibase for DDL updates, not slick

    def * = (id, name, collectionId, entityBody, createdBy, createdTimestamp, updatedBy, updatedTimestamp) <> (EntityRecord.tupled, EntityRecord.unapply)
  }

  object entityQuery extends TableQuery(new EntityTable(_)) {

    def save(name: Option[String], collection: Long, createdBy: String,  entityBody: Json): DBIO[Int] = {
      val now = Timestamp.from(Instant.now)
      //currently the updatedBy and updatedTimestamp are the same as created at save time
      entityQuery += EntityRecord(0, name, collection, entityBody, createdBy, now, createdBy, now)
    }

    def getEntityByName(name: String, collectionId: Long)(implicit executionContext: ExecutionContext): DBIO[Option[Entity]] = {
      entityQuery
        .filter { _.name === name}
        .filter { _.collectionId === collectionId }
        .result map { recs: Seq[EntityRecord] =>
        unmarshalEntities(recs).headOption
      }
    }

    private def unmarshalEntities(entityRecord: Seq[EntityRecord]): Seq[Entity] = {
      entityRecord map {
        case (rec) => Entity(rec.name, rec.collectionId, rec.entityBody.noSpaces, rec.createdBy, rec.createdTimestamp.toInstant, rec.updatedBy, rec.updatedTimestamp.toInstant)
      }
    }
  }

}
