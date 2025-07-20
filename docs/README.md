# Documentação do Fluxo de Requisições - Servlets

## Visão Geral

Esta documentação explica o fluxo completo de requisições na aplicação servlet, com foco especial no processo de listagem de produtos que começa no `AuthFilter`.

## Documentos Disponíveis

### 📋 [Fluxo Completo de Requisição - Listagem de Produtos](./FLUXO_REQUISICAO_LISTAGEM_PRODUTOS.md)
Documentação detalhada e passo-a-passo do fluxo completo de uma requisição de listagem de produtos, incluindo:
- Cadeia de filtros (Filter Chain)
- Processamento de autenticação e autorização
- Roteamento de requisições
- Camadas de serviço e acesso a dados
- Processamento de resposta e renderização

### 📊 [Diagrama de Sequência - Fluxo de Listagem](./DIAGRAMA_SEQUENCIA_LISTAGEM_PRODUTOS.md)
Diagramas visuais e cronologia detalhada do fluxo, incluindo:
- Diagrama de sequência UML completo
- Fluxo simplificado por camadas
- Cronologia com tempos típicos de execução
- Pontos de otimização e performance

## Arquitetura da Aplicação

A aplicação segue uma arquitetura em camadas bem definida:

```
┌─────────────────────────────────────────┐
│              Camada Web                 │
│         (Filters + Servlets)            │
├─────────────────────────────────────────┤
│           Camada Controller             │
│        (ProductController, etc.)        │
├─────────────────────────────────────────┤
│            Camada Service               │
│    (ProductService + Proxy/Cache)       │
├─────────────────────────────────────────┤
│         Camada Data Access              │
│           (DAOs + Hibernate)            │
├─────────────────────────────────────────┤
│            Banco de Dados               │
│            (PostgreSQL)                 │
└─────────────────────────────────────────┘
```

## Componentes Principais

### Filtros de Segurança
1. **PasswordEncryptFilter**: Criptografia de senhas
2. **XSSFilter**: Proteção contra ataques XSS
3. **AuthFilter**: Autenticação e autorização *(ponto de início)*

### Roteamento e Dispatch
- **ServletDispatcherImpl**: Gerenciamento central de requisições
- **HttpExecutor**: Execução e roteamento para controllers
- **BaseRouterController**: Roteamento baseado em anotações

### Camada de Negócio
- **ProductController**: Controller de produtos
- **ProductServiceProxy**: Proxy com cache
- **ProductServiceImpl**: Implementação da lógica de negócio

### Acesso a Dados
- **ProductDAO**: Acesso aos dados de produtos
- **Hibernate/JPA**: Mapeamento objeto-relacional

## Fluxo de Exemplo - Listagem de Produtos

```http
GET /api/v1/product/list HTTP/1.1
Host: localhost:8080
Cookie: JSESSIONID=ABC123...
```

**Processamento:**
1. ✅ Filtros de segurança aplicados
2. ✅ Token de sessão validado
3. ✅ Autorização verificada (ProductController pré-autorizado)
4. ✅ Rate limiting aplicado
5. ✅ Requisição roteada para `ProductController.list()`
6. ✅ Cache verificado via `ProductServiceProxy`
7. ✅ Dados buscados via `ProductDAO`
8. ✅ Resposta processada e enviada para JSP

**Resposta:**
```http
HTTP/1.1 200 OK
Content-Type: text/html
```

## Configurações de Segurança

### web.xml - Ordem dos Filtros
```xml
<!-- IMPORTANTE: A ordem dos filtros é crucial -->
<filter-mapping>
    <filter-name>SecurityPasswordEncryptionFilter</filter-name>
    <url-pattern>/api/*</url-pattern>
</filter-mapping>
<filter-mapping>
    <filter-name>SecurityXSSFilter</filter-name>
    <url-pattern>/api/*</url-pattern>
</filter-mapping>
<filter-mapping>
    <filter-name>SecurityAuthFilter</filter-name>
    <url-pattern>/api/*</url-pattern>
</filter-mapping>
```

### Controllers Pré-autorizados
Por padrão, os seguintes controllers não requerem autenticação:
- `LoginController`
- `UserController`

Configurável via propriedade `auth.authorized`.

## Performance e Otimizações

### Cache
- **Nível de Serviço**: `ProductServiceProxy` implementa cache com EHCache
- **Invalidação**: Cache invalidado automaticamente em operações de escrita

### Rate Limiting
- **Implementação**: Leaky Bucket algorithm
- **Configuração**: Configurável via propriedade `rate.limit.enabled`
- **Timeout**: 600ms de espera máxima

### Paginação
- **Implementação**: Suporte nativo a paginação em todos os endpoints de listagem
- **Performance**: Evita carregar grandes volumes de dados

## Monitoramento e Logs

### Logging
- **Framework**: SLF4J + Logback
- **Níveis**: DEBUG para fluxo detalhado, WARN para problemas de autorização
- **Interceptors**: `LogExecutionTimeInterceptor` para medição de performance

### Métricas Importantes
- Tempo de resposta por endpoint
- Taxa de cache hit/miss
- Número de requisições bloqueadas por rate limiting
- Falhas de autenticação/autorização

## Tratamento de Erros

### Códigos de Status HTTP
- **200**: Sucesso
- **401**: Não autorizado (redirecionamento para login)
- **403**: Proibido
- **404**: Endpoint não encontrado
- **429**: Rate limit excedido
- **500**: Erro interno do servidor

### Páginas de Erro
- **Customizadas**: Templates HTML personalizados com imagens
- **Informativas**: Mensagens de erro claras para o usuário
- **Logs**: Detalhes técnicos registrados nos logs para debugging

## Como Usar Esta Documentação

1. **Para entender o fluxo completo**: Leia o [documento principal](./FLUXO_REQUISICAO_LISTAGEM_PRODUTOS.md)
2. **Para visualizar o fluxo**: Consulte os [diagramas de sequência](./DIAGRAMA_SEQUENCIA_LISTAGEM_PRODUTOS.md)
3. **Para debugging**: Use os logs e pontos de medição descritos
4. **Para otimização**: Consulte as seções de performance e cache

## Próximos Passos

- [ ] Documentar fluxos de outras operações (CREATE, UPDATE, DELETE)
- [ ] Adicionar métricas de performance detalhadas
- [ ] Implementar circuit breaker pattern
- [ ] Adicionar testes de integração para o fluxo completo