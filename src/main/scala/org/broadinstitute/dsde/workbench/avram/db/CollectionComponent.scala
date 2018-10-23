package org.broadinstitute.dsde.workbench.avram.db

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

import AvramPostgresProfile.api._
import org.broadinstitute.dsde.workbench.avram.model.{Collection, SamResource}

import scala.concurrent.ExecutionContext


case class CollectionRecord(id: Long,
                            externalId: UUID,
                            samResource: String,
                            createdBy: String,
                            createdTimestamp: Timestamp,
                            updatedBy: String,
                            updatedTimestamp: Timestamp)

trait CollectionComponent extends AvramComponent {

  class CollectionTable(tag: Tag) extends Table[CollectionRecord](tag, "collection") {
    def id =                     column[Long]            ("id",                  O.PrimaryKey, O.AutoInc)
    def externalId =             column[UUID]            ("external_id",         O.Unique)
    def samResource =            column[String]          ("sam_resource",        O.Length(1000))
    def createdBy =              column[String]          ("created_by",          O.Length(1000))
    def createdTimestamp =       column[Timestamp]       ("created_timestamp",   O.SqlType("TIMESTAMP(6)"))
    def updatedBy =              column[String]          ("updated_by",          O.Length(1000))
    def updatedTimestamp =       column[Timestamp]       ("updated_timestamp",   O.SqlType("TIMESTAMP(6)"))

    //we use liquibase for DDL updates, not slick

    def * = (id, externalId, samResource, createdBy, createdTimestamp, updatedBy, updatedTimestamp) <> (CollectionRecord.tupled, CollectionRecord.unapply)
  }

  object collectionQuery extends TableQuery(new CollectionTable(_)) {

    def save(externalId: UUID, samResource: SamResource, createdBy: String)(implicit executionContext: ExecutionContext): DBIO[Collection] = {
      val now = Timestamp.from(Instant.now)
      //currently the updatedBy and updatedTimestamp are the same as created at save time
      val collectionRecord = CollectionRecord(0, externalId, samResource.resourceName, createdBy, now, createdBy, now)
      for {
        _ <- collectionQuery += collectionRecord
      } yield {
        unmarshalCollection(collectionRecord)
      }
    }

    def getCollectionByExternalId(externalId: UUID)(implicit executionContext: ExecutionContext): DBIO[Option[Collection]] = {
      collectionQuery.filter { _.externalId === externalId}.result map { recs: Seq[CollectionRecord] =>
        unmarshalCollections(recs).headOption
      }
    }

    def getCollectionBySamResource(samResource: SamResource)(implicit executionContext: ExecutionContext): DBIO[Option[Collection]] = {
      collectionQuery.filter { _.samResource === samResource.resourceName }.result map { recs: Seq[CollectionRecord] =>
        unmarshalCollections(recs).headOption
      }
    }
    def deleteCollectionByExternalId(externalId: UUID): DBIO[Int] = {
      collectionQuery.filter { _.externalId === externalId }.delete
    }

    private def unmarshalCollection(rec: CollectionRecord): Collection = {
      Collection(rec.externalId, SamResource(rec.samResource), rec.createdBy, rec.createdTimestamp.toInstant, rec.updatedBy, rec.updatedTimestamp.toInstant)
    }

    private def unmarshalCollections(recs: Seq[CollectionRecord]): Seq[Collection] = {
      recs map {
        case (rec) => unmarshalCollection(rec)
      }
    }
  }

}
