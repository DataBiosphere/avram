package org.broadinstitute.dsde.workbench.entityservice.api


import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{FlatSpec, Matchers}

class EntityServiceRoutesSpec extends FlatSpec with Matchers with ScalatestRouteTest {

  class TestEntityServiceRoutes()
    extends EntityServiceRoutes

  "EntityServiceRoutes" should "200 on ping" in {
    val entityServiceRoutes = new TestEntityServiceRoutes()

    Get("/ping") ~> entityServiceRoutes.route ~> check {
      status shouldEqual StatusCodes.OK
    }
  }
}
