package org.broadinstitute.dsde.workbench.avram.util

import java.util.logging.Logger

import com.google.api.server.spi.response._

/**
  * Representation of an error response for use throughout the code. Is eventually translated to one
  * of the Google Cloud Endpoints Framework exceptions that, when thrown, results in the correct
  * HTTP response.
  *
  * @param statusCode HTTP status code
  * @param message    error message
  */
case class ErrorResponse(statusCode: Int, message: String) {
  private val log = Logger.getLogger(getClass.getName)

  /**
    * Create a Google Cloud Endpoints Framework exception matching the status code.
    */
  def exception: Exception = {
    statusCode match {
      case 400 => new BadRequestException(message)
      case 401 => new UnauthorizedException(message)
      case 403 => new ForbiddenException(message)
      case 404 => new NotFoundException(message)
      case 409 => new ConflictException(message)
      case 500 => new InternalServerErrorException(message)
      case 503 => new ServiceUnavailableException(message)
      case _ =>
        log.warning(s"Unexpected status code: $statusCode. Using 500: $message")
        new InternalServerErrorException(message)
    }
  }
}
