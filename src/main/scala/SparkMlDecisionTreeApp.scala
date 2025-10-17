import org.apache.log4j.{Level, LogManager}

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{HashingTF, IDF, IndexToString, StringIndexer, Tokenizer}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, length, udf}
import org.apache.spark.sql.types.{StringType, StructField, StructType}

/** Entry point for the SMS spam classification example using Spark ML.
  *
  * The application trains a Decision Tree classifier on the SMS Spam Collection dataset.
  * It performs basic text cleaning, vectorisation using TF-IDF and reports
  * accuracy and F1 metrics on a hold-out test set.
  */
object SparkMlDecisionTreeApp {

  private case class Config(
      inputPath: String = "",
      appName: String = "SparkML Decision Tree Text Analysis",
      testFraction: Double = 0.3,
      numFeatures: Int = 1 << 12,
      maxDepth: Int = 5,
      preview: Int = 20
  )

  private val parser = new scopt.OptionParser[Config]("sparkml-dt-textanalysis") {
    head("SparkML Decision Tree Text Analysis", "1.1.0")

    opt[String]("input")
      .required()
      .valueName("<path>")
      .action((path, cfg) => cfg.copy(inputPath = path))
      .text("Path to the SMS Spam TSV dataset (local path, HDFS or GCS URI).")

    opt[String]("app-name")
      .optional()
      .valueName("<name>")
      .action((name, cfg) => cfg.copy(appName = name))
      .text("Optional Spark application name.")

    opt[Double]("test-fraction")
      .optional()
      .validate { value =>
        if (value > 0.0 && value < 1.0) success
        else failure("Test fraction must be in the (0.0, 1.0) interval")
      }
      .action((value, cfg) => cfg.copy(testFraction = value))
      .text("Fraction of the dataset reserved for the evaluation split (default: 0.3).")

    opt[Int]("num-features")
      .optional()
      .validate { value =>
        if (value > 0) success else failure("Number of features must be a positive integer")
      }
      .action((value, cfg) => cfg.copy(numFeatures = value))
      .text("Number of features used by the hashing transformer (default: 4096).")

    opt[Int]("max-depth")
      .optional()
      .validate { value =>
        if (value > 0) success else failure("Tree depth must be a positive integer")
      }
      .action((value, cfg) => cfg.copy(maxDepth = value))
      .text("Maximum depth for the decision tree (default: 5).")

    opt[Int]("preview")
      .optional()
      .validate { value =>
        if (value >= 0 && value <= 1000) success else failure("Preview rows must be between 0 and 1000")
      }
      .action((value, cfg) => cfg.copy(preview = value))
      .text("Number of prediction examples to display (default: 20, set to 0 to disable).")

    help("help").text("Prints this usage text.")
  }

  def main(args: Array[String]): Unit = {
    parser.parse(args, Config()) match {
      case Some(config) =>
        run(config)
      case None =>
        // Errors are already displayed by scopt, exit with error code.
        sys.exit(1)
    }
  }

  private def run(config: Config): Unit = {
    val spark = SparkSession
      .builder()
      .appName(config.appName)
      .getOrCreate()

    try {
      LogManager.getRootLogger.setLevel(Level.WARN)

      val schema = StructType(
        Array(
          StructField("rawlabel", StringType, nullable = false),
          StructField("rawsms", StringType, nullable = false)
        )
      )

      val cleanLabel = udf((label: String) => label.trim.toLowerCase)
      val normaliseText = udf((text: String) => {
        Option(text)
          .map(_.toLowerCase.replaceAll("[^a-z0-9! ]", " ").replaceAll("\\s{2,}", " ").trim)
          .getOrElse("")
      })

      val labelToDouble = udf((label: String) =>
        label match {
          case "ham"  => 0.0
          case "spam" => 1.0
          case _      => 0.0
        }
      )

      val rawDf = spark.read
        .schema(schema)
        .option("header", "false")
        .option("delimiter", "\t")
        .option("mode", "DROPMALFORMED")
        .csv(config.inputPath)

      val preparedDf = rawDf
        .withColumn("rawlabel", cleanLabel(col("rawlabel")))
        .withColumn("cleansms", normaliseText(col("rawsms")))
        .withColumn("label", labelToDouble(col("rawlabel")))
        .filter(length(col("cleansms")) > 0)
        .cache()

      if (preparedDf.isEmpty) {
        throw new IllegalStateException(
          s"No valid rows were found in '${config.inputPath}'. Ensure the TSV has both label and sms columns."
        )
      }

      val labelIndexerModel = new StringIndexer()
        .setInputCol("rawlabel")
        .setOutputCol("indexedLabel")
        .fit(preparedDf)

      val tokenizer = new Tokenizer()
        .setInputCol("cleansms")
        .setOutputCol("words")

      val hashingTF = new HashingTF()
        .setInputCol("words")
        .setOutputCol("rawFeatures")
        .setNumFeatures(config.numFeatures)

      val idf = new IDF()
        .setInputCol("rawFeatures")
        .setOutputCol("features")

      val decisionTree = new DecisionTreeClassifier()
        .setLabelCol("indexedLabel")
        .setFeaturesCol("features")
        .setMaxDepth(config.maxDepth)

      val labelConverter = new IndexToString()
        .setInputCol("prediction")
        .setOutputCol("predictedLabel")
        .setLabels(labelIndexerModel.labels)

      val pipeline = new Pipeline().setStages(
        Array(labelIndexerModel, tokenizer, hashingTF, idf, decisionTree, labelConverter)
      )

      val Array(trainingData, testData) = preparedDf.randomSplit(
        Array(1.0 - config.testFraction, config.testFraction),
        seed = 42L
      )

      val model = pipeline.fit(trainingData)
      val predictions = model.transform(testData).cache()

      val evaluator = new MulticlassClassificationEvaluator()
        .setLabelCol("indexedLabel")
        .setPredictionCol("prediction")

      val accuracy = evaluator.setMetricName("accuracy").evaluate(predictions)
      val f1Score = evaluator.setMetricName("f1").evaluate(predictions)

      println(s"Accuracy: ${"%.4f".format(accuracy)}")
      println(s"F1 score: ${"%.4f".format(f1Score)}")

      if (config.preview > 0) {
        predictions
          .select("rawlabel", "predictedLabel", "rawsms")
          .withColumn("match", col("rawlabel") === col("predictedLabel"))
          .show(config.preview, truncate = false)
      }

    } finally {
      spark.stop()
    }
  }
}
