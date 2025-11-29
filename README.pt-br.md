# Framework Java Web

[![Java](https://img.shields.io/badge/Java-17-007396)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-C71A36)](https://maven.apache.org/)
[![Servlets](https://img.shields.io/badge/Servlets-4.0.1-orange)](https://javaee.github.io/servlet-spec/)
[![Hibernate](https://img.shields.io/badge/Hibernate-5.6.15-59666C)](https://hibernate.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

Um framework Java EE leve, implementando Clean Architecture e padrões avançados de segurança. 
Projetado para aplicações web de alta performance e escalabilidade, com observabilidade completa e recursos para implantação em nuvem.

## Índice

- [Recursos Principais](#recursos-principais)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Prévia da Aplicação](#prévia-da-aplicação) → **[Galeria Completa](PREVIEW.md)**
- [Primeiros Passos](#primeiros-passos)
- [Licença](#licença)

## Recursos Principais

### Capacidades Empresariais

- **🏗️ Framework Personalizado**: Desenvolvido do zero usando Jakarta EE e CDI
- **🔐 Segurança Avançada**: Autenticação baseada em JWT, rotação de refresh token, proteção CSRF
- **⚡ Cache Multi-Camadas**: L1 (Hibernate), L2 (Ehcache), cache de aplicação com gerenciamento de TTL
- **🚦 Limitação de Taxa**: Algoritmo Leaky Bucket
- **📊 Monitoramento de Saúde**: Dashboard com exportação de métricas
- **🔍 Trilhas de Auditoria**: Log estruturado em JSON com IDs de correlação e propagação de contexto de rastreamento
- **🎯 Framework de Validação**: Compatível com JSR-303, incluindo anotações personalizadas e validadores compostos
- **🔄 Mapeamento de DTOs**: Mapeamento de objetos com MapStruct, incluindo tratamento de referências circulares
- **📦 Injeção de Dependência**: CDI com métodos produtores, qualificadores e interceptadores

## Tecnologias Utilizadas

### Tecnologias Principais


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

## 📸 Prévia da Aplicação

> **[📱 Veja a galeria completa de screenshots →](PREVIEW.md)**

Interface moderna de nível empresarial, seguindo princípios do Material Design, com suporte total a modo escuro e conformidade com acessibilidade WCAG AAA.

### Prévia Rápida

<p align="center">
  <img src="images/product-list.png" alt="Interface de Gestão de Produtos" width="800">
  <br>
  <em>Grade de dados com paginação, ordenação e filtragem no servidor</em>
</p>

**[→ Veja a galeria completa de screenshots e documentação de UI/UX](PREVIEW.md)**

---

## Primeiros Passos

### Pré-requisitos

```bash
# Obrigatórios
Java 21 LTS (OpenJDK ou Oracle)
Maven 3.6+
SQL (PostgreSQL recomendado)

# Opcionais (para desenvolvimento)
Docker & Docker Compose
IntelliJ IDEA / Eclipse
Insomnia (Recomendado)
```

### Execução Rápida

```bash
# Crie o arquivo de ambiente
cp .env.example .env
# Edite o .env para configurar credenciais do banco e outras opções

# Construa e execute com Docker Compose
docker-compose build -no-cache
# Inicie os serviços em modo destacado
docker-compose --env-file .env up -d --build
# logs
docker-compose logs -f app
```

## Arquitetura

### Camadas da Clean Architecture

Este projeto segue os princípios da Clean Architecture, garantindo uma separação nítida entre regras de negócio e infraestrutura externa.
O código está organizado em quatro camadas independentes, cada uma responsável por um tipo específico de responsabilidade.

1. Domain Layer (domain): Contém as entidades de negócio, value objects e serviços de domínio.
2. Application Layer (application): Orquestra o fluxo de negócio e coordena os casos de uso.
3. Infrastructure Layer (infrastructure): Trata da persistência de dados, serviços externos e outros detalhes técnicos.
4. Web Layer (web) – Adaptadores de Entrada (Port-In): Gerencia requisições e respostas HTTP.

### Framework MVC Personalizado

Controllers estendem `BaseRouterController`, usando reflexão para mapear requisições HTTP para métodos anotados. O `ServletDispatcherImpl` processa requisições via `HttpExecutor`, suportando lógica de retry, limitação de taxa e tratamento de erros. A injeção de dependência é gerenciada pelo CDI (Weld).

### Segurança & Observabilidade

Autenticação baseada em JWT com refresh/rotate tokens, proteção CSRF e trilhas de auditoria com IDs de correlação. Health checks fornecem probes de readiness/liveness. O cache multi-camadas garante performance.

## Licença

Este projeto está licenciado sob a Licença MIT - veja [LICENSE](LICENSE) para mais detalhes.

