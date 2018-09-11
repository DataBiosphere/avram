package org.broadinstitute.dsde.workbench.avram.config

import net.ceedubs.ficus.readers.ValueReader

case class DbcpDataSourceConfig(driverClassName: String,
                                url: String,
                                username: String,
                                password: String,
                                maxTotal: Int,
                                slickNumThreads: Int,
                                slickQueueSize: Int)  {

  implicit val dbcpDataSourceReader: ValueReader[DbcpDataSourceConfig] = ValueReader.relative { config =>
    DbcpDataSourceConfig(
      config.getString("driverClassName"),
      config.getString("url"),
      config.getString("username"),
      config.getString("password"),
      config.getInt("maxTotal"),
      config.getInt("slickNumThreads"),
      config.getInt("slickQueueSize")
    )
  }
}