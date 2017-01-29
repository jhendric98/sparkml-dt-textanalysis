name := "sparkml-dt-textanalysis"

version := "1.0"

scalaVersion := "2.11.8"

// Support for csv/tsv data writes
libraryDependencies += "com.databricks" % "spark-csv_2.11" % "1.5.0"

// Support for command line options
libraryDependencies += "com.github.scopt" % "scopt_2.11" % "3.5.0"

// Spark import for local run
//libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.0.2"
//libraryDependencies += "org.apache.spark" % "spark-mllib_2.11" % "2.0.2"


// Spark import for cluster run
//  (uncomment above and comment here to eof for local runs)

libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.0.2" % "provided"
libraryDependencies += "org.apache.spark" % "spark-mllib_2.11" % "2.0.2" % "provided"


dependencyOverrides += "org.scala-lang" % "scala-compiler" % scalaVersion.value
dependencyOverrides += "org.scala-lang" % "scala-library" % scalaVersion.value
dependencyOverrides += "commons-net" % "commons-net" % "3.1"


assemblyMergeStrategy in assembly := {
  case PathList("org", "apache", "spark", "unused", "UnusedStubClass.class") => MergeStrategy.discard
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
  case PathList("org", "apache", xs @ _*) => MergeStrategy.last
  case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
  case PathList("org", "eclipse", xs @ _*) => MergeStrategy.discard
  case PathList("org", "joda", "time", "base", xs @ _*) => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

assemblyShadeRules in assembly := Seq(ShadeRule.rename("com.google.**" -> "shadeio.@1").inAll)

    