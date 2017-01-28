import org.apache.log4j.{Level, LogManager}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

/**
  * Sample Spark Assembly example template for Spark on YARN
  *   Created for use in IntelliJ for developing Spark applications.
  *
  *   This code is free for use. If you find issues, have questions, or
  *   have a suggestion to improve please contact me.
  *
  */
object sparkml_dt_textanalysis {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf()
      .setAppName("assemblyexample")
      .setMaster("local[*]") //comment this line for cluster runs

    val sc = new SparkContext(sparkConf)
    val sqlContext: SQLContext = new SQLContext(sc)
    LogManager.getRootLogger.setLevel(Level.ERROR)
    import sqlContext.implicits._

    // TODO: Overwrite the next line with your Spark code.
    println("Hello World!")




  }
}
