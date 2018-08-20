package org.broadinstitute.dsde.workbench.avram.util

import java.util.Collections

import com.google.cloud.MonitoredResource
import com.google.cloud.logging.Payload.StringPayload
import com.google.cloud.logging.{LogEntry, Logging, LoggingOptions, Severity}

class Logger {

  private val logging: Logging = LoggingOptions.getDefaultInstance.getService

  private val logName: String = "avram"

  private def entry(text: String): LogEntry = LogEntry
    .newBuilder(StringPayload.of(text))
    .setSeverity(Severity.INFO)
    .setLogName(logName)
    .setResource(MonitoredResource.newBuilder("cloud_debugger_resource").build)
    .build

  def log(message: String) = {
    logging.write(Collections.singleton(entry(message)))
  }

}
