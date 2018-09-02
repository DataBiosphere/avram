package org.broadinstitute.dsde.workbench.avram.config

case class DbcpDataSourceConfig(driverClassName: String,
                          url: String,
                          username: String,
                          password: String,
                          maxTotal: Int,
                          slickNumThreads: Int,
                          slickQueueSize: Int)
