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


