package org.broadinstitute.dsde.workbench.avram.db


import AvramPostgresProfile.api._
//import org.broadinstitute.dsde.workbench.avram.api.Entity
import spray.json._

import scala.concurrent.ExecutionContext

case class EntityRecord(id: Long, name: String, collection: Long, entityBody: JsValue)

trait EntityComponent extends AvramComponent  {
  this: CollectionComponent =>

  class EntityTable(tag: Tag) extends Table[EntityRecord](tag, "entity") {
    def id =             column[Long]      ("id", O.PrimaryKey, O.AutoInc)
    def name =           column[String]    ("name", O.Length(254))
    def collection =     column[Long]      ("collection")
    def entityBody =     column[JsValue]   ("entityBody", O.Length(254), O.SqlType("JSONB"))

    def collectionForeignKey = foreignKey("FK_COLLECTION", collection, collectionQuery)(_.id)
    def uniqueKey = index("IDX_NAME_COLLECTION_UNIQUE", (name, collection), unique = true)

    def * = (id, name, collection, entityBody) <> (EntityRecord.tupled, EntityRecord.unapply)
  }

  object entityQuery extends TableQuery(new EntityTable(_)) {

    def save(name: String, collection: Long, entityBody: JsValue): DBIO[Int] = {
      entityQuery += EntityRecord(0, name, collection, entityBody)
    }
//
//    def getEntityByName(name: String): DBIO[Option[Entity]] = {
//      entityQuery.filter { _.name === name}.result map { recs: Seq[EntityRecord] =>
//        unmarshalEntities(recs).headOption
//      }
//    }
//
//    def deleteEntityByName(name: String): DBIO[Int] = {
//      entityQuery.filter { _.name === name }.delete
//    }
//
//    private def unmarshalEntities(entityRecord: Seq[EntityRecord]): Seq[Entity] = {
//      entityRecord map {
//        case (recs) => Entity(recs.name, collectionQuery.recs.collection), recs.entityBody)
//      }
//    }
  }

}
