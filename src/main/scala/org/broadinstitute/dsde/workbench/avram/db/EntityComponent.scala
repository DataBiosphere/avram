package org.broadinstitute.dsde.workbench.avram.db



import AvramPostgresProfile.api._
import io.circe.Json
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

import org.broadinstitute.dsde.workbench.avram.model.Entity

import scala.concurrent.ExecutionContext

case class EntityRecord(id: Long,
                        externalId: UUID,
                        externalCollectionId: UUID,
                        entityBody: Json,
                        createdBy: String,
                        createdTimestamp: Timestamp,
                        updatedBy: String,
                        updatedTimestamp: Timestamp)

trait EntityComponent extends AvramComponent  {
  this: CollectionComponent =>

  class EntityTable(tag: Tag) extends Table[EntityRecord](tag, "entity") {
    def id =                     column[Long]                  ("id",                O.PrimaryKey, O.AutoInc)
    def externalId =             column[UUID]                  ("external_id",       O.Unique)
    def externalCollectionId =   column[UUID]                  ("external_collection_id")
    def entityBody =             column[Json]                  ("entity_body",       O.SqlType("JSONB"))
    def createdBy =              column[String]                ("created_by",        O.Length(1000))
    def createdTimestamp =       column[Timestamp]             ("created_timestamp", O.SqlType("TIMESTAMP(6)"))
    def updatedBy =              column[String]                ("updated_by",        O.Length(1000))
    def updatedTimestamp =       column[Timestamp]             ("updated_timestamp", O.SqlType("TIMESTAMP(6)"))

    def collectionForeignKey = foreignKey("FK_COLLECTION", externalCollectionId, collectionQuery)(_.externalId)

    //we use liquibase for DDL updates, not slick

    def * = (id, externalId, externalCollectionId, entityBody, createdBy, createdTimestamp, updatedBy, updatedTimestamp) <> (EntityRecord.tupled, EntityRecord.unapply)
  }

  object entityQuery extends TableQuery(new EntityTable(_)) {

    def save(externalId: UUID, collection: UUID, createdBy: String,  entityBody: Json): DBIO[Int] = {
      val now = Timestamp.from(Instant.now)
      //currently the updatedBy and updatedTimestamp are the same as created at save time
      entityQuery += EntityRecord(0, externalId, collection, entityBody, createdBy, now, createdBy, now)
    }

    def getEntityByExternalId(externalId: UUID, externalCollectionId: UUID)(implicit executionContext: ExecutionContext): DBIO[Option[Entity]] = {
      entityQuery
        .filter { _.externalId === externalId}
        .filter { _.externalCollectionId === externalCollectionId }
        .result map { recs: Seq[EntityRecord] =>
        unmarshalEntities(recs).headOption
      }
    }

    private def unmarshalEntities(entityRecord: Seq[EntityRecord]): Seq[Entity] = {
      entityRecord map {
        case (rec) => Entity(rec.externalId, rec.externalCollectionId, rec.entityBody.noSpaces, rec.createdBy, rec.createdTimestamp.toInstant, rec.updatedBy, rec.updatedTimestamp.toInstant)
      }
    }
  }

}
