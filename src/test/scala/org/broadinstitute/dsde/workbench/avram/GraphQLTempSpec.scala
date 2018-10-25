package org.broadinstitute.dsde.workbench.avram

import java.util.UUID

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator
import io.circe.Json
import org.broadinstitute.dsde.workbench.avram.model.GraphQL
import org.broadinstitute.dsde.workbench.avram.service.EntityService
import org.scalatest.FlatSpecLike
import sangria.execution.Executor
import sangria.macros._
import sangria.marshalling.circe._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

class GraphQLTempSpec extends FlatSpecLike {
  implicit val executionContext: ExecutionContext = TestExecutionContext.testExecutionContext

  "GraphQL first test" should "retrieve attributes" in {
    val q = graphql"""
    query MyProduct {
      entity(collectionId: "8e56d95f-8326-42ce-81f4-909624095ad2", entityId: "816284a9-cca7-4bd4-808f-c430c5bb697e") {
        createdBy
        updatedBy
      }
    }
  """

    val resultFut: Future[Json] =
      Executor.execute(GraphQL.schema, q, new EntityService)
    val response = Await.result(resultFut, 10.seconds)
    println(response.toString)
  }


}
