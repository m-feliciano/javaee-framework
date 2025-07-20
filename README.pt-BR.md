# Aplicação Web Full-Stack Java EE

[![Build](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/m-feliciano/servlets)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/license/mit)
[![Java](https://img.shields.io/badge/java-17-blue)](https://adoptopenjdk.net/)

> Aplicação empresarial Java EE implementando princípios de arquitetura limpa com infraestrutura abrangente de
> segurança, cache e testes.

## Conteúdo

- [Visão Geral](#visão-geral)
- [Arquitetura](#arquitetura)
- [Funcionalidades](#funcionalidades)
- [Stack Tecnológica](#stack-tecnológica)
- [Guia de Desenvolvimento](#guia-de-desenvolvimento)
- [Documentação da API](#documentação-da-api)
- [Arquitetura de Cache](#arquitetura-de-cache)
- [📚 Documentação Detalhada do Fluxo](#-documentação-detalhada-do-fluxo)

## Visão Geral

Este projeto demonstra uma aplicação empresarial Java EE construída com base em princípios de arquitetura limpa.
Projetada para ambientes de produção, fornece uma base escalável com controles de segurança abrangentes, gerenciamento
eficiente de dados e desempenho otimizado através de cache estratégico.

Objetivos arquiteturais principais:

- Clara separação de responsabilidades através de design em camadas
- Componentes desacoplados para máxima testabilidade
- Padrões padronizados para desenvolvimento consistente
- Segurança por design com autenticação e autorização adequadas

### Capturas de Tela

<div align="center">
  <img src="images/homepage.png" alt="Página Inicial da Aplicação" width="80%">
  <p><em>Página inicial com listagem de produtos e navegação</em></p>

  <img src="images/product-list.png" alt="Gerenciamento de Produtos" width="80%">
  <p><em>Interface de gerenciamento de produtos</em></p>
</div>

## Arquitetura

```mermaid
graph TD
    A[Camada de Apresentação] --> B[Camada de Aplicação]
    B --> C[Camada de Domínio]
    C --> D[Camada de Infraestrutura]
    A -.-> E[(Controllers, Filters, JSP)]
    B -.-> F[(Services, DTOs, Mappers)]
    C -.-> G[(Models, Repositories)]
    D -.-> H[(Persistência, Segurança, Externos)]
```

**Fluxo de Requisição:**

```mermaid
sequenceDiagram
    participant Usuário
    participant Auth as "Filtro Auth"
    participant Dispatcher
    participant Controller
    participant Service
    participant Repository
    participant Database
    Usuário ->> Auth: Requisição
    Auth ->> Dispatcher: Requisição Autenticada
    Dispatcher ->> Controller: Roteamento da Requisição
    Controller ->> Service: Processamento
    Service ->> Repository: Consulta
    Repository ->> Database: Execução
    Database -->> Repository: Dados
    Repository -->> Service: Objetos de Domínio
    Service -->> Controller: DTOs
    Controller -->> Dispatcher: Resposta
    Dispatcher -->> Auth: Resposta Processada
    Auth -->> Usuário: Resposta HTTP
```

## Funcionalidades

| Funcionalidade         | Descrição                                                  |
|------------------------|------------------------------------------------------------|
| Autenticação           | Autenticação baseada em JWT e controle de acesso por papel |
| Validação              | Framework de validação personalizado com anotações         |
| Gerenciamento de Dados | Capacidades de paginação, busca e filtragem                |
| Cache                  | Cache multinível com isolamento por usuário                |
| Testes                 | Testes unitários e de integração abrangentes               |
| Logging                | Logging estruturado com SLF4J e Logback                    |
| Arquitetura            | MVC em camadas com clara separação de responsabilidades    |

## Stack Tecnológica

| Componente     | Tecnologia    | Versão       |
|----------------|---------------|--------------|
| Linguagem      | Java          | 17           |
| ORM            | Hibernate/JPA | 6.2.7.Final  |
| Servidor       | Tomcat        | 9            |
| Banco de Dados | PostgreSQL    | 42.5.4       |
| Testes         | JUnit/Mockito | 5.9.2/4.11.0 |
| Logging        | SLF4J/Logback | 2.0.7/1.4.7  |
| Web            | Servlet API   | 4.0.1        |
| Utilitários    | Lombok        | 1.18.26      |

## Estrutura de Pacotes

## Guia de Desenvolvimento

### Pré-requisitos

- Java 17+
- Maven 3.x
- PostgreSQL
- Tomcat 9

### Instruções de Configuração

1. Clone o repositório: `git clone https://github.com/m-feliciano/servlets.git`
2. Configure o banco de dados em `src/main/resources/META-INF/persistence.xml`
3. Build: `mvn clean install`
4. Implante o arquivo WAR no Tomcat
5. Acesse: `http://localhost:8080/api/v1/login/form`

### Arquivos de Configuração

- Banco de dados: `src/main/resources/META-INF/persistence.xml`
- Aplicação: `src/main/resources/app.properties`
- Scripts SQL: `src/main/resources/META-INF/sql`

## Documentação da API

Os endpoints seguem o padrão: `/api/v{versão}/{recurso}/{ação}`

### Endpoints Principais

#### API de Produtos

| Método | Endpoint                    | Auth      | Descrição                |
|--------|-----------------------------|-----------|--------------------------|
| GET    | /api/v1/product/list        | Requerido | Listar todos os produtos |
| GET    | /api/v1/product/list/{id}   | Requerido | Detalhes do produto      |
| POST   | /api/v1/product/create      | Requerido | Criar produto            |
| POST   | /api/v1/product/update/{id} | Requerido | Atualizar produto        |
| POST   | /api/v1/product/delete/{id} | Requerido | Excluir produto          |

#### API de Usuários

| Método | Endpoint                  | Auth      | Descrição              |
|--------|---------------------------|-----------|------------------------|
| POST   | /api/v1/user/update/{id}  | Requerido | Atualizar usuário      |
| POST   | /api/v1/user/delete/{id}  | Admin     | Excluir usuário        |
| POST   | /api/v1/user/registerUser | Público   | Registrar novo usuário |
| GET    | /api/v1/user/list/{id}    | Requerido | Detalhes do usuário    |

#### API de Autenticação

| Método | Endpoint                   | Auth      | Descrição              |
|--------|----------------------------|-----------|------------------------|
| GET    | /api/v1/login/form         | Público   | Formulário de login    |
| POST   | /api/v1/login/login        | Público   | Realizar login         |
| POST   | /api/v1/login/logout       | Requerido | Realizar logout        |
| GET    | /api/v1/login/registerPage | Público   | Formulário de registro |

## Arquitetura de Cache

A aplicação implementa um sistema avançado de cache usando o padrão Decorator para otimizar o desempenho sem modificar o
código principal do serviço.

### Implementação

- Usa Ehcache para armazenamento em memória
- Implementa expiração baseada em tempo (configurável via propriedades)
- Suporta invalidação manual e automática do cache
- Fornece isolamento de cache específico por usuário através de tokens
- Suporta objetos complexos, coleções e paginação
- Gerencia eficientemente o ciclo de vida do cache com limpeza automática de entradas não utilizadas

### Componentes Principais

- **CacheUtils**: Utilitário central que gerencia o ciclo de vida do cache, com suporte para operações CRUD em caches
  isolados por token
- **CachedServiceDecorator**: Implementa o padrão Decorator para adicionar capacidades de cache a qualquer repositório
  sem modificar o código existente
- **Proxies de Serviço**: Utilizam o decorator para interceptar chamadas e aplicar estratégias de cache

### Características Avançadas

- Cache por prefixo de chave para agrupamento lógico de entradas
- Clonagem profunda de objetos para evitar vazamento de estado
- Suporte para consultas paginadas com chaves de cache sensíveis a parâmetros de paginação
- Limpeza automática de caches ociosos para otimizar o uso de memória

### Padrão Decorator

```mermaid
classDiagram
    class ICrudRepository~T,K~ {
<<interface>>
+findById(K) T
+findAll(T) Collection~T~
+save(T) T
+update(T) T
+delete(T) void
}
class ConcreteRepository {
+findById(K) T
+findAll(T) Collection~T~
+save(T) T
+update(T) T
+delete(T) void
}
class CachedServiceDecorator~T, K~ {
-ICrudRepository~T,K~ decorated
-String cacheKeyPrefix
-String cacheToken
+findById(K) T
+findAll(T) Collection~T~
+save(T) T
+update(T) T
+delete(T) void
+invalidateCache() void
+getAllPageable(IPageRequest~T~) IPageable~T~
}

ICrudRepository <|.. ConcreteRepository
ICrudRepository <|.. CachedServiceDecorator
CachedServiceDecorator o-- ICrudRepository
```

## 📚 Documentação Detalhada do Fluxo

Para uma compreensão aprofundada do funcionamento interno da aplicação, consulte a documentação especializada:

### [📋 Fluxo Completo de Requisição - Listagem de Produtos](./docs/FLUXO_REQUISICAO_LISTAGEM_PRODUTOS.md)
Documentação detalhada e passo-a-passo do fluxo completo de uma requisição de listagem de produtos, explicando:
- **Cadeia de filtros**: PasswordEncryptFilter → XSSFilter → AuthFilter
- **Processamento de autenticação**: Validação de tokens e autorização
- **Roteamento**: ServletDispatcher → HttpExecutor → Controllers
- **Camadas de serviço**: Proxy com cache → Implementação → DAO
- **Processamento de resposta**: Preparação e renderização JSP

### [📊 Diagramas de Sequência e Performance](./docs/DIAGRAMA_SEQUENCIA_LISTAGEM_PRODUTOS.md)
Diagramas visuais e análise de performance, incluindo:
- **Diagrama de sequência UML** completo do fluxo
- **Cronologia detalhada** com tempos típicos de execução
- **Pontos de otimização** e estratégias de performance
- **Fluxo simplificado** por camadas arquiteturais

### [📖 Visão Geral da Documentação](./docs/README.md)
Índice centralizado de toda a documentação técnica disponível, com:
- **Guias de uso** para cada tipo de documentação
- **Arquitetura da aplicação** em detalhes
- **Configurações de segurança** e considerações importantes
- **Métricas de performance** e monitoramento

Esta documentação é especialmente útil para:
- **Novos desenvolvedores** entendendo a arquitetura
- **Debug e troubleshooting** de problemas de performance
- **Otimização** de componentes específicos
- **Manutenção** e evolução do código