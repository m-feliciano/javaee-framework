package com.dev.servlet.adapter.in.web.controller.internal.base;

import com.dev.servlet.adapter.in.web.annotation.Async;
import com.dev.servlet.adapter.in.web.annotation.Authorization;
import com.dev.servlet.adapter.in.web.annotation.Cache;
import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.Request;
import com.dev.servlet.adapter.in.web.util.EndpointParser;
import com.dev.servlet.adapter.in.web.validator.RequestValidator;
import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.domain.entity.enums.RequestMethod;
import jakarta.enterprise.context.control.RequestContextController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.dev.servlet.domain.entity.enums.RequestMethod.GET;
import static com.dev.servlet.domain.entity.enums.RequestMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test suite for BaseRouterController.
 * Tests routing, caching, async execution, and argument resolution.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BaseRouterController Tests")
class BaseRouterControllerTest {

    private static final String USER_ID = "user-123";
    private static final String VALID_AUTH_TOKEN = "valid.jwt.token";
    @Mock
    private AuthenticationPort authenticationPort;
    @Mock
    private RequestContextController requestContextController;
    @Mock
    private CachePort cachePort;
    private TestRouterController controller;

    @BeforeEach
    void setUp() {
        controller = new TestRouterController();
        controller.authenticationPort = authenticationPort;
        controller.requestContextController = requestContextController;
        controller.cachePort = cachePort;

        lenient().when(authenticationPort.extractUserId(VALID_AUTH_TOKEN)).thenReturn(USER_ID);
    }

    // Test Controller API Interface
    public interface TestControllerApi {
        @RequestMapping(value = "/simple", method = GET)
        IHttpResponse<String> simple(@Authorization String auth);

        @RequestMapping(value = "/cached", method = GET)
        IHttpResponse<String> cached(@Authorization String auth);

        @RequestMapping(value = "/cached-error", method = GET)
        IHttpResponse<String> cachedError(@Authorization String auth);

        @RequestMapping(value = "/invalidate-cache", method = POST)
        IHttpResponse<String> invalidateCache(@Authorization String auth);

        @RequestMapping(value = "/invalidate-multiple", method = POST)
        IHttpResponse<String> invalidateMultiple(@Authorization String auth);

        @RequestMapping(value = "/cache-ttl", method = POST)
        IHttpResponse<String> timedCache(@Authorization String auth);

        @RequestMapping(value = "/async", method = POST)
        IHttpResponse<String> async(@Authorization String auth);
    }

    // Test Controller Implementation
    public static class TestRouterController extends BaseRouterController implements TestControllerApi {

        @Override
        public IHttpResponse<String> simple(@Authorization String auth) {
            return HttpResponse.ok("simple").build();
        }

        @Override
        @Cache(value = "test_cache", duration = 5, timeUnit = TimeUnit.MINUTES)
        public IHttpResponse<String> cached(@Authorization String auth) {
            return HttpResponse.ok("cached").build();
        }

        @Override
        @Cache("test_cache")
        public IHttpResponse<String> cachedError(@Authorization String auth) {
            return HttpResponse.<String>newBuilder()
                    .statusCode(500)
                    .body("error")
                    .build();
        }

        @Override
        @Cache(invalidate = "test_cache")
        public IHttpResponse<String> invalidateCache(@Authorization String auth) {
            return HttpResponse.ok("invalidated").build();
        }

        @Override
        @Cache(invalidate = {"cache1", "cache2"})
        public IHttpResponse<String> invalidateMultiple(@Authorization String auth) {
            return HttpResponse.ok("invalidated-multiple").build();
        }

        @Override
        @Cache(value = "custom_ttl_cache", duration = 100, timeUnit = TimeUnit.MILLISECONDS)
        public IHttpResponse<String> timedCache(String auth) {
            return HttpResponse.ok("timed-cache").build();
        }

        @Override
        @Async
        public IHttpResponse<String> async(@Authorization String auth) {
            return HttpResponse.ok("async").build();
        }

        @Override
        protected Class<? extends BaseRouterController> implementation() {
            return TestRouterController.class;
        }
    }

    @Nested
    @DisplayName("Route Mapping Tests")
    class RouteMappingTests {

        @Test
        @DisplayName("Should route to correct implementation based on endpoint")
        void shouldRouteToCorrectMethod() throws Exception {
            try (var ignored = mockConstruction(RequestValidator.class,
                    (mock, context) -> doNothing().when(mock).validate(any(), any()))) {

                // Arrange
                EndpointParser endpoint = EndpointParser.of("/api/v1/test/simple");
                Request request = Request.builder()
                        .endpoint("/api/v1/test/simple")
                        .method(GET)
                        .token(VALID_AUTH_TOKEN)
                        .build();

                // Act
                IHttpResponse<String> response = controller.route(endpoint, request);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.statusCode()).isEqualTo(200);
                assertThat(response.body()).isEqualTo("simple");
            }
        }

