package org.broadinstitute.dsde.workbench.avram.db

import org.broadinstitute.dsde.workbench.avram.api.Collection
import AvramPostgresProfile.api._

import scala.concurrent.ExecutionContext

case class CollectionRecord(id: Long, name: String, samResource: String)

trait CollectionComponent extends AvramComponent {

  class CollectionTable(tag: Tag) extends Table[CollectionRecord](tag, "collection") {
    def id =              column[Long]    ("id", O.PrimaryKey, O.AutoInc)
    def name =            column[String]  ("name",  O.PrimaryKey, O.Length(254))
    def samResource =     column[String]  ("samResource", O.Length(254))

    def * = (id, name, samResource) <> (CollectionRecord.tupled, CollectionRecord.unapply)
  }

  object collectionQuery extends TableQuery(new CollectionTable(_)) {

    def save(name: String, samResource: String): DBIO[Int] = {
      collectionQuery += CollectionRecord(0, name, samResource)
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
        case (recs) => Collection(recs.name, recs.samResource)
      }
    }
  }

}
