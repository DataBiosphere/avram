package org.broadinstitute.dsde.workbench.avram.util

import com.google.api.server.spi.ServiceException
import com.google.api.server.spi.response._
import io.circe.{DecodingFailure, ParsingFailure}

case class AvramError(statusCode: Int, message: String) {

  def toServiceException: ServiceException = {
    statusCode match {
      case 400 => new BadRequestException(message)
      case 401 => new UnauthorizedException(message)
      case 403 => new ForbiddenException(message)
      case 404 => new NotFoundException(message)
      case 409 => new ConflictException(message)
      case 500 => new InternalServerErrorException(message)
      case 503 => new ServiceUnavailableException(message)
      case _ =>
        new InternalServerErrorException(message)
    }
  }

}

object AvramError {
  // Typeclass for converting types to AvramError
  trait AvramErrorable[A] {
    def toAvramError(a: A): AvramError
  }

  implicit object StringAvramError extends AvramErrorable[String] {
    override def toAvramError(str: String): AvramError = AvramError(500, str)
  }

  implicit object ParsingFailureErrorable extends AvramErrorable[ParsingFailure] {
    override def toAvramError(err: ParsingFailure): AvramError = AvramError(500, err.message)
  }

  implicit object DecodingFailureErrorable extends AvramErrorable[DecodingFailure] {
    override def toAvramError(err: DecodingFailure): AvramError = AvramError(500, err.message)
  }

  implicit object IdentityAvramErrorable extends AvramErrorable[AvramError] {
    override def toAvramError(a: AvramError): AvramError = a
  }
}