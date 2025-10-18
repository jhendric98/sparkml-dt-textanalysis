# Contributing to Spark ML Decision Tree Text Analysis

Thank you for your interest in contributing to this project! We welcome contributions from the community and are pleased to have you join us.

## Code of Conduct

This project adheres to a [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code.

## How to Contribute

### Reporting Issues

Before creating an issue, please:

1. **Search existing issues** to avoid duplicates
2. **Use the appropriate issue template** for bug reports, feature requests, or questions
3. **Provide detailed information** including:
   - Environment details (Java version, Spark version, OS)
   - Steps to reproduce (for bugs)
   - Expected vs actual behavior
   - Relevant logs or error messages

### Submitting Changes

1. **Fork the repository** and create a new branch from `main`
2. **Make your changes** following the coding standards below
3. **Test your changes** thoroughly
4. **Run the linters** to ensure code quality
5. **Submit a pull request** using the provided template

### Development Setup

#### Prerequisites

- Java 17+
- sbt 1.10+
- Apache Spark 3.5+ (for testing)
- uv 0.2+ (for Python utilities)

#### Local Development

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/sparkml-dt-textanalysis.git
cd sparkml-dt-textanalysis

# Install Python dependencies
uv sync --extra dev

# Build the project
sbt clean compile

# Run tests
sbt test

# Format code
sbt fmt          # Formats Scala code
uv run black scripts/  # Formats Python code

# Run linters
sbt fixCheck     # Check Scala linting
uv run flake8 scripts/  # Check Python linting
uv run mypy scripts/    # Python type checking
```

**📖 For comprehensive local development instructions, see [docs/LOCAL_DEVELOPMENT.md](docs/LOCAL_DEVELOPMENT.md)**

## Coding Standards

### Scala Code

- Follow the configured Scalafmt formatting rules
- Use meaningful variable and function names
- Add appropriate comments for complex logic
- Ensure all public methods have documentation
- Follow functional programming principles where appropriate

### Python Code

- Use Black for code formatting
- Follow PEP 8 style guidelines
- Add type hints where appropriate
- Include docstrings for functions and classes

### Commit Messages

- Use clear, descriptive commit messages
- Start with a verb in the imperative mood (e.g., "Add", "Fix", "Update")
- Keep the first line under 50 characters
- Include additional details in the body if necessary

Example:

```text
Add feature extraction pipeline for SMS text

- Implement tokenization with stop word removal
- Add TF-IDF vectorization with configurable features
- Include unit tests for text preprocessing
```

## Pull Request Process

1. **Update documentation** if your changes affect the API or user interface
2. **Add tests** for new functionality
3. **Ensure all checks pass** (build, tests, linting)
4. **Request review** from maintainers
5. **Address feedback** promptly and professionally

### Pull Request Checklist

- [ ] Code follows the project's style guidelines
- [ ] Self-review of the code has been performed
- [ ] Code is commented, particularly in hard-to-understand areas
- [ ] Corresponding changes to documentation have been made
- [ ] Tests have been added that prove the fix is effective or the feature works
- [ ] New and existing unit tests pass locally
- [ ] Any dependent changes have been merged and published

## Release Process

Releases are managed by project maintainers and follow semantic versioning:

- **Patch** (1.1.1): Bug fixes and minor improvements
- **Minor** (1.2.0): New features that are backward compatible
- **Major** (2.0.0): Breaking changes

## Getting Help

- **Documentation**: Check the README and docs/ directory
- **Issues**: Search existing issues or create a new one
- **Discussions**: Use GitHub Discussions for general questions

## Recognition

Contributors will be recognized in:

- The project's contributor list
- Release notes for significant contributions
- Special recognition for first-time contributors

Thank you for contributing to make this project better!
