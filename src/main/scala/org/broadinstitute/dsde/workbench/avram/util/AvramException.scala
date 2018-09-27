package org.broadinstitute.dsde.workbench.avram.util

import com.google.api.server.spi.ServiceException
import com.google.api.server.spi.response._

case class AvramException(statusCode: Int, message: String) {

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
