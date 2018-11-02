package org.broadinstitute.dsde.workbench.avram.model


import java.util.UUID

import io.circe.Json.{JArray, JNumber, JString}
import io.circe.{Json, JsonObject}
import org.broadinstitute.dsde.workbench.avram.graphql.CustomScalarTypes
import sangria.ast.{FieldDefinition, ObjectValue, StringValue, TypeDefinition}
import sangria.schema._
import sangria.macros._
import sangria.macros.derive.{ReplaceField, _}
import sangria.marshalling.circe._
import org.broadinstitute.dsde.workbench.avram.service.EntityService
import sangria.visitor.VisitorCommand.Transform
import io.circe._
import io.circe.parser._

import scala.concurrent.ExecutionContext.Implicits.global

object GraphQL {

  implicit val UUIDType = CustomScalarTypes.uuidType
  implicit val InstantType = CustomScalarTypes.instantType
  implicit val JsonType = CustomScalarTypes.JsonType

  val test = ObjectType("mytest", () => fields[Unit, ObjectValue](Field("a", StringType, resolve = _ => "aval")))

  val EntityType =
    deriveObjectType[Unit, Entity](
      ReplaceField("entityBody", Field("entityBody", test, resolve = x => ObjectValue("a" -> StringValue("aval"))))
//      ExcludeFields("entityBody")
    )

//  val EntityType = ObjectType("Entity", () ⇒ fields[Unit, Entity](
//    Field("externalId", UUIDType, resolve = _.value.externalId),
//    Field("externalCollectionId", UUIDType, resolve = _.value.externalCollectionId),
//    Field("createdBy", StringType, resolve = _.value.createdBy),
//    Field("createdTimestamp", InstantType, resolve = _.value.createdTimestamp),
//    Field("updatedBy", StringType, resolve = _.value.updatedBy),
//    Field("updatedTimestamp", InstantType, resolve = _.value.updatedTimestamp),
//    Field("jsonData", JsonType,
////      tags = ProjectionName("internalJson") :: Nil,
//      resolve = Projector((ctx, projected) => Value(parse(ctx.value.entityBody).getOrElse(Json.Null)))
//    )
//  ))


  val collectionId = Argument("collectionId", UUIDType)
  val entityId = Argument("entityId", UUIDType)

  val QueryType = ObjectType("Query", fields[EntityService, Unit](
    Field("entity", EntityType,
      arguments = collectionId :: entityId :: Nil,
      resolve = c ⇒ c.ctx.getEntity(c arg collectionId, c.arg(entityId)))))

  val schema = Schema(QueryType)

//  val extensions = gql"""
//                      extend type Entity {
//                      entityBody: String! @loadEntityBody
//                      }
//    """
//
//  val LoadEntityBodyDir = Directive("loadEntityBody",
//    locations = Set(DirectiveLocation.FieldDefinition))
//
//  val builder = AstSchemaBuilder.resolverBased[EntityService](
//    DirectiveResolver(LoadEntityBodyDir, _.ctx.ctx.loadEntityBody),
//    FieldResolver.defaultInput[EntityService, Json])
//
//  val schema = staticSchema.extend(extensions, builder)



//    Field("entities", ListType(EntityType),
//      description = Some("Returns a list of all available products."),
//      resolve = _.ctx.products))

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
