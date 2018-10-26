package org.broadinstitute.dsde.workbench.avram.util

import cats.data.EitherT
import cats.effect.IO

// Note: I'm unsatisfied with AvramResult as a name but I can't think of anything better. Suggestions welcome. -breilly
object AvramResult {

  def pure[A](a: A): AvramResult[A] = EitherT.pure(a)
  def fromError[A](e: ErrorResponse): AvramResult[A] = EitherT.left(IO(e))
  def fromIO[A](io: IO[A]): AvramResult[A] = EitherT.right(io)
  def fromEither[A](either: Either[ErrorResponse, A]): AvramResult[A] = EitherT.fromEither[IO](either)

  /**
    * Invoke execution of computations represented by an AvramResult and produce the result value.
    * This will throw an exception for any errors raised by the computation.
    */
  def unsafeRun[A](result: AvramResult[A]): A =
    result.value.unsafeRunSync().fold(e => throw e.exception, identity)
}
