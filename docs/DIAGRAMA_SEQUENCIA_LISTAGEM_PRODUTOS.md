# Diagrama de Sequência - Fluxo de Listagem de Produtos

## Diagrama de Sequência UML

```mermaid
sequenceDiagram
    participant Client as Cliente
    participant PEF as PasswordEncryptFilter
    participant XSS as XSSFilter  
    participant AF as AuthFilter
    participant SD as ServletDispatcherImpl
    participant RL as RateLimiter
    participant HE as HttpExecutor
    participant EP as EndpointParser
    participant BRC as BaseRouterController
    participant PC as ProductController
    participant PSP as ProductServiceProxy
    participant PS as ProductServiceImpl
    participant PD as ProductDAO
    participant DB as Database
    participant JSP as listProducts.jsp

    Client->>PEF: GET /api/v1/product/list
    PEF->>PEF: Process password encryption (if any)
    PEF->>XSS: Continue filter chain
    
    XSS->>XSS: Sanitize XSS attacks
    XSS->>AF: Continue filter chain
    
    Note over AF: PONTO DE INÍCIO
    AF->>AF: Extract token from session
    AF->>AF: Validate token
    AF->>EP: Parse servlet path
    EP-->>AF: Return controller="ProductController"
    AF->>AF: Check if ProductController is pre-authorized
    
    alt Token invalid AND not pre-authorized
        AF-->>Client: Redirect to login page (401)
    else Authorized
        AF->>SD: dispatcher.dispatch(request, response)
        
        SD->>RL: Check rate limit
        RL-->>SD: Rate limit OK
        
        SD->>SD: Build Request object
        SD->>HE: httpExecutor.call(request)
        
        HE->>EP: EndpointParser.of("/api/v1/product/list")
        EP-->>HE: controller="ProductController", endpoint="list"
        
        HE->>HE: Resolve ProductController bean
        HE->>BRC: router.route(parser, request)
        
        BRC->>BRC: Find route mapping for "list"
        BRC->>BRC: Validate request parameters
        BRC->>BRC: Prepare method arguments
        BRC->>PC: list(request)
        
        PC->>PSP: getEntity(request)
        PSP-->>PC: Product filter
        
        PC->>PSP: getAllPageable(pageRequest, filter)
        PSP->>PSP: Check cache
        PSP->>PS: getAllPageable(pageRequest, mapper)
        PS->>PD: findByFilter(filter, pageRequest)
        PD->>DB: SELECT * FROM products WHERE...
        DB-->>PD: Product records
        PD-->>PS: List<Product>
        PS->>PS: Map to ProductDTO
        PS-->>PSP: IPageable<ProductDTO>
        PSP->>PSP: Update cache
        PSP-->>PC: IPageable<ProductDTO>
        
        PC->>PSP: calculateTotalPriceFor(filter)
        PSP->>PS: calculateTotalPriceFor(filter)
        PS->>PD: calculateTotalPrice(filter)
        PD->>DB: SELECT SUM(price) FROM products WHERE...
        DB-->>PD: BigDecimal totalPrice
        PD-->>PS: BigDecimal totalPrice
        PS-->>PSP: BigDecimal totalPrice
        PSP-->>PC: BigDecimal totalPrice
        
        PC->>PC: categoryService.list(request.withToken())
        PC-->>PC: Collection<CategoryDTO>
        
        PC->>PC: Build response container
        PC-->>BRC: IServletResponse with "forward:listProducts"
        BRC-->>HE: IHttpResponse
        HE-->>SD: IHttpResponse
        
        SD->>SD: processResponse()
        SD->>SD: Parse "forward:listProducts"
        SD->>JSP: RequestDispatcher.forward()
        JSP->>JSP: Render HTML with product data
        JSP-->>Client: HTML Response
    end
```

## Fluxo Simplificado por Camadas

```mermaid
graph TD
    A[Cliente] --> B[Filter Chain]
    B --> C[Authentication & Authorization]
    C --> D[Request Dispatch]
    D --> E[Route Resolution]
    E --> F[Controller Layer]
    F --> G[Service Layer]
    G --> H[Data Access Layer]
    H --> I[Database]
    I --> J[Response Processing]
    J --> K[View Rendering]
    K --> L[Cliente]

    subgraph "Filter Chain"
        B1[PasswordEncryptFilter]
        B2[XSSFilter]
        B3[AuthFilter]
    end

    subgraph "Request Processing"
        D1[ServletDispatcherImpl]
        D2[Rate Limiting]
        D3[Request Building]
    end

    subgraph "Routing"
        E1[HttpExecutor]
        E2[EndpointParser]
        E3[BaseRouterController]
    end

    subgraph "Business Logic"
        F1[ProductController]
        G1[ProductServiceProxy]
        G2[ProductServiceImpl]
    end

    subgraph "Data Layer"
        H1[ProductDAO]
        H2[Hibernate/JPA]
    end
```

## Cronologia Detalhada

| Passo | Componente | Ação | Duração Típica |
|-------|------------|------|----------------|
| 1 | PasswordEncryptFilter | Processar criptografia de senhas | < 1ms |
| 2 | XSSFilter | Sanitizar entrada contra XSS | < 1ms |
| 3 | AuthFilter | Validar token e autorização | 2-5ms |
| 4 | ServletDispatcherImpl | Rate limiting e construção de Request | 1-3ms |
| 5 | HttpExecutor | Parsing e resolução de controller | 1-2ms |
| 6 | BaseRouterController | Mapeamento de rota e validação | 2-3ms |
| 7 | ProductController | Coordenação da lógica de negócio | 5-10ms |
| 8 | ProductServiceProxy | Verificação e gestão de cache | 1-2ms |
| 9 | ProductServiceImpl | Processamento da lógica de negócio | 3-5ms |
| 10 | ProductDAO | Acesso aos dados | 10-50ms |
| 11 | Database | Execução de consultas SQL | 5-100ms |
| 12 | Response Processing | Processamento da resposta | 2-5ms |
| 13 | JSP Rendering | Renderização da view | 5-15ms |

**Tempo Total Típico**: 35-200ms (dependendo da complexidade da consulta e cache)

## Pontos de Otimização

### Cache Hits
- **ProductServiceProxy**: Cache de produtos pode reduzir tempo de 50ms para 2ms
- **Session Cache**: Token validation cache reduz overhead de autenticação

### Database Optimization
- **Indices**: Indices apropriados em campos de filtro
- **Query Optimization**: Consultas otimizadas para paginação
- **Connection Pooling**: Pool de conexões gerenciado pelo Hibernate

### Performance Monitoring
- **Rate Limiting**: Previne sobrecarga do sistema
- **Retry Logic**: Recuperação automática de falhas temporárias
- **Circuit Breaker**: Proteção contra cascateamento de falhas