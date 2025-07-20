# Fluxo Completo de Requisição - Listagem de Produtos

Este documento descreve o fluxo completo de uma requisição de listagem de produtos que começa no `AuthFilter` até a resposta final.

## Visão Geral da Arquitetura

A aplicação utiliza uma arquitetura baseada em Servlets Java com os seguintes componentes principais:

- **Filters (Filtros)**: Interceptam e processam requisições antes de chegarem aos controllers
- **Dispatcher**: Gerencia o roteamento de requisições
- **Controllers**: Controlam a lógica de apresentação e coordenam chamadas aos serviços
- **Services**: Implementam a lógica de negócio
- **DAOs**: Gerenciam o acesso aos dados

## Fluxo Detalhado da Requisição

### 1. Cadeia de Filtros (Filter Chain)

Quando uma requisição chega à aplicação para `/api/v1/product/list`, ela passa pela seguinte cadeia de filtros na ordem configurada no `web.xml`:

#### 1.1 PasswordEncryptFilter
```java
// Localização: com.dev.servlet.infrastructure.security.PasswordEncryptFilter
```
- **Função**: Intercepta e processa parâmetros de senha para criptografia
- **URL Pattern**: `/api/*`
- **Processamento**: Aplica criptografia em campos de senha se presentes na requisição

#### 1.2 XSSFilter
```java
// Localização: com.dev.servlet.infrastructure.security.XSSFilter
```
- **Função**: Previne ataques XSS (Cross-Site Scripting)
- **URL Pattern**: `/api/*`
- **Processamento**: Sanitiza parâmetros de entrada removendo scripts maliciosos

#### 1.3 AuthFilter (Ponto de Início)
```java
// Localização: com.dev.servlet.infrastructure.security.AuthFilter
```
- **Função**: Valida autenticação e autorização do usuário
- **URL Pattern**: `/api/*`

**Processamento detalhado no AuthFilter:**

1. **Extração do Token**:
   ```java
   String token = (String) request.getSession().getAttribute("token");
   ```

2. **Validação do Token**:
   ```java
   if (!isValidToken(token) && !isAuthorizedRequest(request)) {
       log.warn("Unauthorized access to the service: {}, redirecting to login page", request.getRequestURI());
       redirectToLogin(response);
       return;
   }
   ```

3. **Verificação de Endpoints Pré-autorizados**:
   ```java
   private boolean isAuthorizedRequest(HttpServletRequest request) {
       var parser = EndpointParser.of(request.getServletPath());
       String controller = parser.getController(); // "ProductController"
       return controller != null && preAuthorizedPath.contains(controller);
   }
   ```

4. **Dispatch da Requisição**:
   ```java
   log.debug("Access to the endpoint: {}, authorized", request.getRequestURI());
   dispatcher.dispatch(request, response);
   ```

### 2. ServletDispatcherImpl

```java
// Localização: com.dev.servlet.adapter.internal.ServletDispatcherImpl
```

O `AuthFilter` delega para o `ServletDispatcherImpl` que gerencia:

#### 2.1 Rate Limiting
```java
if (rateLimitEnabled && !rateLimiter.acquireOrWait(WAIT_TIME)) {
    throw new ServiceException(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Please try again later.");
}
```

#### 2.2 Construção da Requisição
```java
Request request = RequestBuilder.newBuilder()
    .httpServletRequest(httpServletRequest)
    .complete()
    .retry(1)
    .build();
```

#### 2.3 Execução via HttpExecutor
```java
IHttpResponse<?> httpResponse = httpExecutor.call(request);
```

### 3. HttpExecutor

```java
// Localização: com.dev.servlet.adapter.internal.HttpExecutor
```

#### 3.1 Parsing do Endpoint
```java
EndpointParser parser = EndpointParser.of(request.getEndpoint());
// Para "/api/v1/product/list":
// - controller: "ProductController"
// - apiVersion: "v1"
// - endpoint: "list"
```

#### 3.2 Resolução do Controller
```java
BaseRouterController router = (BaseRouterController) BeanUtil.getResolver()
    .getService(parser.getController()); // "ProductController"
```

#### 3.3 Roteamento
```java
IHttpResponse<TResponse> response = router.route(parser, request);
```

### 4. BaseRouterController

```java
// Localização: com.dev.servlet.controller.base.BaseRouterController
```

#### 4.1 Mapeamento de Rotas
O `BaseRouterController` inicializa um mapa de rotas baseado nas anotações `@RequestMapping`:

```java
private void initRouteMapping() {
    for (Method method : this.getClass().getDeclaredMethods()) {
        if (!method.isAnnotationPresent(RequestMapping.class)) {
            continue;
        }
        var requestMapping = method.getAnnotation(RequestMapping.class);
        String serviceController = requestMapping.value().substring(1); // "/list" → "list"
        routeMappings.put(serviceController, new RouteMapping(method));
    }
}
```

#### 4.2 Validação da Requisição
```java
RequestValidator.validate(endpoint, requestMapping, request);
```

#### 4.3 Preparação dos Argumentos
```java
Object[] args = prepareMethodArguments(routeMapping, request);
```

#### 4.4 Invocação do Método
```java
return invokeServiceMethod(this, routeMapping.method(), args);
```

### 5. ProductController

```java
// Localização: com.dev.servlet.controller.ProductController
```

