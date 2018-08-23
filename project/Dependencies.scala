import sbt._

object Dependencies {
  val jacksonV        = "2.9.5"
  val googleV         = "1.23.0"
  val scalaLoggingV   = "3.9.0"
  val scalaTestV      = "3.0.5"
  val slickV          = "3.2.3"
  val postgresDriverV = "42.2.4"
  val socketFactoryV  = "1.0.10"
  val dbcpV           = "2.5.0"

  val workbenchUtilV    = "0.3-0e9d080"
  val workbenchModelV   = "0.11-2ce3359"
  val workbenchGoogleV  = "0.16-c5b80d2"
  val workbenchMetricsV = "0.3-c5b80d2"

  val samV =  "1.0-5cdffb4"

  val excludeAkkaActor =        ExclusionRule(organization = "com.typesafe.akka", name = "akka-actor_2.12")

  val excludeGuavaJDK5 =        ExclusionRule(organization = "com.google.guava", name = "guava-jdk5")
  val excludeWorkbenchUtil =    ExclusionRule(organization = "org.broadinstitute.dsde.workbench", name = "workbench-util_2.12")
  val excludeWorkbenchModel =   ExclusionRule(organization = "org.broadinstitute.dsde.workbench", name = "workbench-model_2.12")
  val excludeWorkbenchMetrics = ExclusionRule(organization = "org.broadinstitute.dsde.workbench", name = "workbench-metrics_2.12")

  val jacksonAnnotations: ModuleID = "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonV
  val jacksonDatabind: ModuleID =    "com.fasterxml.jackson.core" % "jackson-databind"    % jacksonV
  val jacksonCore: ModuleID =        "com.fasterxml.jackson.core" % "jackson-core"        % jacksonV
  val jacksonScalaModule: ModuleID = "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.6"

  val logbackClassic: ModuleID = "ch.qos.logback"             %  "logback-classic" % "1.2.3"
  val ravenLogback: ModuleID =   "com.getsentry.raven"        %  "raven-logback"   % "8.0.3"
  val scalaLogging: ModuleID =   "com.typesafe.scala-logging" %% "scala-logging"   % scalaLoggingV
  val swaggerUi: ModuleID =      "org.webjars"                %  "swagger-ui"      % "2.2.5"
  val ficus: ModuleID =          "com.iheart"                 %% "ficus"           % "1.4.3"
  val cats: ModuleID =           "org.typelevel"              %% "cats"            % "0.9.0"
  val httpClient: ModuleID =     "org.apache.httpcomponents"  %  "httpclient"       % "4.5.5"  // upgrading a transitive dependency to avoid security warnings
  val enumeratum: ModuleID =     "com.beachape"               %% "enumeratum"      % "1.5.13"

  val unfilteredFilter = "ws.unfiltered" %% "unfiltered-filter" % "0.9.1"
  val javaxServlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"

  val googleEndpointsFramework = "com.google.endpoints" % "endpoints-framework" % "2.1.1"
  val googleEndpointsManagementControl = "com.google.endpoints" % "endpoints-management-control-appengine" % "1.0.8"
  val googleEndpointsAuth = "com.google.endpoints" % "endpoints-framework-auth" % "1.0.8"
  val googleLogging = "com.google.cloud" % "google-cloud-logging" % "1.35.0"

  val googleAppEngine = "com.google.appengine" % "appengine-api-1.0-sdk" % "1.9.64"

  val googleRpc: ModuleID = "io.grpc" % "grpc-core" % "1.12.0"
  val googleOAuth2: ModuleID = "com.google.auth" % "google-auth-library-oauth2-http" % "0.9.1"
  val googleSourceRepositories: ModuleID = "com.google.apis" % "google-api-services-sourcerepo" % s"v1-rev21-$googleV" excludeAll(excludeGuavaJDK5)


  val scalaTest: ModuleID = "org.scalatest" %% "scalatest"    % scalaTestV % "test"
  val mockito: ModuleID =   "org.mockito"    % "mockito-core" % "2.18.3"   % "test"

  // Exclude workbench-libs transitive dependencies so we can control the library versions individually.
  // workbench-google pulls in workbench-{util, model, metrics} and workbench-metrics pulls in workbench-util.
  val workbenchUtil: ModuleID =      "org.broadinstitute.dsde.workbench" %% "workbench-util"    % workbenchUtilV
  val workbenchModel: ModuleID =     "org.broadinstitute.dsde.workbench" %% "workbench-model"   % workbenchModelV
  val workbenchGoogle: ModuleID =    "org.broadinstitute.dsde.workbench" %% "workbench-google"  % workbenchGoogleV excludeAll(excludeWorkbenchUtil, excludeWorkbenchModel, excludeWorkbenchMetrics)
  val workbenchGoogleTests: ModuleID = "org.broadinstitute.dsde.workbench" %% "workbench-google" % workbenchGoogleV % "test" classifier "tests" excludeAll(excludeWorkbenchUtil, excludeWorkbenchModel)
  val workbenchMetrics: ModuleID =   "org.broadinstitute.dsde.workbench" %% "workbench-metrics" % workbenchMetricsV excludeAll(excludeWorkbenchUtil)

  val sam: ModuleID = "org.broadinstitute.dsde.sam-client" %% "sam" % samV

  val slick: ModuleID =     "com.typesafe.slick" %% "slick"                 % slickV
  val dbcp2: ModuleID = "org.apache.commons" % "commons-dbcp2" % dbcpV
  val liquibase: ModuleID = "org.liquibase"       % "liquibase-core"        % "3.5.3"

  val postgresDriver: ModuleID = "org.postgresql" % "postgresql" % postgresDriverV
  val socketFactory: ModuleID = "com.google.cloud.sql" % "postgres-socket-factory" % socketFactoryV

  val rootDependencies = Seq(
    // proactively pull in latest versions of Jackson libs, instead of relying on the versions
    // specified as transitive dependencies, due to OWASP DependencyCheck warnings for earlier versions.
    jacksonAnnotations,
    jacksonDatabind,
    jacksonCore,
    jacksonScalaModule,

    logbackClassic,
    ravenLogback,
    scalaLogging,
    swaggerUi,
    ficus,
    cats,
    httpClient,
    enumeratum,

    unfilteredFilter,
    javaxServlet,

    googleEndpointsFramework,
    googleEndpointsManagementControl,
    googleEndpointsAuth,
    googleLogging,

    googleAppEngine,

    googleRpc,
    googleOAuth2,
    googleSourceRepositories,

    scalaTest,

    slick,
    postgresDriver,
    socketFactory,
    dbcp2,
    liquibase,

    workbenchUtil,
    workbenchModel,
    workbenchGoogle,
    workbenchGoogleTests,
    workbenchMetrics,
    sam
  )
}