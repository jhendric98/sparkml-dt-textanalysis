ThisBuild / name := "sparkml-dt-textanalysis"

ThisBuild / version := "1.1.0"

ThisBuild / scalaVersion := "2.12.18"

lazy val sparkVersion = "3.5.1"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % sparkVersion % Provided,
  "org.apache.spark" %% "spark-mllib" % sparkVersion % Provided,
  "com.github.scopt" %% "scopt" % "4.1.0"
)

Compile / console / initialCommands := "import SparkMlDecisionTreeApp._"
