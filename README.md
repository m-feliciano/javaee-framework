# Java Web Framework

[![Java](https://img.shields.io/badge/Java-17-007396)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-C71A36)](https://maven.apache.org/)
[![Servlets](https://img.shields.io/badge/Servlets-4.0.1-orange)](https://javaee.github.io/servlet-spec/)
[![Hibernate](https://img.shields.io/badge/Hibernate-5.6.15-59666C)](https://hibernate.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

A lightweight Jakarta EE framework implementing Clean Architecture, and enterprise security standards.
Built for high-performance, scalable web applications with comprehensive observability and cloud-native deployment capabilities.

## Table of Contents

- [Core Features](#core-features)
- [Technology Stack](#technology-stack)
- [Application Preview](#application-preview) → **[Full Gallery](PREVIEW.md)**
- [Getting Started](#getting-started)
- [License](#license)

## Core Features

### Enterprise-Grade Capabilities

- **🏗️ Custom Framework**: Built from scratch using Servlet API 4.0 and CDI 1.2
- **🔐 Advanced Security**: JWT-based authentication, refresh token rotation, CSRF protection
- **⚡ Multi-Layer Caching**: L1 (Hibernate), L2 (Ehcache), Application-level with TTL management
- **🚦 Rate Limiting**: Leaky Bucket algorithm
- **📊 Health Monitoring**: Dashboard with metrics export
- **🔍 Audit Trail**: Structured JSON logging with correlation IDs and trace context propagation
- **🎯 Validation Framework**: JSR-303 compliant with custom annotations and composite validators
- **🔄 DTO Mapping**: MapStruct-based object mapping with circular reference handling
- **📦 Dependency Injection**: CDI with producer methods, qualifiers, and interceptors

## Technology Stack

### Core Technologies

| Component         | Technology        | Version |
|-------------------|-------------------|---------|
| **Runtime**       | Java SE           | 21 LTS  |
| **Web Layer**     | Jakarta EE        | 10      |
| **DI Container**  | Weld SE           | 5.x.x   |
| **Flyway**        | FlywayDB          | 11.x.x  |
| **ORM**           | Hibernate         | 5.x.x   |
| **Database**      | PostgreSQL JDBC   | 42.x.x  |
| **Security**      | JJWT              | 0.12.x  |
| **L2 Cache**      | Ehcache           | 3.x.x   |
| **Testing**       | JUnit 5 + Mockito |         |
| **HTTP Client**   | OkHttp            | 4.x.x   |
| **Serialization** | Jackson           | 2.19.x  |
| **Logging**       | SLF4J + Logback   |         |
| **Mapping**       | MapStruct         | 1.6.3   |
| **Build**         | Maven             | 3.6+    |

---

## 📸 Application Preview

> **[📱 View Full Gallery with Screenshots →](PREVIEW.md)**

Modern, enterprise-grade UI implementing Material Design principles with full dark mode support and WCAG AAA
accessibility compliance.

### Quick Preview

<p align="center">
  <img src="images/product-list.png" alt="Product Management Interface" width="800">
  <br>
  <em>Data grid with server-side pagination, sorting, and filtering</em>
</p>

**[→ See full screenshot gallery and UI/UX documentation](PREVIEW.md)**

---

## Getting Started

### Prerequisites

```bash
# Required
Java 21 LTS
Maven 3.6+
SQL (PostgreSQL recommended)

# Optional (for development)
Docker & Docker Compose
IntelliJ IDEA / Eclipse
Insomnia (Recommended)
```

### Quick Start

```bash
# Create a environment file
cp .env.example .env
# Edit .env to set database credentials and other configurations

# Build-Start services in detached mode
docker-compose up -d --build
# logs
docker-compose logs -f app
```

### IDE Setup

Import the project as a Maven project in your favorite IDE (IntelliJ IDEA, Eclipse, etc.). Ensure that your IDE is
configured to use Java 21 LTS.

```
# For IntelliJ IDEA
#File -> New -> Project from Existing Sources... -> Select pom.xml
#Before running, add the maven command to your run configuration:
mvn -DskipTests clean compile

#It will build the project and download all dependencies (frontend and backend).
#xThe frontend is also built using Maven, so no additional setup is required.
```

## Architecture

### Clean Architecture Layers

The framework follows Clean Architecture principles, organized into four main layers:

- **Adapter Layer** (`adapter`): Handles external interfaces, including the custom MVC dispatcher (`ServletDispatcherImpl`), HTTP executors, and request/response adapters.
- **Core Layer** (`core`): Contains framework internals, such as custom annotations (`@Controller`, `@RequestMapping`, `@Authorization`), utilities, validators, and response builders.
- **Domain Layer** (`domain`): Entities, Transfers, and domain models.
- **Service Layer** (`service`): Business logic, Includes audit services, authentication, and health monitoring.
- **Infrastructure Layer** (`infrastructure`): External concerns like persistence (Hibernate), security filters (JWT, XSS), and caching (Ehcache).

### Custom MVC Framework

Controllers extend `BaseRouterController`, using reflection to map HTTP requests to annotated methods. The `ServletDispatcherImpl` processes requests via `HttpExecutor`, supporting retry logic, rate limiting, and error handling. Dependency injection is managed by CDI (Weld).

### Security & Observability

JWT-based auth with refresh/rotate tokens, CSRF protection, and audit trails with correlation IDs. Health checks provide readiness/liveness probes. Multi-layer caching ensures performance.

## License

This project is licensed under the MIT License - see [LICENSE](LICENSE) for details.


## Contributing
Contributions are welcome! Please follow the guidelines below:
1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Commit your changes with clear messages.
4. Push to your fork and submit a pull request.
5. Ensure all tests pass and add new tests as necessary.

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for more details.