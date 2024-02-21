import sbt.Keys.{testFrameworks, version}

lazy val comunica_version = "2.10.2"

def getPackageSetting = Seq(
  name := "comunica-query-sparql",
  version :=  scala.util.Properties.envOrElse("PROG_VERSION", comunica_version),
  scalaVersion := "2.13.12",
  versionScheme := Some("early-semver"),
  organization := "com.github.p2m2",
  organizationName := "p2m2",
  organizationHomepage := Some(url("https://www6.inrae.fr/p2m2")),
  licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php")),
  homepage := Some(url("https://github.com/p2m2")),
  description := "Scalajs Facade for @comunica/query-sparql",
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/p2m2/facade-scalajs-comunica-query-sparql"),
      "scm:git@github.com:p2m2/facade-scalajs-comunica-query-sparql.git"
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
  publishTo := sonatypePublishToBundle.value,
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
      "@comunica/query-sparql" ->  comunica_version,
    ),
    libraryDependencies ++= Seq(
      ("org.scala-js" %%% "scalajs-java-securerandom" % "1.0.0").cross(CrossVersion.for3Use2_13) % "test",
      "org.scala-js" %%% "scala-js-macrotask-executor" % "1.0.0" % "test",
      "com.github.p2m2" %%% "n3js" % "v1.17.2",
      "com.lihaoyi" %%% "utest" % "0.8.2" % "test"
    ) ,
    testFrameworks += new TestFramework("utest.runner.Framework"),
    coverageMinimumStmtTotal := 20,
    coverageFailOnMinimum := false,
    coverageHighlighting := true
  )

Global / onChangedBuildSource := ReloadOnSourceChanges
