# Git Workflow — Web-Gara Project

## Branch Strategy (Gitflow)

```
main         ←── production-ready, always deployable
develop      ←── integration branch, all features merge here
feature/*    ←── new features (branch from develop)
release/*    ←── release preparation (branch from develop)
hotfix/*     ←── urgent production fixes (branch from main)
```

---

## Branch Rules

| Branch | Branch From | Merge Into | Direct Push |
|--------|------------|------------|-------------|
| `main` | — | — | ❌ Never |
| `develop` | `main` | — | ❌ Never |
| `feature/*` | `develop` | `develop` | ❌ Never |
| `release/*` | `develop` | `main` + `develop` | ❌ Never |
| `hotfix/*` | `main` | `main` + `develop` | ❌ Never |

---

## Daily Workflow

### Start a new feature
```bash
git checkout develop
git pull origin develop           # always pull latest before branching
git checkout -b feature/your-feature-name
```

### Work on feature
```bash
git add .
git commit -m "feat(module): description"
git push origin feature/your-feature-name
```

### Stay up to date with develop
```bash
git fetch origin
git rebase origin/develop         # preferred over merge to keep history clean
```

### Finish feature → open Pull Request
1. Push branch to GitHub
2. Open PR: `feature/your-feature-name` → `develop`
3. Request review from teammate
4. Squash and merge after approval

---

## Commit Message Format

```
type(scope): short description

Types:
  feat      → new feature
  fix       → bug fix
  refactor  → code change without feature/bug
  test      → adding/updating tests
  docs      → documentation only
  chore     → build, config, dependencies
  style     → formatting, no logic change
```

**Examples:**
```
feat(auth): add JWT refresh token endpoint
fix(appointment): correct status transition validation
chore(docker): add mongodb healthcheck to compose
docs(api): update swagger description for invoice endpoints
```

---

## Pull Request Rules

- Title follows commit format: `feat(module): description`
- Must have **at least 1 approval** before merging
- Resolve all comments before merging
- Delete branch after merge
- Never merge your own PR (unless solo working)

---

## Release Flow

```bash
# Cut release branch from develop
git checkout develop
git pull origin develop
git checkout -b release/1.0.0

# Fix release bugs, bump version in pom.xml
git commit -m "chore(release): bump version to 1.0.0"

# Merge to main
git checkout main
git merge --no-ff release/1.0.0
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin main --tags

# Merge back to develop
git checkout develop
git merge --no-ff release/1.0.0
git push origin develop

# Delete release branch
git branch -d release/1.0.0
git push origin --delete release/1.0.0
```

---

## Hotfix Flow

```bash
# Branch from main
git checkout main
git pull origin main
git checkout -b hotfix/critical-bug-description

# Fix, commit
git commit -m "fix(scope): fix critical bug"

# Merge to main
git checkout main
git merge --no-ff hotfix/critical-bug-description
git tag -a v1.0.1 -m "Hotfix 1.0.1"
git push origin main --tags

# Merge to develop too
git checkout develop
git merge --no-ff hotfix/critical-bug-description
git push origin develop

# Delete hotfix branch
git branch -d hotfix/critical-bug-description
git push origin --delete hotfix/critical-bug-description
```

---

## Two-Person Workflow (No Conflicts)

### Module Split
| Person | Modules |
|--------|---------|
| Dev 1 | auth, user, vehicle, appointment, notification |
| Dev 2 | garage, service, repair, inventory, invoice, payment, review, report |

### Rules
- Each person works on **different modules** — never edit the same file
- Common files (`BaseDocument`, `ApiResponse`, `GlobalExceptionHandler`) → coordinate before editing
- Pull `develop` every morning before starting work
- Open PR at end of day, don't let branches diverge > 2 days

---

## Protected Branches (GitHub Settings)

| Branch | Rule |
|--------|------|
| `main` | Require PR + 1 approval + no direct push (enforce admins) |
| `develop` | Require PR + 1 approval |
