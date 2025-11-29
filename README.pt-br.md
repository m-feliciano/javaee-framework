# Java Web Framework

Framework web leve baseado em Jakarta EE, seguindo princípios de Clean Architecture.
Focado em desempenho, segurança e estruturação clara de aplicações backend modernas.

## Core Features

- Framework próprio, construído do zero com Servlets, CDI e anotações para controllers e rotas.
- Segurança avançada: JWT com refresh/rotate, proteção CSRF e auditoria estruturada.
- Caching em múltiplas camadas: Hibernate L1/L2 + Ehcache.
- Rate limiting usando Leaky Bucket.
- Mensageria com JMS/Artemis para eventos assíncronos.
- Validação JSR-303 com extensões e validadores customizados.
- Observabilidade: logs com correlation IDs, métricas e health checks.
- DTO Mapping com MapStruct.

## Technology Stack

- Java 21, Jakarta EE 10
- Weld CDI, Hibernate, Ehcache
- JJWT, Jackson, SLF4J/Logback
- JUnit 5 + Mockito
- PostgreSQL, OkHttp
- Maven 3.6+

## Architecture

Estruturado sobre Clean Architecture:

- Domain: entidades e regras de negócio
- Application: casos de uso
- Web: controllers, rotas e adaptação HTTP
- Infrastructure: persistência, mensageria e integrações

Cada camada mantém independência clara — infraestrutura nunca vaza para o domínio.

O framework inclui seu próprio “mini MVC”: reflexão para mapear anotações, pipeline de middleware e um dispatcher central que cuida do fluxo da requisição.

## Preview

A aplicação de demonstração inclui uma interface moderna.

![Preview](images/product-list.png)

## Getting Started

cp .env.example .env
docker-compose up -d --build
docker-compose logs -f app

Ou importe o projeto no IntelliJ/Eclipse e execute:

mvn -DskipTests clean compile

## License

MIT — ver arquivo LICENSE.

## Contribuições

Pull requests são bem-vindos. Guia em CONTRIBUTING.md