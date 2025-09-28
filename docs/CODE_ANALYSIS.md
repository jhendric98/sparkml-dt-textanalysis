# Code Analysis

This document summarises the structure of the Spark ML SMS spam classifier and highlights notable implementation details introduced during the refactor.

## High-level architecture

```
┌──────────────┐    ┌──────────────┐    ┌─────────────────────┐    ┌─────────────────┐    ┌─────────────────────┐
│   Ingestion  │ -> │ Normalisation│ -> │ Feature Engineering │ -> │  Model Training │ -> │ Evaluation & Preview │
└──────────────┘    └──────────────┘    └─────────────────────┘    └─────────────────┘    └─────────────────────┘
```

1. **Ingestion** – `SparkSession.read.csv` loads a tab-separated file with the schema `(rawlabel, rawsms)`.
2. **Normalisation** – UDFs clean the label and SMS text, remove illegal characters, lower-case the input and compute a numeric label column.
3. **Feature engineering** – Tokenisation, hashing TF, and IDF transform the cleaned text into TF-IDF vectors.
4. **Model training** – A `DecisionTreeClassifier` is trained using the preprocessed features. Label indexing ensures consistent label mapping across splits.
5. **Evaluation and preview** – The app reports accuracy and F1 metrics and optionally shows sample predictions for manual inspection.

## Key implementation notes

- **SparkSession-first** – The refactor replaces legacy `SparkContext`/`SQLContext` usage with `SparkSession` and fully relies on the 3.x API.
- **Data validation** – Empty or malformed rows are dropped prior to training; the application fails fast if no valid rows remain.
- **Configurable pipeline** – All critical parameters (test split, feature count, tree depth, preview size, app name) are exposed via `scopt` so the job can be tuned without code changes.
- **Pipeline encapsulation** – Tokenisation, TF-IDF, label handling, and model training are part of a `Pipeline`, ensuring consistent processing for training and evaluation datasets.
- **Deterministic split** – Random split uses a constant seed for reproducibility between runs.
- **Metric reporting** – `MulticlassClassificationEvaluator` captures both accuracy and F1 score, providing insight into class balance.

## Opportunities for further improvement

- Introduce automated tests using Spark's local mode to validate preprocessing UDFs and pipeline metrics.
- Persist the trained model to storage and provide a CLI option to reload and score new data.
- Experiment with additional models (e.g., Gradient-Boosted Trees or Linear SVM) or feature extractors (e.g., `CountVectorizer`).
- Add structured logging and integrate with monitoring solutions when running in production environments.

