package org.broadinstitute.dsde.workbench.avram.db

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

import AvramPostgresProfile.api._
import org.broadinstitute.dsde.workbench.avram.model.Collection

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

    def save(externalId: UUID, samResource: String, createdBy: String): DBIO[Int] = {
      val now = Timestamp.from(Instant.now)
      //currently the updatedBy and updatedTimestamp are the same as created at save time
      collectionQuery += CollectionRecord(0, externalId, samResource, createdBy, now, createdBy, now)
    }

    def getCollectionByExternalId(externalId: UUID)(implicit executionContext: ExecutionContext): DBIO[Option[Collection]] = {
      collectionQuery.filter { _.externalId === externalId}.result map { recs: Seq[CollectionRecord] =>
        unmarshalCollections(recs).headOption
      }
    }

    def deleteCollectionByExternalId(externalId: UUID): DBIO[Int] = {
      collectionQuery.filter { _.externalId === externalId }.delete
    }

    private def unmarshalCollections(collectionRecord: Seq[CollectionRecord]): Seq[Collection] = {
      collectionRecord map {
        case (recs) => Collection(recs.externalId, recs.samResource, recs.createdBy, recs.createdTimestamp.toInstant, recs.updatedBy, recs.updatedTimestamp.toInstant)
      }
    }
  }

}
