# 📸 UI/UX Architecture & Technical Showcase

> Java/JSO frontend implementation with accessibility-first design, performance optimization, and modern CSS
> architecture.

## Table of Contents

- [Design System Architecture](#design-system-architecture)
- [Application Interfaces](#application-interfaces)
    - [Product Management](#product-management)
    - [Health Monitoring Dashboard](#health-monitoring-dashboard)
    - [Activity History & Audit Trail](#activity-history--audit-trail)
- [Technical Implementation](#technical-implementation)

---

## Design System Architecture

### CSS Architecture & Methodology

#### Modular CSS Structure

```
css/
├── variables.css        → Design tokens (CSS Custom Properties)
├── styles.css          → Base styles, typography, grid system
├── navbar.css          → Navigation component encapsulation
├── components.css      → Reusable UI components
├── dark-mode.css       → Dark theme overrides (prefers-color-scheme)
├── fixes.css           → Cross-browser compatibility patches
└── login.css           → Authentication pages (isolated scope)
```

---

## Application Interfaces

### Home Page

<p align="center">
  <img src="images/homepage.png" alt="Health Dashboard" width="700">
  <br>
  <em>Home page</em>
</p>
---

## Health Monitoring Dashboard

### Real-Time System Observability

<p align="center">
  <img src="images/health.png" alt="Health Dashboard - Lighthouse" width="800">
  <br>
  <em>Product view interface</em>
</p>

<p align="center">
  <img src="images/health-ready-dark.png" alt="Readiness Probe - Dark Mode" width="800">
  <br>
  <em>Readiness probe endpoint with dependency health checks</em>
</p>

## Activity History & Audit Trail

### Audit Logging UI

<p align="center">
  <img src="images/history-view.png" alt="Timeline - Dark Mode" width="800">
  <br>
  <em>Timeline view with event filtering and correlation tracking</em>
</p>

<p align="center">
  <img src="images/history-detail.png" alt="Activity Detail - Light Mode" width="800">
  <br>
  <em>Light theme variant with optimized contrast for daytime viewing</em>
</p>
<p align="center">
  <img src="images/doc-page.png" alt="Doc - Light Mode" width="800">
  <br>
  <em>Documentation page with code samples and Endpoint references</em>
</p>

### Technical Implementation

#### Audit Log Schema

```json
{
  "schemaVersion": "1.0",
  "correlationId": "7b558145-e976-4536-bee1-b375026ee973",
  "event": "product:find_by_id",
  "outcome": "SUCCESS",
  "timestamp": "2025-11-25T16:15:02.089861800Z",
  "httpMethod": "GET",
  "userId": "85f66ff4-6b90-4bb8-aa20-9b9ea78e2014",
  "endpoint": "/api/v1/product/list/01148ae5-fc1b-4246-8817-e3bc5b4311dc",
  "payload": {
    "input": {
      "id": "01148ae5-fc1b-4246-8817-e3bc5b4311dc"
    },
    "output": {
      "id": "01148ae5-fc1b-4246-8817-e3bc5b4311dc",
      "name": "Women's High Heel Sandals",
      "description": "Step out in style with our Women's High Heel Sandals. These sandals feature a strappy design that adds a touch of elegance to any outfit. The comfortable footbed and sturdy heel make them perfect for a night out, while the buckle closure ensures a secure fit. Choose from black, red, nude, or silver to complement your wardrobe.",
      "url": "any-url-here",
      "status": "A",
      "registerDate": 1763953200000,
      "price": 59.99
    }
  },
  "ipAddress": "72.14.201.219",
  "startedAt": "1764087302076",
  "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36"
}
```

**CSS Implementation:**

```css
.card {
    background: var(--card-bg);
    border-radius: var(--border-radius-lg);
    box-shadow: var(--shadow-sm);
    overflow: hidden;
    transition: box-shadow var(--transition-base);
}
```

### Button System

#### Reusable Components

```js
<!-- Search Component -->
<jsp:include page="/WEB-INF/view/components/search.jsp">
    <jsp:param name="placeholder" value="Search product"/>
    <jsp:param name="action" value="${baseLink}${version}${ searchProduct }"/>
    <jsp:param name="onclear" value="${baseLink}${version}${ listProduct }"/>
    <jsp:param name="limit" value="${ pageable.getPageSize() }"/>
    <jsp:param name="categories" value="${ categories }"/>
    <jsp:param name="searchType" value="name"/>
</jsp:include>

<!-- Pagination Component -->
<jsp:include page="/WEB-INF/view/components/pagination.jsp">
    <jsp:param name="totalRecords" value="${pageable.getTotalElements()}"/>
    <jsp:param name="currentPage" value="${pageable.getCurrentPage()}"/>
    <jsp:param name="totalPages" value="${pageable.getTotalPages()}"/>
    <jsp:param name="pageSize" value="${pageable.getPageSize()}"/>
    <jsp:param name="sort" value="${pageable.getSort().getField()}"/>
    <jsp:param name="direction" value="${pageable.getSort().getDirection().getValue()}"/>
    <jsp:param name="k" value="${k}"/>
    <jsp:param name="q" value="${q}"/>
</jsp:include>

<!-- Custom Button usage -->
<jsp:include page="/WEB-INF/view/components/buttons/customButton.jsp">
    <jsp:param name="btnLabel" value="Back"/>
    <jsp:param name="btnType" value="button"/>
    <jsp:param name="btnClass" value="btn btn-light"/>
    <jsp:param name="btnIcon" value="bi bi-arrow-left"/>
    <jsp:param name="btnOnclick" value="onclick='history.back()'"/>
    <jsp:param name="btnId" value="id='backButton'"/>
</jsp:include>
```

### Technical Architecture

#### Product View Implementation

```java
// ===== Annotations =====
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value();

    RequestMethod method() default RequestMethod.GET;

    Class<?> jsonType() default Void.class;

    boolean requestAuth() default true;

    String apiVersion() default "v1";

    RoleType[] roles() default {};

    // ===== For API Documentation =====
    String description() default "";
}

// Cache Annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
    String value() default "";

    String[] invalidate() default {};

    long duration() default 1800; // Default is 30 minutes

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}

``` 

#### Product Controller API

```java
/**
 * Note: if the interfaces have any @ injections, the framework will handle it automatically
 */
@Controller("product")
public interface ProductControllerApi {

    @RequestMapping(
            value = "/create",
            method = POST,
            jsonType = ProductRequest.class,
            description = "Create a new product."
    )
    IHttpResponse<Void> register(ProductRequest request, String auth);

    @RequestMapping(
            value = "/new",
            description = "Forward to the product registration page and retrieve available categories."
    )
    IHttpResponse<Collection<CategoryResponse>> forward(String auth);

    @RequestMapping(
            value = "/details/{id}",
            jsonType = ProductRequest.class,
            description = "Retrieve detailed information about a specific product by ID."
    )
    IServletResponse details(ProductRequest request, String auth);

    @RequestMapping(
            value = "/search",
            description = "Search products based on query parameters with pagination."
    )
    IServletResponse search(Query query, IPageRequest pageRequest, String auth);

    @RequestMapping(
            value = "/list",
            jsonType = ProductRequest.class,
            description = "Retrieve paginated list of products."
    )
    IServletResponse list(IPageRequest pageRequest, String auth);

    @RequestMapping(
            value = "/list/{id}",
            jsonType = ProductRequest.class,
            description = "Retrieve product information by ID."
    )
    IHttpResponse<ProductResponse> findById(ProductRequest request, String auth);

    @RequestMapping(
            value = "/update/{id}",
            method = POST,
            jsonType = ProductRequest.class,
            description = "Update an existing product."
    )
    IHttpResponse<Void> update(ProductRequest request, String auth);

    @RequestMapping(
            value = "/delete/{id}",
            method = POST,
            jsonType = ProductRequest.class,
            description = "Delete a product by ID."
    )
    IHttpResponse<Void> delete(ProductRequest filter, String auth);

    @RequestMapping(
            value = "/scrape",
            method = POST,
            description = "Scrape product information from an external URL. DEMO or TEST use only. Runs asynchronously.",
            async = true
    )
    IHttpResponse<Void> scrape(String auth, String url);

    @RequestMapping(
            value = "/upload-picture/{id}",
            apiVersion = "v2", // V2 only
            method = POST,
            jsonType = FileUploadRequest.class,
            description = "Upload a product picture. Accepts file upload. V2 API."
    )
    IHttpResponse<Void> upload(FileUploadRequest request, String auth);
}
```

#### Product Controller Implementation

```java
@Slf4j
@ApplicationScoped
public class ProductController extends BaseController implements ProductControllerApi {
    // Injections omitted for brevity

    @Override
    protected Class<ProductController> implementation() {
        return ProductController.class;
    }

    @SneakyThrows
    public IHttpResponse<Void> register(ProductRequest request, @Authorization String auth) {
        ProductResponse product = createProductWithThumbUseCase.execute(request, auth);
        return newHttpResponse(201, redirectTo(product.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Collection<CategoryResponse>> forward(@Authorization String auth) {
        var categories = listCategoryUseCase.list(null, auth);
        return newHttpResponse(302, categories, forwardTo("formCreateProduct"));
    }

    @SneakyThrows
    public IServletResponse details(ProductRequest request, @Authorization String auth) {
        ProductResponse response = this.findById(request, auth).body();
        Collection<CategoryResponse> categories = listCategoryUseCase.list(null, auth);
        Set<KeyPair> body = Set.of(
                new KeyPair("product", response),
                new KeyPair("categories", categories)
        );
        return newServletResponse(body, forwardTo("formUpdateProduct"));
    }

    @SneakyThrows
    public IServletResponse search(Query query, IPageRequest pageRequest, @Authorization String auth) {
        User user = this.auth.extractUser(auth);
        Product product = mapper.queryToProduct(query, user);
        Set<KeyPair> container = listProductContainerUseCase.assembleContainerResponse(pageRequest, auth, product);
        return newServletResponse(container, forwardTo("listProducts"));
    }

    @SneakyThrows
    public IServletResponse list(IPageRequest pageRequest, @Authorization String auth) {
        Product product = mapper.toProduct(null, authenticationPort.extractUserId(auth));
        Set<KeyPair> container = listProductContainerUseCase.assembleContainerResponse(pageRequest, auth, product);
        return newServletResponse(container, forwardTo("listProducts"));
    }

    @SneakyThrows
    public IHttpResponse<ProductResponse> findById(ProductRequest request, @Authorization String auth) {
        ProductResponse product = productDetailUseCase.get(request, auth);
        return okHttpResponse(product, forwardTo("formListProduct"));
    }

    @SneakyThrows
    public IHttpResponse<Void> update(ProductRequest request, @Authorization String auth) {
        ProductResponse response = updateProductUseCase.update(request, auth);
        return newHttpResponse(204, redirectTo(response.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Void> delete(ProductRequest filter, @Authorization String auth) {
        deleteProductUseCase.delete(filter, auth);
        return HttpResponse.<Void>next(redirectToCtx(LIST)).build();
    }

    @SneakyThrows
    @Async
    public IHttpResponse<Void> scrape(@Authorization String auth,
                                      @Property("scrape_product_url") String url) {
        scrapeProductUseCase.scrape(url, auth);
        return HttpResponse.<Void>next(redirectToCtx(LIST)).build();
    }

    @Override
    public IHttpResponse<Void> upload(FileUploadRequest request, @Authorization String auth) {
        updateProductThumbUseCase.updateThumb(request, auth);
        return newHttpResponse(204, redirectTo(request.id()));
    }
}

```

#### Auth Use Case Implementation

```java

import com.servletstack.application.port.in.auth.LoginUseCase;

@Slf4j
@ApplicationScoped
public class LoginService implements LoginUseCase {
    // Dependencies ommitted for brevity

    @Override
    public IHttpResponse<UserResponse> login(LoginRequest credentials, String onSuccess) throws AppException {
        final String login = credentials.login();

        try {
            if (Properties.isDemoModeEnabled()) {
                log.debug("LoginUseCase: DEMO_MODE is enabled, bypassing authentication for user {}", login);
                User demoUser = userDemoModeUseCase.validateCredentials(credentials);
                UserResponse response = authenticate(demoUser);
                return HttpResponse.ok(response).next(onSuccess).build();
            }

            User user = userPort.get(new UserRequest(login, null))
                    .orElseThrow(() -> new AppException(SC_UNAUTHORIZED, "Invalid login or password"));

            boolean verified = PasswordHasher.verify(credentials.password(), user.getCredentials().getPassword());
            if (!verified) throw new AppException(SC_UNAUTHORIZED, "Invalid login or password");

            if (Status.PENDING.equals(user.getStatus())) {
                UserResponse userResponse = UserResponse.builder()
                        .id(user.getId())
                        .unconfirmedEmail(true)
                        .build();
                return HttpResponse.ok(userResponse).next("forward:pages/formLogin.jsp").build();
            }

            UserResponse response = authenticate(user);
            return HttpResponse.ok(response).next(onSuccess).build();

        } catch (Exception e) {
            log.warn("LoginUseCase: login failed for user {}: {}", login, e.getMessage());

            return HttpResponse.<UserResponse>newBuilder()
                    .statusCode(SC_UNAUTHORIZED)
                    .error("Invalid login or password")
                    .reasonText("Unauthorized")
                    .next("forward:pages/formLogin.jsp")
                    .build();
        }
    }
}
```

### Auth Filter Implementation

```java

@Slf4j
@ApplicationScoped
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (isAuthorizedRequest(request)) {
            chain.doFilter(req, res);
            return;
        }

        Cookie[] cookies = request.getCookies();
        String token = authCookiePort.getCookieFromArray(cookies, authCookiePort.getAccessTokenCookieName());
        String refreshToken = authCookiePort.getCookieFromArray(cookies, authCookiePort.getRefreshTokenCookieName());

        if (token == null && refreshToken == null) {
            log.warn("No tokens found for: {}", request.getRequestURI());
            redirectToLogin(response);
            return;
        }

        if (token != null && authenticationPort.validateToken(token)) {
            log.debug("Valid token access [endpoint={}]", request.getRequestURI());
            chain.doFilter(req, res);
            return;
        }

        if (refreshToken != null && authenticationPort.validateToken(refreshToken)) {
            try {
                RefreshTokenResponse refresh = refreshTokenPort.refreshToken(BEARER_PREFIX + refreshToken);
                authCookiePort.setAuthCookies(response, refresh.token(), refresh.refreshToken());
                response.sendRedirect(request.getRequestURI());
                return;
            } catch (AppException e) {
                log.error("Failed to refresh token", e);
            }
        }

        log.warn("Invalid tokens for: {}", request.getRequestURI());
        authCookiePort.clearCookies(response);
        redirectToLogin(response);
    }
}
```

### MVC Request Flow

The framework's MVC flow starts with the `ServletDispatcherImpl.dispatch()` method, which:

1. Applies rate limiting using a Leaky Bucket algorithm (Header: X-Rate-Limit).
2. Builds a `Request` object from the `HttpServletRequest` (decoupling HttpServletRequest/Response).
3. Calls `HttpExecutor.call()` to resolve the controller and invoke the method.
4. Processes the response, sets headers (e.g., X-Correlation-ID), and forwards or redirects based on the `IHttpResponse.next()` value.

Controllers extend `BaseRouterController`, which uses reflection to map endpoints to methods annotated with `@RequestMapping`. Dependency injection is handled by CDI.


### Package Structure

```text
C:.
└───servlet
    ├───adapter
    │   ├───in
    │   │   ├───messaging
    │   │   │   └───consumer
    │   │   ├───web
    │   │   │   ├───annotation
    │   │   │   ├───builder
    │   │   │   ├───controller
    │   │   │   │   └───internal
    │   │   │   │       └───base
    │   │   │   ├───dispatcher
    │   │   │   │   └───impl
    │   │   │   ├───dto
    │   │   │   ├───filter
    │   │   │   │   └───wrapper
    │   │   │   ├───frontcontroller
    │   │   │   ├───introspection
    │   │   │   ├───listener
    │   │   │   ├───ratelimit
    │   │   │   │   └───internal
    │   │   │   ├───util
    │   │   │   ├───validator
    │   │   │   │   └───internal
    │   │   │   └───vo
    │   │   └───ws
    │   └───out
    │       ├───activity
    │       ├───alert
    │       ├───audit
    │       ├───cache
    │       ├───external
    │       │   └───webscrape
    │       │       ├───api
    │       │       ├───builder
    │       │       ├───service
    │       │       └───transfer
    │       ├───home
    │       ├───image
    │       ├───inventory
    │       ├───messaging
    │       │   ├───adapter
    │       │   ├───email
    │       │   ├───factory
    │       │   ├───producer
    │       │   └───registry
    │       ├───product
    │       ├───publicaccess
    │       ├───security
    │       ├───storage
    │       └───user
    ├───application
    │   ├───exception
    │   ├───mapper
    │   ├───port
    │   │   ├───in
    │   │   │   ├───activity
    │   │   │   ├───auth
    │   │   │   ├───category
    │   │   │   ├───product
    │   │   │   ├───stock
    │   │   │   ├───token
    │   │   │   └───user
    │   │   └───out
    │   │       ├───activity
    │   │       ├───alert
    │   │       ├───audit
    │   │       ├───cache
    │   │       ├───category
    │   │       ├───confirmtoken
    │   │       ├───image
    │   │       ├───inventory
    │   │       ├───product
    │   │       ├───publicaccess
    │   │       ├───refreshtoken
    │   │       ├───security
    │   │       ├───storage
    │   │       └───user
    │   ├───registry
    │   ├───transfer
    │   │   ├───request
    │   │   └───response
    │   └───usecase
    │       ├───activity
    │       ├───auth
    │       ├───category
    │       ├───product
    │       ├───stock
    │       └───user
    ├───domain
    │   ├───entity
    │   │   └───enums
    │   ├───enums
    │   └───vo
    ├───infrastructure
    │   ├───annotations
    │   ├───config
    │   ├───health
    │   │   └───internal
    │   ├───http
    │   ├───migration
    │   ├───persistence
    │   │   ├───repository
    │   │   │   └───base
    │   │   └───transfer
    │   │       └───internal
    │   └───utils
    └───shared
        ├───enums
        ├───util
        └───vo
```