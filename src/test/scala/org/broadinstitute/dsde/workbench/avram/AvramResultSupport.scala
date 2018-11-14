package org.broadinstitute.dsde.workbench.avram

import org.broadinstitute.dsde.workbench.avram.model.AvramException
import org.broadinstitute.dsde.workbench.avram.util.AvramResult

trait AvramResultSupport {
  /**
    * Perform computations represented by an AvramResult and produce the result value. This will
    * throw an exception for any errors raised by the computation.
    *
    * NOTE: This is a convenience for tests and is not intended for situations where proper error
    * handling is required.
    */
  def unsafeRun[A](result: AvramResult[A]): A = {
    AvramResult.unsafeRun(identity[A],
      (e: AvramException) => throw e,
      (e: Throwable) => throw new Exception("Unhandled error", e))(result)
  }
}
