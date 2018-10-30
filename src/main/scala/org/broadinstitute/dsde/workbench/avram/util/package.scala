package org.broadinstitute.dsde.workbench.avram

import cats.data.EitherT
import cats.effect.IO
import org.broadinstitute.dsde.workbench.avram.model.AvramException

package object util {
  /**
    * Convenience type that effectively represents IO[Either[ErrorResponse, A]]
    */
  type AvramResult[A] = EitherT[IO, AvramException, A]
}
