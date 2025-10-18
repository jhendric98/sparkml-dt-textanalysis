ThisBuild / name         := "sparkml-dt-textanalysis"
ThisBuild / version      := "1.1.0"
ThisBuild / scalaVersion := "2.12.19"

// Project metadata
ThisBuild / organization         := "com.example"
ThisBuild / organizationName     := "Spark ML Decision Tree Text Analysis"
ThisBuild / organizationHomepage := Some(url("https://github.com/example/sparkml-dt-textanalysis"))
ThisBuild / description          := "SMS spam classification using Apache Spark ML Decision Trees"
ThisBuild / licenses             := List("MIT" -> url("https://opensource.org/licenses/MIT"))
ThisBuild / homepage             := Some(url("https://github.com/example/sparkml-dt-textanalysis"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/example/sparkml-dt-textanalysis"),
    "scm:git@github.com:example/sparkml-dt-textanalysis.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id = "maintainer",
    name = "Project Maintainer",
    email = "maintainer@example.com",
    url = url("https://github.com/example")
  )
)

lazy val sparkVersion = "3.5.3"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql"   % sparkVersion % Provided,
  "org.apache.spark" %% "spark-mllib" % sparkVersion % Provided,
  "com.github.scopt" %% "scopt"       % "4.1.0"
)

// Assembly configuration
assembly / assemblyJarName := s"${name.value}-assembly-${version.value}.jar"
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case "application.conf"            => MergeStrategy.concat
  case "reference.conf"              => MergeStrategy.concat
  case _                             => MergeStrategy.first
}

// Scalafmt and Scalafix integration
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

Compile / console / initialCommands := "import SparkMlDecisionTreeApp._"

// Custom tasks for code quality
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
addCommandAlias("fmtCheck", "all scalafmtSbtCheck scalafmtCheckAll")
addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias("fixCheck", "all compile:scalafix --check test:scalafix --check")
