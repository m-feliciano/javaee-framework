# Java Web Framework with Clean Architecture Principles

Lightweight Jakarta–based framework designed with Clean Architecture principles.
Focused on performance, security, and a clear structure for modern backend applications.

## Core Features

- Custom framework built from scratch using Servlets, CDI, and annotation-based controllers and routing.
- Advanced security: JWT with refresh/rotate tokens, CSRF protection, and structured auditing.
- Multi-layer caching: Hibernate L1/L2 + Ehcache.
- Rate limiting using the Leaky Bucket algorithm.
- Messaging with JMS/ActiveMQ (Artemis) for asynchronous events.
- JSR-303 validation with custom annotations and composite validators.
- Observability: correlation-ID logging, metrics, and health checks.
- DTO mapping powered by MapStruct.

## Technology Stack

- Java 21, Jakarta
- Weld CDI, Hibernate, Ehcache
- JJWT, Jackson, SLF4J/Logback
- JUnit 5 + Mockito
- PostgreSQL, OkHttp
- Maven 3.6+
- AWS SDK v2
- Docker & Kubernetes

### Migrations over time
- Java -> 11 -> 17 -> 21
- Javax -> Jakarta
- Tomcat 9 -> 10
- Hibernate 5 -> 6

## Architecture

Built the following Clean Architecture guidelines:

- Domain: entities, value objects, and business rules
- Application: use cases and orchestration
- Web: controllers, routing, and HTTP adapters
- Infrastructure: persistence, messaging, and external integrations
- Adapters: Class implementations for interfaces defined in other layers

Each layer remains strictly independent — infrastructure never leaks into the domain.

The framework includes its own lightweight MVC layer: annotation scanning, reflection-based routing, a middleware pipeline, and a central dispatcher to manage request flow.

## Preview

The demo application includes a modern UI.

![Preview](images/product-list.png)
Click [here](PREVIEW.md) to see more.

## Getting Started

### Quickstart (Docker)

Pull the demo `docker-compose` file and start the services:

IMPORTANT: This demo uses an older version (v2.x.x) of the framework and will not be updated with the latest changes.

The current version of the framework can be found in the `master` branch.

```shell
curl -O https://raw.githubusercontent.com/m-feliciano/javaee-framework/refs/heads/master/docker-compose.demo.yml
```

Run the demo application with Docker Compose:

```shell
docker compose -f docker-compose.demo.yml up -d
```

## To Set Up Services Manually

Read the docker-compose file at `docker-compose.yml` to set up the PostgreSQL and ActiveMQ services.

### Build and Run Locally
Build the application using Maven:

```bash
mvn clean package -Pdev # or -Pprod for production, depending on your environment
```

You may provide your own `.env` file to override default settings if you want to use functionality like email
sending.

## Endpoints
The application exposes some endpoints. Here are examples:
- `POST /api/v1/auth/login` - Authenticate with username/password
- `GET /api/v1/product/list` - List products
- `GET /api/v1/health/live` - Liveness probe
- `GET /api/v1/user/{id}` - Get user details

The base URL is `http://localhost:8080` by default (local).

Example of a complete url to list products: http://localhost:8080/api/v1/product/list

If you want to see all available endpoints, check the Docs tab in the demo application UI or refer to the source code.

## License

MIT — see the LICENSE file.

## Contributing

Contributions are welcome. See CONTRIBUTING.md for guidelines.

# Status Badges

[//]: # ([![Build]&#40;https://github.com/m-feliciano/javaee-framework/actions/workflows/build.yml/badge.svg&#41;]&#40;...&#41;)
[//]: # ([![Quality Gate]&#40;https://sonarcloud.io/api/project_badges/measure?project=m-feliciano_javaee-framework&metric=alert_status&#41;]&#40;...&#41;)
![Java](https://img.shields.io/badge/Java-21-blue)
![License](https://img.shields.io/badge/license-MIT-green)
![Docker Pulls](https://img.shields.io/docker/pulls/mfeliciano1/servlets-app)
[![Last Commit](https://img.shields.io/github/last-commit/m-feliciano/javaee-framework)](...)
