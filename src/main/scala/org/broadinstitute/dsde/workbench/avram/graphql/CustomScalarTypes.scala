package org.broadinstitute.dsde.workbench.avram.graphql

import java.time.Instant
import java.util.UUID

import io.circe.{Json, JsonNumber}
import io.circe.Json.{JBoolean, JNumber, JString}
import sangria.ast
import sangria.schema.ScalarType
import sangria.validation.{ValueCoercionViolation, Violation}
import io.circe._
import io.circe.parser._
import org.broadinstitute.dsde.workbench.avram.graphql.CustomScalarTypes.JsonCoercionViolation

import scala.util.{Failure, Success, Try}

object CustomScalarTypes {

  case object InstantCoercionViolation extends ValueCoercionViolation("Date value expected")

  def parseDate(s: String) = Try(Instant.parse(s)) match {
    case Success(d) ⇒ Right(d)
    case Failure(error) ⇒ Left(InstantCoercionViolation)
  }


  val instantType = ScalarType[Instant]("Instant",
    description = Some("A date scalar type"),
    coerceOutput = (d, _) ⇒ d.toString,
    coerceUserInput = {
      case s: String ⇒ parseDate(s)
      case _ ⇒ Left(InstantCoercionViolation)
    },
    coerceInput = {
      case ast.StringValue(s, _, _, _, _) ⇒ parseDate(s)
      case _ ⇒ Left(InstantCoercionViolation)
    })


  case object UUIDCoercionViolation extends ValueCoercionViolation("UUID value expected")

  def parseUUID(s: String) = Try(UUID.fromString(s)) match {
    case Success(uuid) ⇒ Right(uuid)
    case Failure(error) ⇒ Left(UUIDCoercionViolation)
  }

  val uuidType = ScalarType[UUID]("UUID",
    description = Some("A UUID type"),
    coerceOutput = (uuid, _) ⇒ uuid.toString,
    coerceUserInput = {
      case s: String ⇒ parseUUID(s)
      case _ ⇒ Left(UUIDCoercionViolation)
    },
    coerceInput = {
      case ast.StringValue(s, _, _, _, _) ⇒ parseUUID(s)
      case _ ⇒ Left(UUIDCoercionViolation)
    })


  def parseJsonString(jsonStr: String): Either[Violation, Json] = {
    parse(jsonStr) match {
      case Left(value) => Left(JsonCoercionViolation)
      case Right(x) => Right(x)
    }
  }

  case object JsonCoercionViolation extends ValueCoercionViolation("Not valid JSON")

  implicit val JsonType = ScalarType[Json]("Json",
    description = Some("Raw JSON value"),
    coerceOutput = (value: Json, _) ⇒ value.toString(),
    coerceUserInput = {
      case v: String ⇒ parseJsonString(v)
      case _ => Left(JsonCoercionViolation)

      //      case v: Boolean ⇒ Right(Json.fromBoolean(v))
//      case v: Int ⇒ Right(Json.fromInt(v))
//      case v: Long ⇒ Right(Json.fromLong(v))
//      case v: Float ⇒ Right(Json.fromFloatOrString(v))
//      case v: Double ⇒ Right(Json.fromDoubleOrString(v))
//      case v: BigInt ⇒ Right(Json.fromBigInt(v))
//      case v: BigDecimal ⇒ Right(Json.fromBigDecimal(v))
    },
    coerceInput = {
      case ast.StringValue(jsonStr, _, _, _, _) ⇒
        parseJsonString(jsonStr)
      case _ ⇒
        Left(JsonCoercionViolation)
    })


//  case object JsonCoercionViolation extends ValueCoercionViolation("Not valid JSON")
//
//  implicit val JsonType = ScalarType[JsValue]("Json",
//    description = Some("Raw JSON value"),
//    coerceOutput = (value, _) ⇒ value,
//    coerceUserInput = {
//      case v: String ⇒ Right(JsString(v))
//      case v: Boolean ⇒ Right(JsBoolean(v))
//      case v: Int ⇒ Right(JsNumber(v))
//      case v: Long ⇒ Right(JsNumber(v))
//      case v: Float ⇒ Right(JsNumber(v))
//      case v: Double ⇒ Right(JsNumber(v))
//      case v: BigInt ⇒ Right(JsNumber(v))
//      case v: BigDecimal ⇒ Right(JsNumber(v))
//      case v: JsValue ⇒ Right(v)
//    },
//    coerceInput = {
//      case ast.StringValue(jsonStr, _, _) ⇒
//        Right(jsonStr.parseJson)
//      case _ ⇒
//        Left(JsonCoercionViolation)
//    })
//


}
