package org.broadinstitute.dsde.workbench.avram.api

import org.broadinstitute.dsde.workbench.avram.db.TestComponent
import org.broadinstitute.dsde.workbench.avram.model.SamResource
import org.scalatest.FlatSpecLike

class CollectionsServletSpec extends TestComponent with FlatSpecLike {

  val collectionsServlet = new CollectionsServlet()

  //TODO: Implement this test in imminent PR once we decide on how to mock out our tests
  "CollectionsServlet" should "POST and GET a collection" ignore isolatedDbTest {

  }
}
