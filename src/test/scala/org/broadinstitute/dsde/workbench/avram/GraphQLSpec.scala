package org.broadinstitute.dsde.workbench.avram

import java.util.UUID

import io.circe.Json
import org.broadinstitute.dsde.workbench.avram.db.TestComponent
import org.broadinstitute.dsde.workbench.avram.model.GraphQL
import org.broadinstitute.dsde.workbench.avram.service.EntityService
import org.scalatest.FlatSpecLike
import sangria.ast.Document
import sangria.macros._
import sangria.execution._
import sangria.marshalling.circe._

import scala.concurrent.Future


class GraphQLSpec extends TestComponent with FlatSpecLike {

  "GraphQL" should "retrieve attributes" in isolatedDbTest {
    // save a collection
    val externalCollectionId = UUID.randomUUID()
    dbFutureValue { _.collectionQuery.save(externalCollectionId, CommonTestData.samResource, CommonTestData.user1) }

    // save an entity
    val externalEntityId = UUID.randomUUID()
    dbFutureValue { _.entityQuery.save(externalEntityId, externalCollectionId, CommonTestData.user1, entityBody) }

    // query for a field
//    GraphQL.query(queryAst, entityBody)

    val saveResult = dbFutureValue { _.entityQuery.getEntityByExternalId(externalEntityId, externalCollectionId) }.get

    saveResult.externalId shouldEqual externalEntityId
    saveResult.externalCollectionId shouldEqual externalCollectionId
    saveResult.createdBy shouldEqual CommonTestData.user1
    saveResult.entityBody shouldEqual CommonTestData.entityBody1.noSpaces
  }


  val entityBody = Json.fromFields(List(
    ("sample_id", Json.fromString("sample_ABC")),
    ("age", Json.fromInt(20)),
    ("bam", Json.fromString("gs://path/to/bam.txt"))
  ))

  val queryAst: Document =
    graphql"""
    {
      bam
    }
  """

}
