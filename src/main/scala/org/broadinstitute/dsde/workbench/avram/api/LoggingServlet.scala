package org.broadinstitute.dsde.workbench.avram.api

import java.time.Instant
import java.util.logging.Logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet

// [START simple_logging_example]
// With @WebServlet annotation the webapp/WEB-INF/web.xml is no longer required.
@WebServlet(name = "RequestLogging", description = "Requests: Logging example", urlPatterns = Array("/requests/log"))
class LoggingServlet extends HttpServlet {

  private val log = Logger.getLogger("Logging Servlet")


  import javax.servlet.http.HttpServletRequest
  import javax.servlet.http.HttpServletResponse
  import java.io.IOException

  @throws[IOException]
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    log.info("An informational message.")
    log.warning("A warning message.")
    log.severe("An error message.")
    // [START_EXCLUDE]
    resp.setContentType("text/plain")
    resp.getWriter.println("This is my response~~~")
    // [END_EXCLUDE]
  }

}
// [END simple_logging_example]
