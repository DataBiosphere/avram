package org.broadinstitute.dsde.workbench.avram


import net.ceedubs.ficus.readers.ValueReader

package object config {
  implicit val dbcpDataSourceReader: ValueReader[DbcpDataSourceConfig] = ValueReader.relative { config =>
    DbcpDataSourceConfig(
      config.getString("driverClassName"),
      config.getString("url"),
      config.getString("username"),
      config.getString("password"),
      config.getInt("maxTotal"),
      config.getInt("slick.numThreads"),
      config.getInt("slick.queueSize")
    )
  }
}
