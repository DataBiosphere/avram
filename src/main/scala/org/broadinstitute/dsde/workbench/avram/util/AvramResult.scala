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
    * Make an AvramException from a Try. If it is a Failure, returns an AvramResult with a copy of
    * the provided AvramException where the cause is the Failure's exception. Primarily meant for
    * interfacing with Java and other external code that may throw exceptions.
    */
  def fromTry[A](t: Try[A], e: AvramException): AvramResult[A] = t.fold(cause => fromError[A](e.copy(cause = Option(cause))), pure)

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
}
