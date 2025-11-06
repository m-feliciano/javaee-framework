# Java Servlets Framework

[![Java](https://img.shields.io/badge/Java-17-007396)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-C71A36)](https://maven.apache.org/)
[![Servlets](https://img.shields.io/badge/Servlets-4.0.1-orange)](https://javaee.github.io/servlet-spec/)
[![Hibernate](https://img.shields.io/badge/Hibernate-5.6.15-59666C)](https://hibernate.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A custom Java EE framework for building robust web applications. This project showcases the best practices in enterprise Java development, including Clean Architecture principles, security, efficient caching, and testing.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Installation](#installation)
- [Usage](#usage)
- [Stateless Architecture](#stateless-architecture)
- [Configuration](#configuration)
- [Testing](#testing)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)
- [License](#license)

## Features

- **Custom API**: Custom framework for building APIs with Java Servlets and CDI.
- **Security**: JWT-based authentication with XSS protection, password encryption, role-based access control and refresh tokens.
- **Performance**: Multi-level caching with Ehcache and rate limiting using Leaky Bucket algorithm.
- **Database**: JPA/Hibernate integration with PostgreSQL, including pagination and sorting.
- **Validation**: Custom annotations and DTO mapping for robust data handling.
- **External Integrations**: Web scraping client using OkHttp for development/testing.
- **Utilities**: Comprehensive toolkit for URI parsing, cryptography, formatting, and reflection.
- **Audit Logging**: Detailed JSON-based logging for all operations.

## Technology Stack

| Component   | Technology               | Version/Notes                                    |
|-------------|--------------------------|--------------------------------------------------|
| ☕ Language  | Java                     | 17                                               |
| 🌐 Servlet  | Servlet API              | 4.0.1 (javax.*)                                  |
| 🧩 CDI      | Weld / CDI API           | 2.4.8.Final / 1.2                                |
| 🗄️ ORM     | Hibernate ORM            | 5.6.15.Final                                     |
| 🐘 Database | PostgreSQL JDBC          | 42.4.4                                           |
| 🔐 Security | Auth0 java-jwt           | 4.4.0                                            |
| ⚡ Cache     | Ehcache                  | 3.9.11; hibernate-ehcache 5.6.15                 |
| 🧪 Testing  | JUnit Jupiter / Mockito  | RELEASE (5.10.2) / 5.14.2, 5.2.0                 |
| 📦 HTTP     | OkHttp                   | 4.12.0                                           |
| 🔣 JSON     | Jackson (core/databind)  | 2.19.0                                           |
| 🧱 Logging  | SLF4J / Logback          | 2.0.9 / 1.5.18                                   |
| 🧾 JSP/JSTL | JSTL / Taglibs Standard  | 1.2 / 1.2.5                                      |
| ♻️ Others   | Lombok / mchange-commons | 1.18.32 / 0.2.20                                 |

## Architecture

This application follows Clean Architecture principles, separating concerns into layers:

- **Adapter**: Handles HTTP requests, dispatching, and execution logging.
- **Controller**: Manages REST endpoints and routing logic.
- **Core**: Contains utilities, validators, mappers, and cache decorators.
- **Domain**: Business logic, entities, services, and repositories.
- **Infrastructure**: Persistence, security filters, and external clients.
- **Config**: CDI producers and configuration beans.

### Request Flow

```
🌐 Browser → 🔐 Filters → 🔧 Dispatcher → 📋 BaseController → 🎯 Controller → 🏭 Service → 💾 DAO → 🐘 PostgreSQL
```

## Installation

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL database

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/servlets.git
   cd servlets
   ```

2. Set up the database:
   - Ensure PostgreSQL is running.
   - Create a database and update connection details in `src/main/resources/app-dev.properties`.

3. Build the project:
   ```bash
   mvn clean package
   ```

4. Deploy the WAR file:
   - Copy `target/servlets-<current_version>.war` to your servlet container (e.g., Tomcat, WildFly).
   - Start the server.

The application will be available at `http://localhost:8080` (adjust port as needed).

## Usage

### API Examples

- **Login**: `/api/v1/auth/login`
- **Products**: `/api/v1/product/list`
- **Inventory (by ID)**: `/api/v1/inventory/list/{id}`

Example request:
```bash
curl -X GET "http://localhost:8080/api/v1/product/list/?page=1&limit=5&sort=id&order=asc" \
     -H "Cookie: YOUR_COOKIE_HERE"
```

## Stateless Architecture

This application follows a **stateless architecture**, which means the server does not store any session information between requests. Each request contains all the information needed to process it independently.

### Why Stateless?

✅ **Scalability**: No session synchronization needed — any server can handle any request

✅ **Performance**: No server-side session storage or lookup overhead

✅ **Reliability**: Server crashes don't lose user sessions

✅ **Cloud-Ready**: Perfect for horizontal scaling and load balancing

✅ **Microservices**: Easy to split into independent services

## Quick Reference

### 🚀 Stateless Architecture Summary

| Feature                | Status              | Details                       |
|------------------------|---------------------|-------------------------------|
| **Session Storage**    | ❌ None              | Zero server-side sessions     |
| **Authentication**     | ✅ JWT               | Self-contained tokens         |
| **Token Storage**      | ✅ HTTP-Only Cookies | Secure, XSS-protected         |
| **Token Lifetime**     | ⏱️ 1d / 30d         | Access / Refresh              |
| **Auto-Refresh**       | ✅ Yes               | Seamless UX                   |
| **Horizontal Scaling** | ✅ Yes               | No session replication needed |
| **CSRF Protection**    | ✅ SameSite=Strict   | Production                    |
| **XSS Protection**     | ✅ HttpOnly          | Always                        |
| **HTTPS Required**     | ✅ Production        | Secure flag enforced          |

---

## Screenshots

<div align="center">
  <img src="images/homepage.png" alt="Application Homepage" width="80%">
  <p><em>Homepage displaying product listings</em></p>

  <img src="images/product-list.png" alt="Product Management Interface" width="80%">
  <p><em>Product management dashboard</em></p>
</div>

## Configuration

Configuration is managed via properties files:

- **Development**: `src/main/resources/app-dev.properties`
- **Production**: `src/main/resources/app-prod.properties`

Key settings include:
- Database connection parameters
- JWT secret key
- Cache timeouts
- Rate limiting toggles
- Pagination defaults

Example configuration snippet:
```properties
env=development
host=localhost
port=8080
context=/api/v1
security.jwt.key=your-secret-key-here
rate.limit.enabled=true
cache.timeout.minutes=30
```

## Testing

Run the test suite with Maven:
```bash
mvn test
```

Tests cover:
- Unit tests for services, controllers, and utilities
- Integration tests for security filters and rate limiting
- Mocked dependencies using Mockito

## API Documentation

For detailed API documentation, refer to the inline comments in the controller classes or use tools like Swagger if integrated.

### Audit Logging

The `AuditService` generates JSON logs for all operations:

```json
{
  "schemaVersion": "1.0",
  "event": "product:list",
  "timestamp": "2025-10-20T04:13:59.125748900Z",
  "correlationId": "61aa3a37-26aa-44df-b8eb-cc564a5a8603",
  "outcome": "success",
  "userId": "f6fbba83",
  "payload": { ... }
}
```

Logs are stored in `logs/audit.log`.

## Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/your-feature`.
3. Commit your changes: `git commit -m 'Add some feature'`.
4. Push to the branch: `git push origin feature/your-feature`.
5. Open a pull request.

Ensure your code follows the project's coding standards and includes tests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
