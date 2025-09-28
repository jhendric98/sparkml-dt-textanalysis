# Spark ML Decision Tree SMS Spam Classifier

This project demonstrates how to train and evaluate a binary Decision Tree classifier on the [SMS Spam Collection](https://archive.ics.uci.edu/ml/datasets/SMS+Spam+Collection) dataset using Apache Spark ML. The example has been modernised to use the Spark 3.x API, provides command-line configuration, and includes tooling to download and prepare the dataset locally.

## Features

- Cleans and vectorises raw SMS text with Tokeniser, HashingTF and IDF.
- Builds a Decision Tree classifier and reports accuracy and F1 score on a configurable test split.
- Supports local execution or submission to a cluster (Dataproc, EMR, Spark Standalone, etc.).
- Provides a Python helper (managed with [uv](https://github.com/astral-sh/uv)) to download and inspect the UCI dataset.

## Requirements

- Java 11+
- [sbt](https://www.scala-sbt.org/) 1.9+
- Apache Spark 3.5 runtime (cluster provided libraries or local install)
- [uv](https://github.com/astral-sh/uv) 0.2+ (for optional dataset utilities)

## Quick start

1. **Download the dataset (optional but recommended).**

   ```bash
   uv sync
   uv run download-sms-spam --output data/SMSSpam.tsv
   ```

   The command downloads the official dataset archive, extracts the SMS corpus and writes a clean TSV file compatible with the Spark job. Files are stored under `data/` (which is ignored by Git).

2. **Build the assembly JAR.**

   ```bash
   sbt clean assembly
   ```

   The resulting fat JAR will be available under `target/scala-2.12/sparkml-dt-textanalysis-assembly-1.1.0.jar`.

3. **Run the job locally.**

   ```bash
   spark-submit \
     --class SparkMlDecisionTreeApp \
     --master local[*] \
     target/scala-2.12/sparkml-dt-textanalysis-assembly-1.1.0.jar \
     --input data/SMSSpam.tsv \
     --test-fraction 0.25 \
     --max-depth 6 \
     --preview 10
   ```

   Adjust the options to match your environment or cluster launcher. When targeting a managed cluster (Dataproc, EMR, etc.) upload the JAR and dataset to a storage bucket accessible by the cluster and reference their URIs in the `--input` flag.

## Command-line options

| Option | Description |
| ------ | ----------- |
| `--input <path>` | **Required.** Path to the SMS Spam TSV file. Supports local files, HDFS and cloud storage URIs. |
| `--app-name <name>` | Optional Spark application name. |
| `--test-fraction <0-1>` | Fraction of data reserved for evaluation (default `0.3`). |
| `--num-features <n>` | Feature space size for `HashingTF` (default `4096`). |
| `--max-depth <n>` | Decision Tree maximum depth (default `5`). |
| `--preview <n>` | Number of prediction examples to display (default `20`, set to `0` to skip).

Run `spark-submit ... --help` to see the generated usage information.

## Project layout

- `src/main/scala/SparkMlDecisionTreeApp.scala` – main entry point, data preparation and model pipeline.
- `scripts/download_dataset.py` – helper utility to fetch the dataset (exposed as `download-sms-spam`).
- `docs/CODE_ANALYSIS.md` – architectural and code analysis notes covering the processing pipeline.

## Running on Google Cloud Dataproc (example)

1. Upload `data/SMSSpam.tsv` and the assembly JAR to a Cloud Storage bucket.
2. Create a Dataproc cluster with Spark 3.5.
3. Submit the job referencing the uploaded artefacts:

   ```bash
   gcloud dataproc jobs submit spark \
     --cluster=<cluster-name> \
     --region=<region> \
     --class=SparkMlDecisionTreeApp \
     --jars=gs://<bucket>/sparkml-dt-textanalysis-assembly-1.1.0.jar \
     -- \
     --input gs://<bucket>/SMSSpam.tsv \
     --test-fraction 0.3
   ```

4. Review the driver output for metrics and sample predictions.

## Development tips

- The Spark log level is reduced to `WARN` for clarity; adjust it by editing `SparkMlDecisionTreeApp` if you need detailed logs.
- The dataset loader drops malformed rows automatically; if you need stricter validation customise the `normaliseText` and `labelToDouble` UDFs.
- Consider experimenting with alternative feature transformers (e.g. `CountVectorizer`) or model types by extending the existing pipeline.

