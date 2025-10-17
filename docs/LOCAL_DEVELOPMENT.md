# Local Development Guide

This guide explains how to run all code quality checks locally before pushing to GitHub.

## Prerequisites

Ensure you have the following installed:

- **Java 11+**: Required for Scala/sbt
- **sbt 1.9+**: Scala build tool
- **Python 3.9+**: For Python utilities
- **uv**: Python package manager

### Installing Prerequisites

#### macOS (using Homebrew)

```bash
# Install Java 11
brew install openjdk@11

# Install sbt
brew install sbt

# Install uv
brew install uv
```

#### Linux (Ubuntu/Debian)

```bash
# Install Java 11
sudo apt-get update
sudo apt-get install openjdk-11-jdk

# Install sbt
echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list
curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x99E82A75642AC823" | sudo apt-key add
sudo apt-get update
sudo apt-get install sbt

# Install uv
curl -LsSf https://astral.sh/uv/install.sh | sh
```

#### Windows (using Chocolatey)

```powershell
# Install Java 11
choco install openjdk11

# Install sbt
choco install sbt

# Install uv
powershell -c "irm https://astral.sh/uv/install.ps1 | iex"
```

## Scala Code Quality Checks

### 1. Check Formatting (without changes)

```bash
sbt scalafmtCheckAll scalafmtSbtCheck
```

This checks if your code is properly formatted according to `.scalafmt.conf` rules.

**Expected output (passing):**
```
[success] Total time: 2 s
```

**Expected output (failing):**
```
[warn] scalafmt: /path/to/file.scala isn't formatted properly!
[error] scalafmt: 1 files must be formatted
```

### 2. Apply Formatting

```bash
sbt scalafmtAll scalafmtSbt
```

Or use the convenient alias:

```bash
sbt fmt
```

This automatically formats all Scala code and sbt build files.

### 3. Check Scalafix Rules (without changes)

```bash
sbt "scalafix --check"
```

Or use the alias:

```bash
sbt fixCheck
```

This checks for code quality issues defined in `.scalafix.conf`.

### 4. Apply Scalafix Rules

```bash
sbt scalafix
```

Or use the alias:

```bash
sbt fix
```

This automatically applies Scalafix rules to improve code quality.

### 5. Compile Code

```bash
sbt compile
```

This compiles your Scala code and checks for syntax errors.

### 6. Run Tests

```bash
sbt test
```

This runs all unit tests (when tests are added to the project).

### 7. Build Assembly JAR

```bash
sbt assembly
```

This creates the fat JAR with all dependencies for deployment.

## Python Code Quality Checks

### 1. Install Development Dependencies

```bash
uv sync --extra dev
```

This installs all Python dependencies including linting tools.

### 2. Check Formatting (without changes)

```bash
uv run black --check scripts/
```

**Expected output (passing):**
```
All done! ✨ 🍰 ✨
2 files would be left unchanged.
```

**Expected output (failing):**
```
would reformat scripts/download_dataset.py
1 file would be reformatted, 1 file would be left unchanged.
```

### 3. Apply Formatting

```bash
uv run black scripts/
```

This automatically formats all Python code according to Black standards.

### 4. Run Linting (flake8)

```bash
uv run flake8 scripts/
```

This checks for PEP 8 style violations and common errors.

**Expected output (passing):**
```
(no output = all good)
```

### 5. Run Type Checking (mypy)

```bash
uv run mypy scripts/
```

This performs static type checking on Python code.

**Expected output (passing):**
```
Success: no issues found in 2 source files
```

## Complete Pre-Push Checklist

Run all checks before pushing to ensure CI passes:

### Quick Check (Recommended)

```bash
# Run all Scala checks
sbt fmtCheck fixCheck compile

# Run all Python checks
uv run black --check scripts/
uv run flake8 scripts/
uv run mypy scripts/
```

### Full Check (Complete CI Simulation)

```bash
# Scala: Format, fix, compile, test, and build
sbt scalafmtCheckAll scalafmtSbtCheck
sbt "scalafix --check"
sbt compile
sbt test
sbt assembly

# Python: All checks
uv sync --extra dev
uv run black --check scripts/
uv run flake8 scripts/
uv run mypy scripts/
uv run python -m scripts.download_dataset --help
```

### Auto-Fix Everything

If checks fail, run these commands to automatically fix most issues:

```bash
# Fix Scala formatting and linting
sbt fmt fix

# Fix Python formatting
uv run black scripts/
```

Then rerun the checks to verify everything passes.

## IDE Integration

### IntelliJ IDEA / Metals

1. **Install Scalafmt Plugin**
   - Go to: Settings → Plugins → Search "Scalafmt"
   - Enable "Format on Save"

2. **Enable Scalafix**
   - Metals automatically picks up `.scalafix.conf`
   - Run Scalafix from the command palette

### Visual Studio Code

1. **Install Metals Extension**
   ```bash
   code --install-extension scalameta.metals
   ```

2. **Install Python Extensions**
   ```bash
   code --install-extension ms-python.python
   code --install-extension ms-python.black-formatter
   ```

3. **Configure Settings (`.vscode/settings.json`)**
   ```json
   {
     "metals.scalafmtConfigPath": ".scalafmt.conf",
     "[python]": {
       "editor.defaultFormatter": "ms-python.black-formatter",
       "editor.formatOnSave": true
     }
   }
   ```

## Git Hooks (Optional)

Set up pre-commit hooks to automatically run checks:

### Create `.git/hooks/pre-commit`

```bash
#!/bin/bash
set -e

echo "Running pre-commit checks..."

# Scala checks
echo "Checking Scala formatting..."
sbt scalafmtCheckAll scalafmtSbtCheck

echo "Checking Scala code quality..."
sbt "scalafix --check"

echo "Compiling Scala code..."
sbt compile

# Python checks
if command -v uv &> /dev/null; then
  echo "Checking Python formatting..."
  uv run black --check scripts/
  
  echo "Running Python linting..."
  uv run flake8 scripts/
  
  echo "Running Python type checking..."
  uv run mypy scripts/
fi

echo "✅ All pre-commit checks passed!"
```

### Make it executable

```bash
chmod +x .git/hooks/pre-commit
```

## Troubleshooting

### "sbt: command not found"

Make sure sbt is installed and in your PATH:

```bash
# macOS/Linux
which sbt

# If not found, install using instructions above
```

### "uv: command not found"

Install uv using the appropriate method for your OS (see Prerequisites).

### "Formatting check failed"

Simply run the auto-fix commands:

```bash
sbt fmt fix
uv run black scripts/
```

### "Out of memory" errors with sbt

Increase JVM memory:

```bash
export SBT_OPTS="-Xmx2G -XX:+UseG1GC"
sbt compile
```

### Python imports not found

Ensure you've installed the development dependencies:

```bash
uv sync --extra dev
```

## Continuous Integration Parity

The local checks mirror what runs in CI:

| Local Command | CI Workflow Step |
|---------------|------------------|
| `sbt scalafmtCheckAll scalafmtSbtCheck` | Code Quality → Check Scala formatting |
| `sbt "scalafix --check"` | Code Quality → Run Scalafix checks |
| `sbt compile` | CI → Compile Scala code |
| `sbt test` | CI → Run Scala tests |
| `sbt assembly` | CI → Build assembly JAR |
| `uv run black --check scripts/` | Code Quality → Check Python formatting |
| `uv run flake8 scripts/` | Code Quality → Run Python linting |
| `uv run mypy scripts/` | Code Quality → Run Python type checking |

By running these checks locally, you can catch issues before pushing and ensure your CI pipeline passes on the first try!

