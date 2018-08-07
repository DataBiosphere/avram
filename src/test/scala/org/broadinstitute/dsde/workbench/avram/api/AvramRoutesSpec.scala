package org.broadinstitute.dsde.workbench.avram.api

import org.scalatest.{FlatSpec, Matchers}

class AvramRoutesSpec extends FlatSpec with Matchers with ScalatestRouteTest {

  class TestAvramRoutes()
    extends AvramRoutes

  "AvramRoutes" should "200 on ping" in {
    val avramRoutes = new TestAvramRoutes()

    Get("/ping") ~> avramRoutes.route ~> check {
      status shouldEqual StatusCodes.OK
    }
  }
}