#### 5.1 Método list()
```java
@RequestMapping(value = "/list")
public IServletResponse list(Request request) {
    // 1. Extração do filtro da requisição
    Product filter = productService.getEntity(request);
    
    // 2. Busca paginada dos produtos
    IPageable<ProductDTO> page = getAllPageable(request.getQuery().getPageRequest(), filter);
    
    // 3. Cálculo do preço total
    BigDecimal price = calculateTotalPrice(page, filter);
    
    // 4. Busca das categorias
    Collection<CategoryDTO> categories = categoryService.list(request.withToken());
    
    // 5. Preparação da resposta
    Set<KeyPair> container = new HashSet<>();
    container.add(new KeyPair("pageable", page));
    container.add(new KeyPair("totalPrice", price));
    container.add(new KeyPair("categories", categories));
    
    return newServletResponse(container, forwardTo("listProducts"));
}
```

#### 5.2 Método getAllPageable()
```java
private IPageable<ProductDTO> getAllPageable(IPageRequest<Product> pageRequest, Product filter) {
    pageRequest.setFilter(filter);
    return productService.getAllPageable(pageRequest, ProductMapper::base);
}
```

### 6. Camada de Serviços

#### 6.1 ProductServiceProxyImpl (Proxy com Cache)
```java
// Localização: com.dev.servlet.domain.service.internal.proxy.ProductServiceProxyImpl
```

- **Função**: Decorador que adiciona funcionalidades de cache
- **Responsabilidades**:
  - Gerenciamento de cache para operações de leitura
  - Invalidação de cache para operações de escrita
  - Delegação para o serviço real

#### 6.2 ProductServiceImpl (Implementação Real)
```java
// Localização: com.dev.servlet.domain.service.internal.ProductServiceImpl
```

- **Função**: Implementa a lógica de negócio para produtos
- **Métodos principais**:
  - `getAllPageable()`: Busca paginada com filtros
  - `getEntity()`: Conversão de parâmetros da requisição para entidade
  - `calculateTotalPriceFor()`: Cálculo de preços

### 7. Camada de Acesso a Dados

#### 7.1 ProductDAO
```java
// Localização: com.dev.servlet.infrastructure.persistence.dao.ProductDAO
```

- **Função**: Acesso direto aos dados de produtos
- **Responsabilidades**:
  - Consultas ao banco de dados
  - Mapeamento objeto-relacional
  - Implementação de filtros e paginação

### 8. Processamento da Resposta

#### 8.1 Volta ao ServletDispatcherImpl
Após a execução do controller, a resposta retorna ao `ServletDispatcherImpl`:

```java
private void processResponse(HttpServletRequest httpRequest, HttpServletResponse httpResponse, 
                           Request request, IHttpResponse<?> response) throws ServiceException {
    processResponseData(httpRequest, httpResponse, request, response);
    if (response.next() == null) return;
    
    String[] path = response.next().split(":");
    String pathAction = path[0]; // "forward"
    String pathUrl = path[1];    // "listProducts"
    
    if ("forward".equalsIgnoreCase(pathAction)) {
        httpRequest.getRequestDispatcher("/WEB-INF/view/" + pathUrl).forward(httpRequest, httpResponse);
    } else {
        httpResponse.sendRedirect(pathUrl);
    }
}
```

#### 8.2 Forward para JSP
Para a listagem de produtos, é feito um forward para:
```
/WEB-INF/view/listProducts.jsp
```

## Diagrama de Fluxo

```
Cliente
  ↓
[PasswordEncryptFilter]
  ↓
[XSSFilter]
  ↓
[AuthFilter] ← PONTO DE INÍCIO
  ↓ (validação token)
  ↓ (verificação autorização)
  ↓
[ServletDispatcherImpl]
  ↓ (rate limiting)
  ↓ (construção Request)
  ↓
[HttpExecutor]
  ↓ (parsing endpoint)
  ↓ (resolução controller)
  ↓
[BaseRouterController]
  ↓ (mapeamento rota)
  ↓ (validação)
  ↓ (preparação argumentos)
  ↓
[ProductController.list()]
  ↓ (extração filtros)
  ↓ (busca produtos)
  ↓ (cálculo preços)
  ↓ (busca categorias)
  ↓
[ProductServiceProxyImpl]
  ↓ (verificação cache)
  ↓
[ProductServiceImpl]
  ↓ (lógica negócio)
  ↓
[ProductDAO]
  ↓ (consulta banco)
  ↓
[Banco de Dados]
  ↓
[Resposta processada]
  ↓
[Forward para JSP]
  ↓
[Renderização HTML]
  ↓
Cliente
```

## Tratamento de Erros

Durante todo o fluxo, diversos pontos implementam tratamento de erros:

1. **AuthFilter**: Redirecionamento para login em caso de não autorização
2. **ServletDispatcherImpl**: Páginas de erro personalizadas
3. **HttpExecutor**: Retry automático e tratamento de exceções
4. **Controllers**: Validação de entrada e códigos de status HTTP
5. **Services**: Exceções de negócio
6. **DAOs**: Exceções de acesso a dados

## Configurações Importantes

### web.xml
```xml
<!-- Ordem dos filtros é importante -->
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

### Propriedades de Autorização
```java
// Configuração de controllers pré-autorizados
List<String> defaultAuthorized = List.of("LoginController,UserController");
preAuthorizedPath = PropertiesUtil.getProperty("auth.authorized", defaultAuthorized);
```

## Considerações de Performance

1. **Cache**: Implementado via `ProductServiceProxyImpl`
2. **Rate Limiting**: Controlado pelo `ServletDispatcherImpl`
3. **Paginação**: Evita carregar todos os produtos de uma vez
4. **Lazy Loading**: Carregamento sob demanda de dados relacionados

Este fluxo garante segurança, performance e manutenibilidade da aplicação através de uma arquitetura bem estruturada e separação clara de responsabilidades.