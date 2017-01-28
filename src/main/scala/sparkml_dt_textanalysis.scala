import org.apache.log4j.{Level, LogManager}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SQLContext, UserDefinedFunction}
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.functions._
import org.apache.spark.ml.feature.{HashingTF, IDF, Tokenizer}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer}

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

    // Schema for data file
    val dataSchema = StructType(Array(
      StructField("rawlabel", StringType, true),
      StructField("rawsms", StringType, true)
    ))

    // load text file with labels
    val inputDF = sqlContext.read
      .format("com.databricks.spark.csv")
      .schema(dataSchema)
      .option("header", "false")
      .option("delimiter", "\t")
      .load("data/SMSSpam.tsv")

    val createLabel = udf((term: String) => {
      if ( term =="ham" ) 0.0
      else if (term == "spam") 1.0
      else 0.0
    } )

    val removeQuotes = udf((term: String) => {
      term
        .toLowerCase
        .replaceAll("""[^a-zA-Z0-9! ]""", " ")
        .replaceAll("\\s{2,}"," ")
        .trim
    })

    val dataDF = inputDF
      .withColumn("label", createLabel($"rawlabel"))
      .withColumn("cleansms", removeQuotes($"rawsms"))


    // encode the data using TF/IDF
    val tokenizer = new Tokenizer().setInputCol("cleansms").setOutputCol("words")
    val wordsData = tokenizer.transform(dataDF)


    val hashingTF = new HashingTF()
      .setInputCol("words").setOutputCol("rawFeatures").setNumFeatures(1000)

    val featurizedData = hashingTF.transform(wordsData)

    val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
    val idfModel = idf.fit(featurizedData)

    val readyDF = idfModel.transform(featurizedData)


    // Index labels, adding metadata to the label column.
    // Fit on whole dataset to include all labels in index.
    val labelIndexer = new StringIndexer()
      .setInputCol("rawlabel")
      .setOutputCol("indexedLabel")
      .fit(readyDF)
    // Automatically identify categorical features, and index them.
    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      .setMaxCategories(1000) // features with > 4 distinct values are treated as continuous.
      .fit(readyDF)

    // Split the data into training and test sets (30% held out for testing).
    val Array(trainingData, testData) = readyDF.randomSplit(Array(0.7, 0.3))

    // Train a DecisionTree model.
    val dt = new DecisionTreeClassifier()
      .setLabelCol("indexedLabel")
      .setFeaturesCol("indexedFeatures")

    // Convert indexed labels back to original labels.
    val labelConverter = new IndexToString()
      .setInputCol("prediction")
      .setOutputCol("predictedLabel")
      .setLabels(labelIndexer.labels)

    // Chain indexers and tree in a Pipeline.
    val pipeline = new Pipeline()
      .setStages(Array(labelIndexer, featureIndexer, dt, labelConverter))

    // Train model. This also runs the indexers.
    val model = pipeline.fit(trainingData)

    // Make predictions.
    val predictions = model.transform(testData)

    // Select example rows to display.
    predictions.select("rawlabel","predictedLabel","rawsms").show(5000)


  }
}
