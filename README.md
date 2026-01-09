# Java Web Framework – Clean Architecture

Jakarta-based framework designed with Clean Architecture principles, focused on performance, security, and a clear structure for modern backend development.

## Key Features

- Custom framework: Servlets, CDI, annotation-based controllers and routing.
- Advanced security: JWT, refresh/rotate, CSRF, structured auditing.
- Multi-layer cache: Hibernate L1/L2, Ehcache.
- Rate limiting (Leaky Bucket).
- Messaging: JMS/ActiveMQ, SQS.
- Custom JSR-303 validation.
- Observability: correlationId, metrics, health checks.
- DTO mapping via MapStruct.

## Stack

- Java 21, Jakarta, Weld CDI, Hibernate, Ehcache
- JJWT, Jackson, SLF4J/Logback
- JUnit 5, Mockito
- PostgreSQL 16+, OkHttp
- Maven 3.6+, AWS SDK v2
- Docker, Kubernetes

## Architecture

- Domain: entities, value objects, business rules
- Application: use cases, orchestration
- Web: controllers, routing, HTTP adapters
- Infrastructure: persistence, messaging, integrations
- Adapters: implementations for interfaces from other layers

Each layer is independent; infrastructure never leaks into the domain.

Custom MVC: annotation scanning, reflection-based routing, middleware pipeline, and central dispatcher.

## Preview

![Preview](images/product-list.png)
Click [here](PREVIEW.md) to see more.

## Demo

A demo application is available for evaluation.

Pull the demo `docker-compose` file and start the services:

```shell
curl -O https://raw.githubusercontent.com/m-feliciano/javaee-framework/refs/heads/master/docker/demo/docker-compose.demo.yml
```

IMPORTANT: This demo uses an older version (v2.x.x) of the framework and will not be updated with the latest changes.

The current version of the framework can be found in the `master` branch.

Run the demo application with Docker Compose:

```shell
docker compose -f docker-compose.demo.yml up -d
```

For local build: `mvn clean package -Pdev`.

Modern UI available in the demo application. See technical details in PREVIEW.md.

## Endpoints
The application exposes some endpoints. Here are examples:
- `GET /api/v1/user/me` - Get current user details
- `POST /api/v2/user/upload-photo` - Upload user photo (v2 example)
- `POST /api/v1/auth/login` - Authenticate with username/password
- `GET /api/v1/health/up` - Check application health
- `GET /api/v1/product/list` - List products

If you want to see all available endpoints, check the Docs tab in the application UI or refer to the source code.

## OpenAPI / Swagger

The demo application includes OpenAPI documentation available at:

- [OpenAPI Spec](openapi/openapi.yaml)

View the API documentation online:

- https://tinyurl.com/4ndzytyh

## License

MIT. See LICENSE.

## Contribution

Contributions are welcome. See CONTRIBUTING.md.

# Status Badges

[//]: # ([![Build]&#40;https://github.com/m-feliciano/javaee-framework/actions/workflows/build.yml/badge.svg&#41;]&#40;...&#41;)
[//]: # ([![Quality Gate]&#40;https://sonarcloud.io/api/project_badges/measure?project=m-feliciano_javaee-framework&metric=alert_status&#41;]&#40;...&#41;)
![Java](https://img.shields.io/badge/Java-21-blue)
![License](https://img.shields.io/badge/license-MIT-green)
![Docker Pulls](https://img.shields.io/docker/pulls/mfeliciano1/servlets-app)
[![Last Commit](https://img.shields.io/github/last-commit/m-feliciano/javaee-framework)](...)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-green)](openapi/openapi.yaml)