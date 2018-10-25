package org.broadinstitute.dsde.workbench.avram.graphql

import java.time.Instant
import java.util.{Date, UUID}

import sangria.ast
import sangria.schema.ScalarType
import sangria.validation.ValueCoercionViolation

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

}
