package org.broadinstitute.dsde.workbench.avram.util

import java.util.Collections
import com.google.cloud.MonitoredResource
import com.google.cloud.logging.Payload.StringPayload
import com.google.cloud.logging.{LogEntry, Logging, LoggingOptions, Severity}
import scala.collection.JavaConverters._

/*
 Logger allows logging to Stackdriver Logging
 */
class Logger {

  private val logging: Logging = LoggingOptions.getDefaultInstance.getService

  // this could be passed to the Logger as a parameter
  private val logName = "avram"

  //these values should come from config
  private val projectId = "broad-avram-dev"

  private val resourceType = "cloud_debugger_resource"

  private def entry(text: String): LogEntry = LogEntry
    .newBuilder(StringPayload.of(text))
    .setSeverity(Severity.INFO)
    .setLogName(logName)
    .setResource(MonitoredResource.newBuilder(resourceType).setLabels(Map("project_id" -> projectId, "app" -> "avram").asJava).build)
    .build

  def log(message: String) = {
    logging.write(Collections.singleton(entry(message)))
  }
}