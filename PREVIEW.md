# Architecture & Technical Overview

Frontend framework focused performance, and modular CSS architecture.

## Summary
- Design System
- Application Interfaces
- Technical Implementation

---

## Design System

Modular CSS structure, separating tokens, base styles, reusable components, and themes. Uses custom properties and isolated scopes for critical components.

---

## Interfaces

- Home: health dashboard and navigation.
- Health: real-time monitoring of dependencies and readiness.
- Audit Trail: event history and traceability.

---

## Technical Implementation

- Structured logging and correlationId tracking.
- Componentization via JSP and annotation-based controllers.
- Centralized authentication and authorization pipeline.
- MVC decoupled from HttpServletRequest/Response.
- Rate limiting (Leaky Bucket) and multi-layer cache.
- Dependency injection via CDI.

### Implementation Details

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

@Controller("product")
public interface ProductControllerApi {
    @RequestMapping(value = "/create", method = POST, jsonType = ProductRequest.class)
    IHttpResponse<Void> register(ProductRequest request, String auth);
    // ...other methods omitted...
}
```

### MVC Flow

- Dispatcher applies rate limiting, builds Request, resolves controller, and executes method.
- Response is processed and forwarded according to the defined strategy.

### Package Structure

- adapter: web, messaging, filters, etc.
- application: use cases, mappers, ports.
- domain: entities, enums, value objects.
- infrastructure: config, health, persistence, utils.
- shared: common enums and utilities.

---

A demo application is available. For execution instructions, see the README.md.

Detailed documentation and complete examples are available in the source code.
