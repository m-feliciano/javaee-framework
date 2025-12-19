package com.dev.servlet.adapter.in.web.frontcontroller;

import com.dev.servlet.adapter.in.web.dispatcher.IServletDispatcher;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.Request;
import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.out.audit.AuditPort;
import com.dev.servlet.application.port.out.security.AuthCookiePort;
import com.dev.servlet.domain.entity.enums.RequestMethod;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.MDC;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("FrontControllerServlet Tests")
class FrontControllerServletTest {

    @Mock
    private AuditPort auditPort;

    @Mock
    private IServletDispatcher dispatcher;

    @Mock
    private ResponseWriter responseWriter;

    @Mock
    private ErrorResponseWriter errorWriter;

    @Mock
    private AuthCookiePort authCookiePort;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private FrontControllerServlet frontControllerServlet;

    @BeforeEach
    void setUp() {
        // Setup default mocks
        lenient().when(request.getMethod()).thenReturn("GET");
        lenient().when(request.getRequestURI()).thenReturn("/api/v1/product/list");
        lenient().when(request.getContextPath()).thenReturn("");
        lenient().when(request.getQueryString()).thenReturn(null);
        lenient().when(request.getHeader(anyString())).thenReturn(null);
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle AppException")
        void shouldHandleAppException() throws Exception {
            // Arrange
            when(dispatcher.dispatch(any(Request.class)))
                    .thenThrow(new AppException(400, "Bad Request"));

            doNothing().when(errorWriter).write(any(), any(), anyInt(), anyString());

            // Act
            frontControllerServlet.service(request, response);

            // Assert
            verify(errorWriter).write(eq(request), eq(response), eq(400), eq("Bad Request"));
            verify(auditPort).failure(any(), any(), any());
        }

        @Test
        @DisplayName("Should handle unexpected exception")
        void shouldHandleUnexpectedException() throws Exception {
            // Arrange
            when(dispatcher.dispatch(any(Request.class)))
                    .thenThrow(new RuntimeException("Unexpected error"));

            doNothing().when(errorWriter).write(any(), any(), anyInt(), anyString());

            // Act
            frontControllerServlet.service(request, response);

            // Assert
            verify(errorWriter).write(eq(request), eq(response), eq(500), eq("Unexpected error"));
            verify(auditPort).failure(any(), any(), any());
        }

        @Test
        @DisplayName("Should handle error response from dispatcher")
        void shouldHandleErrorResponse() throws Exception {
            // Arrange

            when(dispatcher.dispatch(any(Request.class)))
                    .thenReturn(HttpResponse.error(404, "Not Found"));

            // Act
            frontControllerServlet.service(request, response);

            // Assert
            verify(errorWriter).write(any(), any(), eq(404), eq("Not Found"));
        }
    }

    @Nested
    @DisplayName("Audit Logging")
    class AuditLoggingTests {

        @Test
        @DisplayName("Should log successful audit event for normal endpoints")
        void shouldLogSuccessfulAuditEvent() throws Exception {
            // Arrange
            when(request.getRequestURI()).thenReturn("/api/v1/product/register");
            when(request.getMethod()).thenReturn("POST");

            IHttpResponse mockResponse = HttpResponse.<String>newBuilder()
                    .statusCode(201)
                    .body("Created")
                    .build();

            when(dispatcher.dispatch(any(Request.class))).thenReturn(mockResponse);

            // Act
            frontControllerServlet.service(request, response);

            // Assert
            verify(auditPort).success(contains("POST:/api/v1/product/register"), any(), any());
        }

        @Test
        @DisplayName("Should log failure audit event for error responses")
        void shouldLogFailureAuditEvent() throws Exception {
            // Arrange
            when(request.getRequestURI()).thenReturn("/api/v1/product/delete");

            IHttpResponse errorResponse = HttpResponse.<String>newBuilder()
                    .statusCode(403)
                    .error("Forbidden")
                    .build();

            when(dispatcher.dispatch(any(Request.class))).thenReturn(errorResponse);

            // Act
            frontControllerServlet.service(request, response);

            // Assert
            verify(auditPort).failure(any(), any(), any());
        }

        @Test
        @DisplayName("Should not audit activity endpoints")
        void shouldNotAuditActivityEndpoints() throws Exception {
            // Arrange
            when(request.getRequestURI()).thenReturn("/api/v1/activity/history");

            IHttpResponse mockResponse = HttpResponse.<String>ok("[]").build();
            when(dispatcher.dispatch(any(Request.class))).thenReturn(mockResponse);

            // Act
            frontControllerServlet.service(request, response);

            // Assert
            verify(auditPort, never()).success(any(), any(), any());
        }

