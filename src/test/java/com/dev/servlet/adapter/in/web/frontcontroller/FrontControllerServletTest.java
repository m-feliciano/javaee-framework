package com.dev.servlet.adapter.in.web.frontcontroller;

import com.dev.servlet.adapter.in.web.dispatcher.IServletDispatcher;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.Request;
import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.out.audit.AuditPort;
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
            verify(response).setStatus(200);
            verify(responseWriter).write(any(), any(), any(), eq(mock));
            verify(auditPort).success(any(), any(), any());
        }

        @Test
        @DisplayName("Should handle JSON response")
        void shouldHandleJsonResponse() throws Exception {
            // Arrange
            IHttpResponse mockResponse = HttpResponse.ofJson("{\"status\":\"ok\"}");

            when(dispatcher.dispatch(any(Request.class))).thenReturn(mockResponse);

            // Act
            frontControllerServlet.service(request, response);

            // Assert
            verify(response).setStatus(200);
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
            verify(response).setStatus(404);
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
    @DisplayName("Response Status Handling")
    class ResponseStatusTests {

        @Test
        @DisplayName("Should set correct status code for successful response")
        void shouldSetSuccessStatusCode() throws Exception {
            // Arrange
            IHttpResponse errorResponse = HttpResponse.<String>newBuilder()
                    .statusCode(201)
                    .error("Created")
                    .build();

            when(dispatcher.dispatch(any(Request.class))).thenReturn(errorResponse);

            // Act
            frontControllerServlet.service(request, response);

            // Assert
            verify(response).setStatus(201);
        }

        @Test
        @DisplayName("Should handle 204 No Content response")
        void shouldHandle204Response() throws Exception {
            // Arrange
            IHttpResponse mockResponse = HttpResponse.<Void>newBuilder()
                    .statusCode(204)
                    .build();

            when(dispatcher.dispatch(any(Request.class))).thenReturn(mockResponse);

            // Act
            frontControllerServlet.service(request, response);

            // Assert
            verify(response).setStatus(204);
        }
    }
}

