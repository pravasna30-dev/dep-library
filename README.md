# dep-library

A demonstration library showing how API breaking changes can be detected by consumer tests.

[![Library CI](https://github.com/pravasna30-dev/dep-library/actions/workflows/ci.yml/badge.svg)](https://github.com/pravasna30-dev/dep-library/actions/workflows/ci.yml)

## Overview

This library is part of a demonstration showing how **consumer integration tests act as an early warning system** for breaking API changes. The library has two versions:

| Branch | Version | API Status |
|--------|---------|------------|
| `main` | v1.0.0 | Stable API |
| `feature/method-signature-change` | v2.0.0 | **Breaking Changes** |

## Architecture

```mermaid
graph TB
    subgraph "Library Repository"
        direction TB
        M[main branch<br/>v1.0.0]
        F[feature branch<br/>v2.0.0]
    end

    subgraph "Maven Local"
        direction TB
        V1[library:1.0.0]
        V2[library:2.0.0]
    end

    subgraph "Consumer Repository"
        direction TB
        C[Consumer Code]
        T[Integration Tests]
    end

    M -->|publish| V1
    F -->|publish| V2
    V1 -->|dependency| C
    V2 -.->|❌ breaks| C
    C --> T

    style F fill:#ff6b6b,color:#fff
    style V2 fill:#ff6b6b,color:#fff
```

## API Versions

### v1.0.0 (main branch) - Stable API

```java
public class UserService {
    public User findById(Long userId);        // Returns null if not found
    public List<User> findAll();
    public User createUser(String email, String name);
}

public class User {
    public Long getId();
    public String getEmail();
    public String getName();
}
```

### v2.0.0 (feature branch) - Breaking Changes

```java
public class UserService {
    public Optional<User> findById(String userId);  // ⚠️ Long→String, User→Optional<User>
    public List<User> findAll();
    public User createUser(String email, String name);
}

public class User {
    public String getId();  // ⚠️ Long→String
    public String getEmail();
    public String getName();
}
```

## Breaking Changes Visualization

```mermaid
flowchart LR
    subgraph v1["v1.0.0 API"]
        A1["findById(Long)"] --> B1["User"]
        C1["getId()"] --> D1["Long"]
    end

    subgraph v2["v2.0.0 API"]
        A2["findById(String)"] --> B2["Optional&lt;User&gt;"]
        C2["getId()"] --> D2["String"]
    end

    v1 ==>|"❌ Breaking Change"| v2

    style v1 fill:#4ade80,color:#000
    style v2 fill:#ff6b6b,color:#fff
```

## How Breaking Change Detection Works

```mermaid
sequenceDiagram
    autonumber
    participant Dev as Developer
    participant Lib as Library
    participant Maven as Maven Local
    participant Consumer as Consumer
    participant CI as CI Pipeline

    Note over Dev,CI: Happy Path (v1.0.0)
    Dev->>Lib: Develop v1.0.0
    Lib->>Maven: ./gradlew publishToMavenLocal -Pversion=1.0.0
    Consumer->>Maven: Depends on library:1.0.0
    Consumer->>CI: ./gradlew test
    CI-->>Consumer: ✅ All tests pass

    Note over Dev,CI: Breaking Change (v2.0.0)
    Dev->>Lib: Change findById(Long) → findById(String)
    Lib->>Maven: ./gradlew publishToMavenLocal -Pversion=2.0.0
    Consumer->>Maven: Upgrade to library:2.0.0
    Consumer->>CI: ./gradlew test
    CI-->>Consumer: ❌ Compilation Error!

    Note over CI: Breaking change detected!<br/>Consumer code expects Long,<br/>but library now requires String
```

## Local Development

### Prerequisites

- Java 21+ (JDK, not JRE)
- Gradle 8.5+ (wrapper included)

### Build and Publish

```bash
# Clone the repository
git clone https://github.com/pravasna30-dev/dep-library.git
cd dep-library

# Build the library
./gradlew build

# Publish v1.0.0 to Maven Local
git checkout main
./gradlew publishToMavenLocal -Pversion=1.0.0

# Publish v2.0.0 (breaking changes) to Maven Local
git checkout feature/method-signature-change
./gradlew publishToMavenLocal -Pversion=2.0.0
```

### Verify Published Artifacts

```bash
# Check Maven Local
ls ~/.m2/repository/com/example/library/

# Should show:
# 1.0.0/
# 2.0.0/
```

## CI/CD Pipeline

The GitHub Actions workflow automatically:

1. **Builds** the library on every push
2. **Publishes** to Maven Local for testing
3. **Uploads** JAR artifacts

### Trigger CI Manually

```bash
# Using GitHub CLI
gh workflow run ci.yml --repo pravasna30-dev/dep-library

# Check status
gh run list --repo pravasna30-dev/dep-library
```

### CI Workflow

```mermaid
flowchart TD
    A[Push to GitHub] --> B{Which Branch?}
    B -->|main| C[Build v1.0.0]
    B -->|feature/*| D[Build v2.0.0]

    C --> E[Run Tests]
    D --> F[Run Tests]

    E --> G[Publish to Maven Local]
    F --> H[Publish to Maven Local]

    G --> I[Upload JAR Artifact]
    H --> J[Upload JAR Artifact]

    I --> K[✅ CI Complete]
    J --> L[✅ CI Complete]
```

## Related Repositories

| Repository | Description |
|------------|-------------|
| [dep-consumer](https://github.com/pravasna30-dev/dep-consumer) | Consumer that depends on this library |
| [dep-multimodule](https://github.com/pravasna30-dev/dep-multimodule) | Single repo with both library and consumer |

## License

MIT
