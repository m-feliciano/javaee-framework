# Java Web Framework

Lightweight Jakarta EE–based framework designed with Clean Architecture principles.
Focused on performance, security, and a clear structure for modern backend applications.

## Core Features

- Custom framework built from scratch using Servlets, CDI, and annotation-based controllers and routing.
- Advanced security: JWT with refresh/rotate tokens, CSRF protection, and structured auditing.
- Multi-layer caching: Hibernate L1/L2 + Ehcache.
- Rate limiting using the Leaky Bucket algorithm.
- Messaging with JMS/ActiveMQ for asynchronous events.
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

## Architecture

Built following Clean Architecture guidelines:

- Domain: entities, value objects, and business rules
- Application: use cases and orchestration
- Web: controllers, routing, and HTTP adaptation
- Infrastructure: persistence, messaging, and external integrations

Each layer remains strictly independent — infrastructure never leaks into the domain.

The framework includes its own lightweight MVC layer: annotation scanning, reflection-based routing, a middleware pipeline, and a central dispatcher to manage request flow.

## Preview

The demo application includes a modern UI.

![Preview](images/product-list.png)

## Getting Started

cp .env.example .env
docker-compose up -d --build
docker-compose logs -f app

Or import the project into IntelliJ/Eclipse and run:

mvn -DskipTests clean compile

## License

MIT — see the LICENSE file.

## Contributing

Contributions are welcome. See CONTRIBUTING.md for guidelines.