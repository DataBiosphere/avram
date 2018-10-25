package org.broadinstitute.dsde.workbench.avram.util

import cats.data.EitherT
import cats.effect.IO

// Note: I'm unsatisfied with AvramResult as a name but I can't think of anything better. Suggestions welcome. -breilly
object AvramResult {
  /**
    * Convenience type that effectively represents IO[Either[ErrorResponse, A]]
    */
  type AvramResult[A] = EitherT[IO, ErrorResponse, A]

  /**
    * Promote an IO to an AvramResult.
    */
  def AvramResult[A](io: IO[A]): AvramResult[A] = EitherT.right(io)

  /**
    * Promote an Either to an AvramResult.
    */
  def AvramResult[A](either: Either[ErrorResponse, A]): AvramResult[A] =
    EitherT.fromEither[IO](either)

  /**
    * Invoke execution of computations represented by an AvramResult and produce the result value.
    * This will throw an exception for any errors raised by the computation.
    */
  def unsafeRun[A](result: AvramResult[A]): A =
    result.value.unsafeRunSync().fold(e => throw e.exception, identity)
}