        @Test
        @DisplayName("Should throw AppException when endpoint not found")
        void shouldThrowExceptionWhenEndpointNotFound() {
            try (var ignored = mockConstruction(RequestValidator.class,
                    (mock, context) -> doNothing().when(mock).validate(any(), any()))) {

                // Arrange
                EndpointParser endpoint = EndpointParser.of("/api/v1/test/nonexistent");
                Request request = Request.builder()
                        .endpoint("/api/v1/test/nonexistent")
                        .method(GET)
                        .build();

                // Act & Assert
                assertThatThrownBy(() -> controller.route(endpoint, request))
                        .isInstanceOf(AppException.class)
                        .hasMessageContaining("Api not implemented!");
            }
        }
    }

    @Nested
    @DisplayName("Cache Tests")
    class CacheTests {

        @Test
        @DisplayName("Should cache response when cacheKey is defined")
        void shouldCacheResponse() throws Exception {
            try (var ignored = mockConstruction(RequestValidator.class,
                    (mock, context) -> doNothing().when(mock).validate(any(), any()))) {

                // Arrange
                EndpointParser endpoint = EndpointParser.of("/api/v1/test/cached");
                Request request = Request.builder()
                        .endpoint("/api/v1/test/cached")
                        .method(GET)
                        .token(VALID_AUTH_TOKEN)
                        .build();

                when(cachePort.get(anyString(), eq(USER_ID))).thenReturn(null);

                // Act
                IHttpResponse<String> response = controller.route(endpoint, request);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.body()).isEqualTo("cached");
                verify(cachePort).get(eq("cached:test_cache"), eq(USER_ID));
                verify(cachePort).set(eq("cached:test_cache"), eq(USER_ID), any(IHttpResponse.class), eq(Duration.ofMinutes(5)));
            }
        }

        @Test
        @DisplayName("Should return cached response when available")
        void shouldReturnCachedResponse() throws Exception {
            try (var ignored = mockConstruction(RequestValidator.class,
                    (mock, context) -> doNothing().when(mock).validate(any(), any()))) {

                // Arrange
                EndpointParser endpoint = EndpointParser.of("/api/v1/test/cached");
                Request request = Request.builder()
                        .endpoint("/api/v1/test/cached")
                        .method(GET)
                        .token(VALID_AUTH_TOKEN)
                        .build();

                IHttpResponse<String> cachedResponse = HttpResponse.ok("cached-value").build();
                when(cachePort.get(anyString(), eq(USER_ID))).thenReturn(cachedResponse);

                // Act
                IHttpResponse<String> response = controller.route(endpoint, request);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.body()).isEqualTo("cached-value");
                verify(cachePort).get(eq("cached:test_cache"), eq(USER_ID));
                verify(cachePort, never()).set(anyString(), anyString(), any(), any());
            }
        }

        @Test
        @DisplayName("Should not cache response with error status code")
        void shouldNotCacheErrorResponse() throws Exception {
            try (var ignored = mockConstruction(RequestValidator.class,
                    (mock, context) -> doNothing().when(mock).validate(any(), any()))) {

                // Arrange
                EndpointParser endpoint = EndpointParser.of("/api/v1/test/cached-error");
                Request request = Request.builder()
                        .endpoint("/api/v1/test/cached-error")
                        .method(GET)
                        .token(VALID_AUTH_TOKEN)
                        .build();

                when(cachePort.get(anyString(), eq(USER_ID))).thenReturn(null);

                // Act
                IHttpResponse<String> response = controller.route(endpoint, request);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.statusCode()).isEqualTo(500);
                verify(cachePort).get(anyString(), eq(USER_ID));
                verify(cachePort, never()).set(anyString(), anyString(), any(), any());
            }
        }

        @Test
        @DisplayName("Should invalidate cache when invalidateCacheKey is defined")
        void shouldInvalidateCache() throws Exception {
            try (var ignored = mockConstruction(RequestValidator.class,
                    (mock, context) -> doNothing().when(mock).validate(any(), any()))) {

                // Arrange
                EndpointParser endpoint = EndpointParser.of("/api/v1/test/invalidate-cache");
                Request request = Request.builder()
                        .endpoint("/api/v1/test/invalidate-cache")
                        .method(POST)
                        .token(VALID_AUTH_TOKEN)
                        .build();

                // Act
                IHttpResponse<String> response = controller.route(endpoint, request);

                // Assert
                assertThat(response).isNotNull();
                verify(cachePort).clearSuffix("test_cache", USER_ID);
            }
        }

        @Test
        @DisplayName("Should invalidate multiple caches")
        void shouldInvalidateMultipleCaches() throws Exception {
            try (var ignored = mockConstruction(RequestValidator.class,
                    (mock, context) -> doNothing().when(mock).validate(any(), any()))) {

                // Arrange
                EndpointParser endpoint = EndpointParser.of("/api/v1/test/invalidate-multiple");
                Request request = Request.builder()
                        .endpoint("/api/v1/test/invalidate-multiple")
                        .method(POST)
                        .token(VALID_AUTH_TOKEN)
                        .build();

                // Act
                IHttpResponse<String> response = controller.route(endpoint, request);

                // Assert
                assertThat(response).isNotNull();
                verify(cachePort).clearSuffix("cache1", USER_ID);
                verify(cachePort).clearSuffix("cache2", USER_ID);
            }
        }

        @Test
        @DisplayName("Should not invalidate cache when token is null")
        void shouldNotInvalidateCacheWithoutToken() throws Exception {
            try (var ignored = mockConstruction(RequestValidator.class,
                    (mock, context) -> doNothing().when(mock).validate(any(), any()))) {

                // Arrange
                EndpointParser endpoint = EndpointParser.of("/api/v1/test/invalidate-cache");
                Request request = Request.builder()
                        .endpoint("/api/v1/test/invalidate-cache")
                        .method(POST)
                        .build();

                // Act
                IHttpResponse<String> response = controller.route(endpoint, request);

                // Assert
                assertThat(response).isNotNull();
                verify(cachePort, never()).clearSuffix(anyString(), anyString());
            }
        }


        @Test
        @DisplayName("Should set custom TTL for cached response")
        void shouldSetCustomTtlForCachedResponse() throws Exception {
            try (var ignored = mockConstruction(RequestValidator.class,
                    (mock, context) -> doNothing().when(mock).validate(any(), any()))) {
                // Arrange
                EndpointParser endpoint = EndpointParser.of("/api/v1/test/cache-ttl");
                Request request = Request.builder()
                        .endpoint("/api/v1/test/cache-ttl")
                        .method(POST)
                        .token(VALID_AUTH_TOKEN)
                        .build();

                when(cachePort.get(anyString(), eq(USER_ID))).thenReturn(null);
                // Act
                IHttpResponse<String> response = controller.route(endpoint, request);
                // Assert
                assertThat(response).isNotNull();
                assertThat(response.body()).isEqualTo("timed-cache");

                verify(cachePort).get(eq("cache-ttl:custom_ttl_cache"), eq(USER_ID));
                verify(cachePort).set(
                        eq("cache-ttl:custom_ttl_cache"),
                        eq(USER_ID),
                        any(IHttpResponse.class),
                        eq(Duration.ofMillis(100)));
            }
        }
    }

    @Nested
    @DisplayName("Async Execution Tests")
    class AsyncExecutionTests {

        @Test
        @DisplayName("Should execute async request and return SC_ACCEPTED")
        void shouldExecuteAsyncRequest() throws Exception {
            try (var ignored = mockConstruction(RequestValidator.class,
                    (mock, context) -> doNothing().when(mock).validate(any(), any()))) {

                // Arrange
                EndpointParser endpoint = EndpointParser.of("/api/v1/test/async");
                Request request = Request.builder()
                        .endpoint("/api/v1/test/async")
                        .method(POST)
                        .token(VALID_AUTH_TOKEN)
                        .build();

                // Act
                IHttpResponse<String> response = controller.route(endpoint, request);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.statusCode()).isEqualTo(202); // SC_ACCEPTED

                // Give the async thread time to execute
                Thread.sleep(200);

                verify(requestContextController, times(1)).activate();
                verify(requestContextController, times(1)).deactivate();
            }
        }
    }

    @Nested
    @DisplayName("Synchronous Execution Tests")
    class SynchronousExecutionTests {

        @Test
        @DisplayName("Should execute synchronous request directly")
        void shouldExecuteSyncRequest() throws Exception {
            try (var ignored = mockConstruction(RequestValidator.class,
                    (mock, context) -> doNothing().when(mock).validate(any(), any()))) {

                // Arrange
                EndpointParser endpoint = EndpointParser.of("/api/v1/test/simple");
                Request request = Request.builder()
                        .endpoint("/api/v1/test/simple")
                        .method(GET)
                        .token(VALID_AUTH_TOKEN)
                        .build();

                // Act
                IHttpResponse<String> response = controller.route(endpoint, request);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.statusCode()).isEqualTo(200);
                assertThat(response.body()).isEqualTo("simple");
                verify(requestContextController, never()).activate();
                verify(requestContextController, never()).deactivate();
            }
        }
    }
}