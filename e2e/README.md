# E2E Tests with Runn

This directory contains End-to-End tests for the RealWorld API using [Runn](https://github.com/k1LoW/runn).

## Prerequisites

Install Runn:

```bash
# macOS
brew install k1LoW/tap/runn

# Linux
go install github.com/k1LoW/runn/cmd/runn@latest

# Or download binary from releases
# https://github.com/k1LoW/runn/releases
```

## Running Tests

### Start the application

```bash
docker compose up -d
```

### Run all E2E tests

```bash
task e2e
```

Or directly with runn:

```bash
runn run e2e/*.yml
```

### Run specific test

```bash
runn run e2e/01_user_registration.yml
```

### Watch mode

```bash
task e2e:watch
```

## Test Structure

- `01_user_registration.yml` - User registration, login, and profile updates
- `02_article_crud.yml` - Article CRUD operations and favorites
- `03_comments.yml` - Article comments
- `04_profiles.yml` - User profiles and follow/unfollow
- `05_tags.yml` - Tags operations

## Environment Variables

- `API_ENDPOINT` - API base URL (default: `http://localhost:8080`)

Example:

```bash
API_ENDPOINT=https://api.example.com runn run e2e/*.yml
```

## Test Output

Runn will output test results showing:
- ✅ Passed steps
- ❌ Failed steps with detailed error messages
- Response bodies and status codes for debugging
