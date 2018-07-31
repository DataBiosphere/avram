package org.broadinstitute.dsde.workbench.avram.api

import com.google.api.server.spi.config.Api
import com.google.api.server.spi.auth.EspAuthenticator
import com.google.api.server.spi.auth.common.User
import com.google.api.server.spi.config.AnnotationBoolean
import com.google.api.server.spi.config.ApiIssuer
import com.google.api.server.spi.config.ApiIssuerAudience
import com.google.api.server.spi.config.ApiMethod
import com.google.api.server.spi.config.ApiNamespace
import com.google.api.server.spi.config.Named
import com.google.api.server.spi.config.Nullable
import com.google.api.server.spi.response.UnauthorizedException


@Api(name = "skeleton-api", version = "v1")
class Skeleton {

  /**
    * Echoes the received message back. If n is a non-negative integer, the message is copied that
    * many times in the returned message.
    *
    * <p>Note that name is specified and will override the default name of "{class name}.{method
    * name}". For example, the default is "echo.echo".
    *
    * <p>Note that httpMethod is not specified. This will default to a reasonable HTTP method
    * depending on the API method name. In this case, the HTTP method will default to POST.
    */
  // [START echo_method]
  @ApiMethod(name = "echo")
  def echo(message: String, @Named("n") @Nullable n: Integer): String = "Hello, " + message
  // [END echo_method]


}
