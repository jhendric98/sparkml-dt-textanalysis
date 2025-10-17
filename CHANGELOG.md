# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- GitHub compliance upgrade with comprehensive templates and workflows
- MIT License
- Code of Conduct and Contributing guidelines
- Comprehensive issue templates (Bug Report, Feature Request, Question, Performance Issue)
- Pull Request template with detailed checklist
- Scalafmt and Scalafix integration for code quality
- Python linting tools (Black, flake8, mypy)
- GitHub Actions CI/CD workflows
- Dependabot configuration for automated dependency updates
- Security scanning with Trivy
- Enhanced .gitignore with comprehensive patterns

### Changed
- Updated build.sbt with proper project metadata and SCM information
- Enhanced pyproject.toml with development dependencies and tool configurations
- Updated SECURITY.md with proper version support table

## [1.1.0] - 2024-10-17

### Added
- Command-line configuration with scopt library
- Configurable test fraction, number of features, and tree depth
- Preview functionality for prediction examples
- Support for cloud storage URIs (HDFS, GCS)
- Python helper script for dataset download using uv
- Comprehensive documentation and usage examples

### Changed
- Upgraded to Spark 3.5.1
- Modernized to use Spark 3.x API
- Improved text preprocessing with UDFs
- Enhanced error handling and validation

### Fixed
- Proper handling of malformed data rows
- Improved memory management with caching strategy

## [1.0.0] - Initial Release

### Added
- Basic SMS spam classification using Decision Tree
- TF-IDF feature extraction pipeline
- Evaluation metrics (accuracy and F1 score)
- Assembly JAR build configuration
- Basic documentation
