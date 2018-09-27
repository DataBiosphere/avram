package org.broadinstitute.dsde.workbench

import cats.data.{EitherT, ReaderT}
import cats.effect._
import cats.implicits._
import io.circe.{DecodingFailure, ParsingFailure}
import org.broadinstitute.dsde.workbench.avram.dependencies.{AvramDependencies, Global}
import org.broadinstitute.dsde.workbench.avram.util.AvramException

import scala.concurrent.Future

package object avram {

  // Typeclass for converting to AvramException
  trait AvramErrorable[A] {
    def toAvramError(a: A): AvramException
  }
  implicit object StringAvramError extends AvramErrorable[String] {
    override def toAvramError(str: String): AvramException = AvramException(500, str)
  }
  implicit object ParsingFailureErrorable extends AvramErrorable[ParsingFailure] {
    override def toAvramError(err: ParsingFailure): AvramException = AvramException(500, err.message)
  }
  implicit object DecodingFailureErrorable extends AvramErrorable[DecodingFailure] {
    override def toAvramError(err: DecodingFailure): AvramException = AvramException(500, err.message)
  }
  implicit object IdentityAvramErrorable extends AvramErrorable[AvramException] {
    override def toAvramError(a: AvramException): AvramException = a
  }

  // Type aliases using monad transformers to stack up the effects:
  // - Reader
  //   - IO
  //     - Either
  type AvramReader[A] = ReaderT[IO, AvramDependencies, A]
  type AvramResult[A] = EitherT[AvramReader, AvramException, A]

  // Conversions to AvramResult
  def ioToResult[A](io: IO[A]): AvramResult[A] = {
    EitherT.liftF(ReaderT.liftF(io))
  }

  def eitherToResult[A: AvramErrorable, B](either: Either[A, B]): AvramResult[B] = {
    val withAvramError = either.leftMap(a => implicitly[AvramErrorable[A]].toAvramError(a))
    EitherT.fromEither[AvramReader](withAvramError)
  }

  def withDependencies[A](reader: AvramDependencies => A): AvramResult[A] = {
    withDependenciesIO(deps => IO.pure(reader(deps)))
  }

  def withDependenciesIO[A](readerIO: AvramDependencies => IO[A]): AvramResult[A] = {
    EitherT.liftF(ReaderT(readerIO))
  }

  def futureToIO[A](future: Future[A]): IO[A] = {
    IO.fromFuture(IO(future))
  }

  // Runs an AvramResult
  def unsafeRun[A](result: AvramResult[A]): A = {
    // AAAAHHHH
    result.value.run(Global.dependencies).unsafeRunSync().fold(error => throw error.toServiceException, identity)
  }

}