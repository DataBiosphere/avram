package org.broadinstitute.dsde.workbench.avram.service

import java.time.Instant
import java.util.UUID

import io.circe.Json
import org.broadinstitute.dsde.workbench.avram.model.Entity
import io.circe._, io.circe.parser._
class EntityService {

  val entityBody = """
  |{ "anon1" : "getthisstring",
  |  "embeded1" : { "interior" : "ignorethisvalue"},
  |  "anon2" : 42,
  |  "tryanarray" : ["first", "second", "third"]
  | }""".stripMargin


  def getEntity(collectionId: UUID, entityId: UUID): Entity = {
    println(parse(entityBody))
    new Entity(UUID.fromString("8e56d95f-8326-42ce-81f4-909624095ad2"), UUID.fromString("816284a9-cca7-4bd4-808f-c430c5bb697e"), entityBody, "andrea", Instant.now, "andrea", Instant.now)
  }

  def loadEntityBody: Json = parse(entityBody).getOrElse(Json.Null)

}
