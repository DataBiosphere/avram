package org.broadinstitute.dsde.workbench.avram.db

import java.sql.Timestamp
import java.time.Instant

import org.broadinstitute.dsde.workbench.avram.api.Collection
import AvramPostgresProfile.api._

import scala.concurrent.ExecutionContext

case class CollectionRecord(id: Long,
                            name: String,
                            createdBy: String,
                            samResource: String,
                            createdTimestamp: Timestamp,
                            updatedBy: Option[String],
                            updatedTimestamp: Timestamp)

trait CollectionComponent extends AvramComponent {

  class CollectionTable(tag: Tag) extends Table[CollectionRecord](tag, "collection") {
    def id =                     column[Long]            ("id",                  O.PrimaryKey, O.AutoInc)
    def name =                   column[String]          ("name",                O.PrimaryKey, O.Length(1000))
    def samResource =            column[String]          ("sam_resource",        O.Length(1000))
    def createdBy =              column[String]          ("created_by",          O.Length(1000))
    def createdTimestamp =       column[Timestamp]       ("created_timestamp",   O.SqlType("TIMESTAMP(6)"))
    def updatedBy =              column[Option[String]]  ("updated_by",          O.Length(1000))
    def updatedTimestamp =       column[Timestamp]       ("updated_timestamp",   O.SqlType("TIMESTAMP(6)"))

    def * = (id, name, samResource, createdBy, createdTimestamp, updatedBy, updatedTimestamp) <> (CollectionRecord.tupled, CollectionRecord.unapply)
  }

  object collectionQuery extends TableQuery(new CollectionTable(_)) {

    def save(name: String, samResource: String, createdBy: String): DBIO[Int] = {
      collectionQuery += CollectionRecord(0, name, samResource, createdBy, Timestamp.from(Instant.now), None, marshalDate(None))
    }

    def getCollectionByName(name: String): DBIO[Option[Collection]] = {
      collectionQuery.filter { _.name === name}.result map { recs: Seq[CollectionRecord] =>
        unmarshalCollections(recs).headOption
      }
    }

    def deleteCollectionByName(name: String): DBIO[Int] = {
      collectionQuery.filter { _.name === name }.delete
    }

    private def unmarshalCollections(collectionRecord: Seq[CollectionRecord]): Seq[Collection] = {
      collectionRecord map {
        case (recs) => Collection(recs.name, recs.samResource, recs.createdBy, recs.createdTimestamp, recs.updatedBy, recs.updatedTimestamp)
      }
    }
  }

}
