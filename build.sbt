import sbt.Keys.{testFrameworks, version}

lazy val comunica_version = "1.22.3"
lazy val comunica_version_http_proxy = "1.22.1"
lazy val comunica_version_bus_query_operation = "1.22.0"
lazy val comunica_version_logger_pretty = "1.22.0"

def getPackageSetting = Seq(
  name := "comunica-actor-init-sparql-rdfjs",
  version :=  scala.util.Properties.envOrElse("PROG_VERSION", comunica_version ),
  scalaVersion := "2.13.5",
  organization := "com.github.p2m2",
  organizationName := "p2m2",
  organizationHomepage := Some(url("https://www6.inrae.fr/p2m2")),
  licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php")),
  homepage := Some(url("https://github.com/p2m2")),
  description := "Scalajs Facade for rdfxml-streaming-parser",
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/p2m2/comunica-actor-init-sparql-rdfjs-facade"),
      "scm:git@github.com:p2m2/comunica-actor-init-sparql-rdfjs-facade.git"
    )
  ),
  developers := List(
    Developer("ofilangi", "Olivier Filangi", "olivier.filangi@inrae.fr",url("https://github.com/ofilangi"))
  ),
  credentials += {

    val realm = scala.util.Properties.envOrElse("REALM_CREDENTIAL", "" )
    val host = scala.util.Properties.envOrElse("HOST_CREDENTIAL", "" )
    val login = scala.util.Properties.envOrElse("LOGIN_CREDENTIAL", "" )
    val pass = scala.util.Properties.envOrElse("PASSWORD_CREDENTIAL", "" )

    val file_credential = Path.userHome / ".sbt" / ".credentials"

    if (reflect.io.File(file_credential).exists) {
      Credentials(file_credential)
    } else {
      Credentials(realm,host,login,pass)
    }
  },
  publishTo := {
    if (isSnapshot.value)
      Some("Sonatype Snapshots Nexus" at "https://oss.sonatype.org/content/repositories/snapshots")
    else
      Some("Sonatype Snapshots Nexus" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
  },
  publishConfiguration := publishConfiguration.value.withOverwrite(true) ,
  publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true),
  pomIncludeRepository := { _ => false },
  publishMavenStyle := true,
)

lazy val root = project.in(file(".")).
  enablePlugins(ScalaJSPlugin).
  enablePlugins(ScalaJSBundlerPlugin).
  // add the `it` configuration
  configs(IntegrationTest).
  // add `it` tasks
  settings(Defaults.itSettings: _*).
  // add Scala.js-specific settings and tasks to the `it` configuration
  settings(inConfig(IntegrationTest)(ScalaJSPlugin.testConfigSettings): _*).
  settings(
    getPackageSetting,
    Compile / fastOptJS / scalaJSLinkerConfig ~= {
      _.withOptimizer(false)
        .withPrettyPrint(true)
        .withSourceMap(true)
    },
    Compile / fullOptJS / scalaJSLinkerConfig ~= {
      _.withSourceMap(false)
        .withModuleKind(ModuleKind.CommonJSModule)
    },
    webpackBundlingMode := BundlingMode.LibraryAndApplication(),
    Compile / npmDependencies ++= Seq(
      "@comunica/actor-init-sparql" -> comunica_version,
      "@comunica/bus-query-operation" -> comunica_version_bus_query_operation ,
      "@comunica/logger-pretty" -> comunica_version_logger_pretty,
      "@comunica/actor-http-proxy" -> comunica_version_http_proxy ),
    libraryDependencies ++= Seq(
      "net.exoego" %%% "scala-js-nodejs-v14" % "0.14.0",
      "com.github.p2m2" %%% "data-model-rdfjs" % "1.0.1",
      "com.github.p2m2" %%% "n3js" % "1.13.0",
      "com.lihaoyi" %%% "utest" % "0.7.11" % "test"
    ) ,
    testFrameworks += new TestFramework("utest.runner.Framework"),
    coverageMinimumStmtTotal := 20,
    coverageFailOnMinimum := false,
    coverageHighlighting := true
  )

Global / onChangedBuildSource := ReloadOnSourceChanges