        @Test
        @DisplayName("Should not audit alert endpoints")
        void shouldNotAuditAlertEndpoints() throws Exception {
            // Arrange
            when(request.getRequestURI()).thenReturn("/api/v1/alert/list");

            IHttpResponse mockResponse = HttpResponse.<String>ok("[]").build();
            when(dispatcher.dispatch(any(Request.class))).thenReturn(mockResponse);

            // Act
            frontControllerServlet.service(request, response);

            // Assert
            verify(auditPort, never()).success(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("Successful Request Processing")
    class SuccessfulRequestTests {

        @Test
        @DisplayName("Should process successful request")
        void shouldProcessSuccessfulRequest() throws Exception {
            // Arrange

            IHttpResponse mock = HttpResponse.<String>ok("Success").build();
            when(dispatcher.dispatch(any())).thenReturn(mock);

            doNothing().when(responseWriter).write(any(), any(), any(), any());

            // Act
            frontControllerServlet.service(request, response);

            // Assert
            verify(responseWriter).write(any(), any(), any(), eq(mock));
            verify(auditPort).success(any(), any(), any());
        }

        @Test
        @DisplayName("Should handle JSON response")
        void shouldHandleJsonResponse() throws Exception {
            // Arrange
            IHttpResponse mockResponse = HttpResponse.ok("{\"status\":\"ok\"}").build();

            when(dispatcher.dispatch(any(Request.class))).thenReturn(mockResponse);

            // Act
            frontControllerServlet.service(request, response);

            // Assert
            verify(responseWriter).write(any(), any(), any(), eq(mockResponse));
        }

        @Test
        @DisplayName("Should not audit health check endpoints")
        void shouldNotAuditHealthEndpoints() throws Exception {
            // Arrange
            when(request.getRequestURI()).thenReturn("/api/v1/health/status");

            HttpResponse response = HttpResponse.<String>ok("UP").build();
            when(dispatcher.dispatch(any(Request.class)))
                    .thenReturn(response);

            // Act
            frontControllerServlet.service(request, FrontControllerServletTest.this.response);

            // Assert
            verify(auditPort, never()).success(any(), any(), any());
            verify(auditPort, never()).failure(any(), any(), any());
        }

        @Test
        @DisplayName("Should not audit inspect endpoints")
        void shouldNotAuditInspectEndpoints() throws Exception {
            // Arrange
            when(request.getRequestURI()).thenReturn("/api/v1/inspect/controllers");

            IHttpResponse mockResponse = HttpResponse.<String>ok("[]").build();
            when(dispatcher.dispatch(any(Request.class))).thenReturn(mockResponse);

            // Act
            frontControllerServlet.service(request, response);

            // Assert
            verify(auditPort, never()).success(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("Cookie Handling Tests")
    class CookieHandlingTests {

        @Test
        @DisplayName("Should set auth cookies when POST returns UserResponse with tokens")
        void shouldSetAuthCookiesForLogin() throws Exception {
            // Arrange
            com.dev.servlet.application.transfer.response.UserResponse userResponse =
                    com.dev.servlet.application.transfer.response.UserResponse.builder()
                            .id("user-123")
                            .token("auth-token")
                            .refreshToken("refresh-token")
                            .build();

            IHttpResponse userHttpResponse = HttpResponse.ok(userResponse).build();

            when(request.getMethod()).thenReturn("POST");
            when(request.getRequestURI()).thenReturn("/api/v1/auth/login");
            when(dispatcher.dispatch(any())).thenReturn(userHttpResponse, userHttpResponse);
            doNothing().when(authCookiePort).setAuthCookies(any(), anyString(), anyString());
            doNothing().when(responseWriter).write(any(), any(), any(), any());

            // Act
            frontControllerServlet.service(request, response);

            // Assert
            verify(authCookiePort).setAuthCookies(response, "auth-token", "refresh-token");
            verify(authCookiePort, never()).clearCookies(any());
        }

        @Test
        @DisplayName("Should clear cookies on logout request")
        void shouldClearCookiesOnLogout() throws Exception {
            // Arrange
            IHttpResponse mockResponse = HttpResponse.<String>ok("Logged out").build();

            when(request.getRequestURI()).thenReturn("/api/v1/auth/logout");
            when(dispatcher.dispatch(any())).thenReturn(mockResponse);
            doNothing().when(authCookiePort).clearCookies(any());
            doNothing().when(responseWriter).write(any(), any(), any(), any());

            // Act
            frontControllerServlet.service(request, response);

            // Assert
            verify(authCookiePort).clearCookies(response);
            verify(authCookiePort, never()).setAuthCookies(any(), anyString(), anyString());
        }

        @Test
        @DisplayName("Should add CDN cookies for normal requests")
        void shouldAddCdnCookiesForNormalRequests() throws Exception {
            // Arrange
            IHttpResponse mockResponse = HttpResponse.<String>ok("Success").build();

            when(request.getRequestURI()).thenReturn("/api/v1/product/list");
            when(dispatcher.dispatch(any())).thenReturn(mockResponse);
            doNothing().when(authCookiePort).addCdnCookies(any());
            doNothing().when(responseWriter).write(any(), any(), any(), any());

            // Act
            frontControllerServlet.service(request, response);

            // Assert
            verify(authCookiePort).addCdnCookies(response);
        }

        @Test
        @DisplayName("Should not set auth cookies for GET requests even with UserResponse")
        void shouldNotSetAuthCookiesForGetRequests() throws Exception {
            // Arrange
            com.dev.servlet.application.transfer.response.UserResponse userResponse =
                    com.dev.servlet.application.transfer.response.UserResponse.builder()
                            .id("user-123")
                            .token("auth-token")
                            .refreshToken("refresh-token")
                            .build();

            IHttpResponse userHttpResponse = HttpResponse.ok(userResponse).build();

            when(request.getMethod()).thenReturn("GET");
            when(request.getRequestURI()).thenReturn("/api/v1/user/me");
            when(dispatcher.dispatch(any())).thenReturn(userHttpResponse);
            doNothing().when(authCookiePort).addCdnCookies(any());
            doNothing().when(responseWriter).write(any(), any(), any(), any());

            // Act
            frontControllerServlet.service(request, response);

            // Assert
            verify(authCookiePort, never()).setAuthCookies(any(), anyString(), anyString());
            verify(authCookiePort).addCdnCookies(response);
        }

        @Test
        @DisplayName("Should not set auth cookies when UserResponse has no token")
        void shouldNotSetAuthCookiesWhenNoToken() throws Exception {
            // Arrange
            com.dev.servlet.application.transfer.response.UserResponse userResponse =
                    com.dev.servlet.application.transfer.response.UserResponse.builder()
                            .id("user-123")
                            .build();

            IHttpResponse userHttpResponse = HttpResponse.ok(userResponse).build();

            when(request.getMethod()).thenReturn("POST");
            when(request.getRequestURI()).thenReturn("/api/v1/user/update");
            when(dispatcher.dispatch(any())).thenReturn(userHttpResponse);
            doNothing().when(authCookiePort).addCdnCookies(any());
            doNothing().when(responseWriter).write(any(), any(), any(), any());

            // Act
            frontControllerServlet.service(request, response);

            // Assert
            verify(authCookiePort, never()).setAuthCookies(any(), anyString(), anyString());
            verify(authCookiePort).addCdnCookies(response);
        }
    }

    @Nested
    @DisplayName("Request Attributes Tests")
    class RequestAttributesTests {

        @Test
        @DisplayName("Should set response as request attribute")
        void shouldSetResponseAttribute() throws Exception {
            // Arrange
            IHttpResponse response = HttpResponse.ok("test-data").build();

            Request request = Request.builder()
                    .method(RequestMethod.GET)
                    .endpoint("/api/v1/test")
                    .build();

            when(FrontControllerServletTest.this.dispatcher.dispatch(any(Request.class))).thenReturn(response);
            doNothing().when(FrontControllerServletTest.this.responseWriter).write(any(), any(), any(), any());

            // Act
            frontControllerServlet.service(FrontControllerServletTest.this.request, FrontControllerServletTest.this.response);
            // Assert
            verify(FrontControllerServletTest.this.request).setAttribute("response", response);
        }

        @Test
        @DisplayName("Should handle query parameters")
        void shouldHandleQueryParameters() throws Exception {
            // Arrange
            when(request.getQueryString()).thenReturn("q=search&k=value");

            IHttpResponse response = HttpResponse.ok("data").build();

            Request request = Request.builder()
                    .method(RequestMethod.GET)
                    .endpoint("/api/v1/search")
                    .build();

            when(FrontControllerServletTest.this.dispatcher.dispatch(any(Request.class))).thenReturn(response);
            doNothing().when(FrontControllerServletTest.this.responseWriter).write(any(), any(), any(), any());

            // Act
            frontControllerServlet.service(FrontControllerServletTest.this.request, FrontControllerServletTest.this.response);
            // Assert
            verify(FrontControllerServletTest.this.request).setAttribute("response", response);

        }
    }
}

