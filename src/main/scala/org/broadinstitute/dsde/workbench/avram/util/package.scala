package org.broadinstitute.dsde.workbench.avram

import cats.data.EitherT
import cats.effect.IO

package object util {
  /**
    * Convenience type that effectively represents IO[Either[ErrorResponse, A]]
    */
  type AvramResult[A] = EitherT[IO, ErrorResponse, A]
}
