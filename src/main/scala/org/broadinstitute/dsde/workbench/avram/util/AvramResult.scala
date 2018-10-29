package org.broadinstitute.dsde.workbench.avram.util

import cats.data.EitherT
import cats.effect.IO
import org.broadinstitute.dsde.workbench.avram.model.AvramException

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

// Note: I'm unsatisfied with AvramResult as a name but I can't think of anything better. Suggestions welcome. -breilly
object AvramResult {

  def pure[A](a: A): AvramResult[A] = EitherT.pure(a)
  def fromError[A](e: AvramException): AvramResult[A] = EitherT.left(IO(e))
  def fromIO[A](io: IO[A]): AvramResult[A] = EitherT.right(io)
  def fromEither[A](either: Either[AvramException, A]): AvramResult[A] = EitherT.fromEither[IO](either)
  def fromOption[A](option: Option[A], e: AvramException): AvramResult[A] = EitherT.fromOption[IO](option, e)
  def fromFuture[A](future: Future[A]): AvramResult[A] = fromIO(IO.fromFuture(IO(future)))

  /**
    * Perform computations represented by an AvramResult and handle the result.
    *
    * @param onSuccess callback for successful computation
    * @param onFailure callback for error
    * @param throwableT transform an unhandled throwable
    * @param result computations to run
    * @tparam A computation result type
    * @tparam B callback function result type
    */
  def unsafeRun[A, B](onSuccess: A => B,
                      onFailure: AvramException => B,
                      throwableT: Throwable => AvramException)
                     (result: AvramResult[A]): B = {
    /*
     * All computation done when evaluating the AvramResult _should_ translate exceptions to
     * appropriate AvramException results. However, it is still possible for exceptions to be
     * thrown. We catch those here and treat them as AvramException(500, ...).
     */
    Try {
      result.value.unsafeRunSync()
    } match {
      case Failure(unhandledThrowable) =>
        onFailure(throwableT(unhandledThrowable))
      case Success(value) =>
        value match {
          case Left(error) => onFailure(error)
          case Right(thing) => onSuccess(thing)
        }
    }
  }

  /**
    * Perform computations represented by an AvramResult and produce the result value. This will
    * throw an exception for any errors raised by the computation.
    *
    * NOTE: This is a convenience for tests and is not intended for situations where proper error
    * handling is required.
    */
  def unsafeRun[A](result: AvramResult[A]): A = {
    unsafeRun(identity[A], e => throw e, e => throw e)(result)
  }
}
