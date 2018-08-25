package org.broadinstitute.dsde.workbench.avram.api

import org.scalatest.{FlatSpec, Matchers}

class AvramRoutesSpec extends FlatSpec with Matchers {

  //some comments and stuff
  "AvramRoutes" should "should return Pong() on ping" in {
    val avramRoutes = new AvramRoutes()
    avramRoutes.ping shouldEqual Pong()
  }

}
