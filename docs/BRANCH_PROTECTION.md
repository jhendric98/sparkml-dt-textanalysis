# Branch Protection Configuration

This document outlines the recommended branch protection settings for maintaining code quality and security in the repository.

## Main Branch Protection Rules

Configure the following settings for the `main` branch in GitHub repository settings:

### General Settings
- ✅ **Restrict pushes that create files larger than 100MB**
- ✅ **Require a pull request before merging**
  - ✅ Require approvals: **1**
  - ✅ Dismiss stale PR approvals when new commits are pushed
  - ✅ Require review from code owners (if CODEOWNERS file exists)
  - ✅ Restrict reviews to users with write access
  - ✅ Allow specified actors to bypass required pull requests: *None*

### Status Checks
- ✅ **Require status checks to pass before merging**
- ✅ **Require branches to be up to date before merging**
- **Required status checks:**
  - `Test (Java 11, Scala 2.12.18)`
  - `Linting and Formatting`
  - `Security Scan`
  - `Dependency Review` (for PRs)

### Additional Restrictions
- ✅ **Require conversation resolution before merging**
- ✅ **Require signed commits** (recommended for enhanced security)
- ✅ **Require linear history** (optional, helps maintain clean git history)
- ✅ **Include administrators** (apply rules to repository administrators)
- ✅ **Allow force pushes**: *Disabled*
- ✅ **Allow deletions**: *Disabled*

## Develop Branch Protection (if using GitFlow)

If using a `develop` branch, apply similar but slightly relaxed rules:

### General Settings
- ✅ **Require a pull request before merging**
  - ✅ Require approvals: **1**
  - ✅ Dismiss stale PR approvals when new commits are pushed

### Status Checks
- ✅ **Require status checks to pass before merging**
- **Required status checks:**
  - `Test (Java 11, Scala 2.12.18)`
  - `Linting and Formatting`

## Repository Security Settings

### General Security
- ✅ **Private vulnerability reporting**: Enabled
- ✅ **Dependency graph**: Enabled
- ✅ **Dependabot alerts**: Enabled
- ✅ **Dependabot security updates**: Enabled
- ✅ **Dependabot version updates**: Enabled (configured via .github/dependabot.yml)

### Code Scanning
- ✅ **CodeQL analysis**: Enabled for pull requests and pushes to main
- ✅ **Secret scanning**: Enabled
- ✅ **Push protection**: Enabled (prevents commits with secrets)

### Access Control
- **Repository visibility**: Public or Private (as appropriate)
- **Forking**: Enabled for public repositories, disabled for private
- **Issues**: Enabled
- **Projects**: Enabled
- **Wiki**: Disabled (use docs/ directory instead)
- **Discussions**: Optional

## Team Permissions

### Recommended Team Structure
- **Maintainers**: Admin access
- **Contributors**: Write access
- **Reviewers**: Triage access

### Permission Guidelines
- **Admin**: Repository owners and lead maintainers only
- **Write**: Trusted contributors who can merge PRs
- **Triage**: Community members who can manage issues and PRs
- **Read**: All other contributors

## Automation Rules

### Auto-merge Conditions
Consider enabling auto-merge for:
- Dependabot PRs that pass all checks
- Documentation-only changes from trusted contributors
- Minor version updates with passing tests

### Required Checks Before Auto-merge
- All CI checks pass
- At least one approval from a maintainer
- No requested changes
- Branch is up to date

## Implementation Steps

1. **Navigate to Repository Settings**
   - Go to your repository on GitHub
   - Click on "Settings" tab
   - Select "Branches" from the left sidebar

2. **Add Branch Protection Rule**
   - Click "Add rule"
   - Enter branch name pattern: `main`
   - Configure settings as outlined above

3. **Configure Security Settings**
   - Go to "Security & analysis" in repository settings
   - Enable recommended security features

4. **Set Up Teams and Permissions**
   - Go to repository "Settings" → "Manage access"
   - Configure team permissions as needed

5. **Test Configuration**
   - Create a test PR to verify all rules work correctly
   - Ensure CI checks are properly required
   - Verify approval requirements function as expected

## Monitoring and Maintenance

- **Weekly**: Review Dependabot PRs and security alerts
- **Monthly**: Audit access permissions and team memberships
- **Quarterly**: Review and update branch protection rules as needed
- **As needed**: Adjust rules based on team growth and workflow changes
