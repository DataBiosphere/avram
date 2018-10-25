package org.broadinstitute.dsde.workbench.avram.model


import java.util.UUID

import io.circe.Json.{JArray, JNumber, JString}
import io.circe.{Json, JsonObject}
import org.broadinstitute.dsde.workbench.avram.graphql.CustomScalarTypes
import sangria.ast.{FieldDefinition, TypeDefinition}
import sangria.schema._
import sangria.macros.derive._
import org.broadinstitute.dsde.workbench.avram.service.EntityService

import scala.concurrent.ExecutionContext.Implicits.global

object GraphQL {

  implicit val UUIDType = CustomScalarTypes.uuidType
  implicit val InstantType = CustomScalarTypes.instantType

  val EntityType =
    deriveObjectType[Unit, Entity](
    )

  val collectionId = Argument("collectionId", UUIDType)
  val entityId = Argument("entityId", UUIDType)

  val QueryType = ObjectType("Query", fields[EntityService, Unit](
    Field("entity", EntityType,
      arguments = collectionId :: entityId :: Nil,
      resolve = c ⇒ c.ctx.getEntity(c arg collectionId, c.arg(entityId)))))

//    Field("entities", ListType(EntityType),
//      description = Some("Returns a list of all available products."),
//      resolve = _.ctx.products))


  val schema = Schema(QueryType)
//  def query(queryAst: Document, data: Json): Json = {
//    val clientSchema: Schema[Any, Any] =
//      Schema.buildFromIntrospection(data)
//
//    Executor.execute(clientSchema, queryAst)
////      userContext = new CharacterRepo,
////      deferredResolver = new DynamicResolver,
////      variables = vars)
//  }

//  val builder =
//    new DefaultAstSchemaBuilder[Json] {
//      override def resolveField(typeDefinition: TypeDefinition, definition: FieldDefinition) =
//        typeDefinition.name match {
//          case "Query" ⇒
//            c ⇒ c.ctx.asJsObject.fields get c.field.name map fromJson
//          case _ ⇒
//            c ⇒ fromJson(c.value.asInstanceOf[JsonObject].fields(c.field.name))
//        }
//
//      def fromJson(v: Json) = v match {
//        case JArray(l) ⇒ l
//        case JString(s) ⇒ s
//        case JNumber(n) ⇒ n.toInt
//        case other ⇒ other
//      }
//    }
}
