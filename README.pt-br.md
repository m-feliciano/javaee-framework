# Framework Backend Jakarta EE com Clean Architecture

Framework web leve baseado em Jakarta EE, seguindo princípios de Clean Architecture.
Focado em desempenho, segurança e estruturação clara de aplicações backend modernas.

## Funcionalidades Principais

- Framework próprio, construído do zero com Servlets, CDI e anotações para controllers e rotas.
- Segurança avançada: JWT com refresh/rotate, proteção CSRF e auditoria estruturada.
- Caching em múltiplas camadas: Hibernate L1/L2 + Ehcache.
- Rate limiting usando Leaky Bucket.
- Mensageria com JMS/Artemis para eventos assíncronos.
- Validação JSR-303 com extensões e validadores customizados.
- Observabilidade: logs com correlation IDs, métricas e health checks.
- DTO Mapping com MapStruct.

## Stack Tecnológico

- Java 21, Jakarta EE 10
- Weld CDI, Hibernate, Ehcache
- JJWT, Jackson, SLF4J/Logback
- JUnit 5 + Mockito
- PostgreSQL, OkHttp
- Maven 3.6+

### Migrações ao longo do tempo
- Java -> 11 -> 17 -> 21
- Javax EE -> Jakarta EE
- Tomcat 9 -> 10
- Hibernate 5 -> 6

## Arquitetura (Pacotes)

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
Clique [aqui](PREVIEW.md) para ver mais.

## Iniciando

Veja o README.md

## Endpoints

O framework expõe alguns endpoints. Aqui estão exemplos:
- `POST /api/v1/auth/form` - Autenticar com usuário/senha
- `GET /api/v1/product/list` - Listar produtos
- `POST /api/v1/health/live` - Liveness probe
- `GET /api/v1/user/{id}` - Obter detalhes do usuário

A URL base é `http://localhost:8080` por padrão (local).

Exemplo de uma URL completa para listar produtos: http://localhost:8080/api/v1/products

Se você quiser ver todos os endpoints disponíveis, verifique a aba Docs na interface da aplicação de demonstração ou consulte o código-fonte.

## Licença

MIT — ver arquivo LICENSE.

## Contribuições

Pull requests são bem-vindos. Guia em CONTRIBUTING.md


## Status Badges

![Java](https://img.shields.io/badge/Java-21-blue)
![License](https://img.shields.io/badge/license-MIT-green)
![Docker Pulls](https://img.shields.io/docker/pulls/mfeliciano1/servlets-app)
[![Last Commit](https://img.shields.io/github/last-commit/m-feliciano/javaee-framework)](...)