package org.broadinstitute.dsde.workbench.avram.util

import org.scalatest.{OneInstancePerTest, Suite}
import org.scalatest.mockito.MockitoSugar

/**
  * Convenience mash-up of MockitoSugar and OneInstancePerTest. Mockito mocks are stateful and
  * therefore must not be shared between tests. Using OneInstancePerTest creates test case isolation
  * whice allowing mocks to be instance variables as opposed to the extra ceremony of fixture
  * factory functions.
  */
trait MockitoSafety extends MockitoSugar with OneInstancePerTest { self: Suite => }
