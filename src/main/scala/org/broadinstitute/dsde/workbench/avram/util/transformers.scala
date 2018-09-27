package org.broadinstitute.dsde.workbench.avram.util

import cats.data.{EitherT, ReaderT}
import cats.effect.IO
import cats.implicits._
import org.broadinstitute.dsde.workbench.avram.dependencies.{AvramDependencies, Global}
import org.broadinstitute.dsde.workbench.avram.util.AvramError._

import scala.concurrent.Future

/**
  * Defines an effect stack for Avram based on cats monad transformers. See:
  * https://typelevel.org/cats/datatypes/eithert.html
  * https://typelevel.org/cats/datatypes/kleisli.html
  * https://typelevel.org/cats-effect/datatypes/io.html
  */
object transformers {
  // AvramResult is a stack of the effects:
  // - Reader
  //   - IO
  //     - Either
  type AvramReader[A] = ReaderT[IO, AvramDependencies, A]
  type AvramResult[A] = EitherT[AvramReader, AvramError, A]

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

  // AAAAHHHH
  def unsafeRun[A](result: AvramResult[A]): A = {
    result.value.run(Global.dependencies).unsafeRunSync().fold(error => throw error.toServiceException, identity)
  }

}
