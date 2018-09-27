import Settings._
import Testing._

lazy val root = project.in(file("."))
  .settings(rootSettings:_*)
  .withTestSettings

Revolver.settings

Revolver.enableDebugging(port = 5051, suspend = false)

enablePlugins(AppenginePlugin)
javaOptions in appengineDevServer in Compile -= (javaOptions in appengineDevServer in Compile).value.find(_.matches("-javaagent:.*"))
javaOptions in appengineDevServer in Compile ++= Seq(
  "-Duse_jetty9_runtime=true",
  "-D--enable_all_permissions=true")

// When JAVA_OPTS are specified in the environment, they are usually meant for the application
// itself rather than sbt, but they are not passed by default to the application, which is a forked
// process. This passes them through to the "re-start" command, which is probably what a developer
// would normally expect.
javaOptions in reStart ++= sys.env.get("JAVA_OPTS").map(_.split(" ").toSeq).getOrElse(Seq.empty)