# API Test Core Framework

Enterprise-grade API automation core engine for multi-team microservices testing.

## Features

- RestAssured HTTP client wrapper (auth, retry, correlation IDs, PII masking)
- JDBC helper (HikariCP + PostgreSQL)
- Redis helper (Lettuce)
- TestNG base test + suite listeners
- YAML environment config with secret placeholder resolution
- Allure step utilities
- Parallel-safe test data isolation and cleanup registry

## Build & Install

```bash
mvn clean install
```

This publishes `com.org.apitest:api-test-core:1.0.0-SNAPSHOT` to your local Maven repository.

## Usage in Consumer Projects

Add dependency:

```xml
<dependency>
    <groupId>com.org.apitest</groupId>
    <artifactId>api-test-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Run tests with environment:

```bash
mvn test -Denv=qa
```

## Configuration

Environment YAML files live in `src/main/resources/environments/`. Secrets are resolved from environment variables or system properties using `${VAR_NAME}` syntax.

## Jenkins

Consumer repos should install this artifact from your Maven repository (Nexus/Artifactory) or build and install from source in a pipeline stage before running service tests.
