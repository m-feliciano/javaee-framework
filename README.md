# Java Web Framework with Clean Architecture Principles

Lightweight Jakarta EE–based framework designed with Clean Architecture principles.
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

- Java 21, Jakarta EE 10
- Weld CDI, Hibernate, Ehcache
- JJWT, Jackson, SLF4J/Logback
- JUnit 5 + Mockito
- PostgreSQL, OkHttp
- Maven 3.6+

### Migrations over time
- Java -> 11 -> 17 -> 21
- Javax EE -> Jakarta EE
- Tomcat 9 -> 10
- Hibernate 5 -> 6

## Architecture

Built the following Clean Architecture guidelines:

- Domain: entities, value objects, and business rules
- Application: use cases and orchestration
- Web: controllers, routing, and HTTP adapters
- Infrastructure: persistence, messaging, and external integrations

Each layer remains strictly independent — infrastructure never leaks into the domain.

The framework includes its own lightweight MVC layer: annotation scanning, reflection-based routing, a middleware pipeline, and a central dispatcher to manage request flow.

## Preview

The demo application includes a modern UI.

![Preview](images/product-list.png)
Click [here](PREVIEW.md) to see more.

## Getting Started

First, load the default environment variables and customize the `.env` file as needed:

```bash
cp .env.example .env
```

### Quickstart (Docker)

Pull the demo `docker-compose` file and start the services:

```shell
curl -O https://raw.githubusercontent.com/m-feliciano/javaee-framework/refs/heads/master/docker-compose.demo.yml
docker compose -f docker-compose.demo.yml up -d 
```

Note: You may provide your own `.env` file to override default settings if you want to use functionality like email
sending.

Run with Docker Compose:

```shell
docker-compose -f docker-compose.demo.yml up -d
```

## To Set Up Services Manually

Read the docker-compose file at `docker-compose.yml` to set up the PostgreSQL and ActiveMQ services.

## License

MIT — see the LICENSE file.

## Contributing

Contributions are welcome. See CONTRIBUTING.md for guidelines.