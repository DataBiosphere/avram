addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")

//idk if we're using spray, take this line out if not
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.2.5")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.0")

addSbtPlugin("com.eed3si9n" % "sbt-appengine" % "0.8.0")

//{
//  val pluginVersion = System.getProperty("plugin.version")
//  if(pluginVersion == null)
//    throw new RuntimeException("""|The system property 'plugin.version' is not defined.
//                                  |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
//  else addSbtPlugin("com.eed3si9n" % "sbt-appengine" % pluginVersion)
//}