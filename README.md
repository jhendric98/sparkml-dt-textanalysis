# Spark ML Decision Tree SMS Spam Classifier

[![CI](https://github.com/jhendric98/sparkml-dt-textanalysis/actions/workflows/ci.yml/badge.svg)](https://github.com/YOUR_USERNAME/sparkml-dt-textanalysis/actions/workflows/ci.yml)
[![Code Quality](https://github.com/jhendric98/sparkml-dt-textanalysis/actions/workflows/code-quality.yml/badge.svg)](https://github.com/YOUR_USERNAME/sparkml-dt-textanalysis/actions/workflows/code-quality.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Scala Version](https://img.shields.io/badge/scala-2.12.18-red.svg)](https://scala-lang.org/)
[![Spark Version](https://img.shields.io/badge/spark-3.5.1-orange.svg)](https://spark.apache.org/)

This project demonstrates how to train and evaluate a binary Decision Tree classifier on the [SMS Spam Collection](https://archive.ics.uci.edu/ml/datasets/SMS+Spam+Collection) dataset using Apache Spark ML. The example has been modernised to use the Spark 3.x API, provides command-line configuration, and includes tooling to download and prepare the dataset locally.

**🎯 This repository follows GitHub best practices with comprehensive CI/CD, code quality tools, and security scanning.**

## Features

### Machine Learning Pipeline

- Cleans and vectorises raw SMS text with Tokeniser, HashingTF and IDF.
- Builds a Decision Tree classifier and reports accuracy and F1 score on a configurable test split.
- Supports local execution or submission to a cluster (Dataproc, EMR, Spark Standalone, etc.).
- Provides a Python helper (managed with [uv](https://github.com/astral-sh/uv)) to download and inspect the UCI dataset.

### Development & Quality Assurance

- **Code Quality**: Scalafmt and Scalafix for Scala, Black/flake8/mypy for Python
- **CI/CD Pipeline**: Automated testing on Java 11, code quality checks, security scanning
- **Automated Releases**: GitHub releases with semantic versioning
- **Dependency Management**: Dependabot for automated security updates
- **Comprehensive Documentation**: Contributing guidelines, issue templates, security policy

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
| `--preview <n>` | Number of prediction examples to display (default `20`, set to `0` to skip). |

Run `spark-submit ... --help` to see the generated usage information.

## Project Layout

### Core Application

- `src/main/scala/SparkMlDecisionTreeApp.scala` – main entry point, data preparation and model pipeline
- `scripts/download_dataset.py` – helper utility to fetch the dataset (exposed as `download-sms-spam`)
- `build.sbt` – project configuration with metadata, dependencies, and quality tools

### Documentation

- `docs/CODE_ANALYSIS.md` – architectural and code analysis notes covering the processing pipeline
- `docs/LOCAL_DEVELOPMENT.md` – guide for running code quality checks locally
- `docs/GITHUB_COMPLIANCE_SUMMARY.md` – comprehensive overview of implemented GitHub best practices
- `docs/BRANCH_PROTECTION.md` – guidelines for configuring repository security settings
- `CONTRIBUTING.md` – contribution guidelines and development workflow
- `CHANGELOG.md` – version history following semantic versioning

### Configuration & Quality

- `.scalafmt.conf` – Scala code formatting rules
- `.scalafix.conf` – Scala linting and refactoring rules
- `.flake8` – Python linting configuration
- `pyproject.toml` – Python project metadata and tool configurations
- `.github/` – issue templates, PR template, and CI/CD workflows

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

## Development

### Code Quality Tools

This project uses modern tooling to ensure code quality and consistency:

```bash
# Scala formatting and linting
sbt fmt          # Format all Scala code
sbt fmtCheck     # Check formatting without changes
sbt fix          # Apply Scalafix rules
sbt fixCheck     # Check Scalafix rules without changes

# Python formatting and linting
uv sync --extra dev        # Install development dependencies
uv run black scripts/      # Format Python code
uv run flake8 scripts/     # Check Python linting
uv run mypy scripts/       # Type checking
```

**📖 For detailed instructions on running checks locally, see [docs/LOCAL_DEVELOPMENT.md](docs/LOCAL_DEVELOPMENT.md)**

### Development Workflow

1. **Fork and clone** the repository
2. **Create a feature branch** from `main`
3. **Make your changes** following the coding standards
4. **Run quality checks** using the commands above
5. **Test your changes** thoroughly
6. **Submit a pull request** using the provided template

### Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines on:

- Setting up your development environment
- Coding standards and best practices
- Pull request process
- Issue reporting guidelines

### Versioning and Releases

This project follows [Semantic Versioning](https://semver.org/):

- **Patch** (1.1.1): Bug fixes and minor improvements
- **Minor** (1.2.0): New features that are backward compatible  
- **Major** (2.0.0): Breaking changes

Releases are automated through GitHub Actions when tags are pushed:

```bash
# Create and push a new release
git tag v1.2.0
git push origin v1.2.0
```

### Development Tips

- The Spark log level is reduced to `WARN` for clarity; adjust it by editing `SparkMlDecisionTreeApp` if you need detailed logs.
- The dataset loader drops malformed rows automatically; if you need stricter validation customise the `normaliseText` and `labelToDouble` UDFs.
- Consider experimenting with alternative feature transformers (e.g. `CountVectorizer`) or model types by extending the existing pipeline.
- All pull requests are automatically tested on Java 11 to ensure compatibility with Spark 3.5.

## Security

Security is a priority for this project. Please see [SECURITY.md](SECURITY.md) for:

- Supported versions and security update policy
- How to report security vulnerabilities
- Response timeline for security issues

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- **Documentation**: Check the `docs/` directory and this README
- **Issues**: Use our [issue templates](.github/ISSUE_TEMPLATE/) for bug reports, feature requests, or questions
- **Contributing**: See [CONTRIBUTING.md](CONTRIBUTING.md) for development guidelines
- **Security**: Report security issues via [GitHub Security Advisories](https://github.com/YOUR_USERNAME/sparkml-dt-textanalysis/security/advisories/new)
