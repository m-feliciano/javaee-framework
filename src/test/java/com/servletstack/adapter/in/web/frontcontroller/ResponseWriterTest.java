package com.servletstack.adapter.in.web.frontcontroller;

import com.servletstack.adapter.in.web.dto.HttpResponse;
import com.servletstack.adapter.in.web.dto.IHttpResponse;
import com.servletstack.adapter.in.web.dto.Request;
import com.servletstack.domain.entity.enums.RequestMethod;
import jakarta.servlet.RequestDispatcher;
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

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ResponseWriter Tests")
@SuppressWarnings("all")
class ResponseWriterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher requestDispatcher;

    @InjectMocks
    private ResponseWriter responseWriter;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);

        MDC.put("correlationId", "test-correlation-id");

        lenient().when(request.getContextPath()).thenReturn("/app");
        lenient().when(request.getQueryString()).thenReturn(null);
        lenient().when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
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

            when(request.getHeader("Accept")).thenReturn("text/html");
            // Act
            responseWriter.write(request, response,
                    Request.builder()
                            .method(RequestMethod.POST)
                            .endpoint("/api/v1/resource") .build(),
                    errorResponse);

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

            when(request.getHeader("Accept")).thenReturn("text/html");
            // Act
            responseWriter.write(request, response,
                    Request.builder()
                            .method(RequestMethod.DELETE)
                            .endpoint("/api/v1/resource/123") .build(),
                    mockResponse);

            // Assert
            verify(response).setStatus(204);
        }
    }

    @Test
    @DisplayName("Should set correlation ID header")
    void shouldSetCorrelationIdHeader() throws Exception {
        // Arrange
        MDC.put("correlationId", "custom-correlation-id");

        IHttpResponse response = HttpResponse.ok("data").build();

        Request request = Request.builder()
                .method(RequestMethod.GET)
                .endpoint("/api/v1/test")
                .build();

        when(ResponseWriterTest.this.request.getHeader("Accept")).thenReturn("text/html");
        // Act
        responseWriter.write(ResponseWriterTest.this.request, ResponseWriterTest.this.response, request, response);

        // Assert
        verify(ResponseWriterTest.this.response).setHeader("X-Correlation-ID", "custom-correlation-id");
    }

    @Nested
    @DisplayName("JSON Response Tests")
    class JsonResponseTests {

        @Test
        @DisplayName("Should write JSON response correctly")
        void shouldWriteJsonResponse() throws Exception {
            // Arrange
            IHttpResponse jsonResponse = HttpResponse.ok("{\"status\":\"ok\"}").build();

            when(request.getHeader("Accept")).thenReturn("application/json");
            when(response.getWriter()).thenReturn(printWriter);

            Request request = Request.builder()
                    .method(RequestMethod.GET)
                    .endpoint("/api/v1/health")
                    .build();

            // Act
            responseWriter.write(ResponseWriterTest.this.request, response, request, jsonResponse);

            // Assert
            verify(response).setContentType("application/json");
            verify(response).setCharacterEncoding("UTF-8");

            String output = stringWriter.toString();
            assertThat(output).contains("status");
        }

        @Test
        @DisplayName("Should handle null body in JSON response")
        void shouldHandleNullBodyInJsonResponse() throws Exception {
            // Arrange
            IHttpResponse<String> jsonResponse = HttpResponse.ok("{\"data\":null}").build();

            when(request.getHeader("Accept")).thenReturn("application/json");
            when(response.getWriter()).thenReturn(printWriter);

            Request request = Request.builder()
                    .method(RequestMethod.GET)
                    .endpoint("/api/v1/test")
                    .build();

            // Act
            responseWriter.write(ResponseWriterTest.this.request, response, request, jsonResponse);

            // Assert
            verify(response).setContentType("application/json");
        }
    }


    @Nested
    @DisplayName("Navigation Handling Tests")
    class NavigationHandlingTests {

        @Test
        @DisplayName("Should forward to JSP page")
        void shouldForwardToJsp() throws Exception {
            // Arrange
            IHttpResponse<String> response = HttpResponse.ok("data")
                    .next("forward:pages/product/list.jsp")
                    .build();

            Request request = Request.builder()
                    .method(RequestMethod.GET)
                    .endpoint("/api/v1/product/list")
                    .build();

            when(ResponseWriterTest.this.request.getRequestDispatcher(contains("list.jsp"))).thenReturn(requestDispatcher);
            doNothing().when(requestDispatcher).forward(any(), any());

            // Act
            responseWriter.write(ResponseWriterTest.this.request, ResponseWriterTest.this.response, request, response);

            // Assert
            verify(ResponseWriterTest.this.request).getRequestDispatcher(contains("list.jsp"));
            verify(requestDispatcher).forward(ResponseWriterTest.this.request, ResponseWriterTest.this.response);
        }

        @Test
        @DisplayName("Should redirect to URL")
        void shouldRedirectToUrl() throws Exception {
            // Arrange
            IHttpResponse<String> response = HttpResponse.ok("data")
                    .next("redirect:/product/123")
                    .build();

            Request request = Request.builder()
                    .method(RequestMethod.POST)
                    .endpoint("/api/v1/product/register")
                    .build();

            doNothing().when(ResponseWriterTest.this.response).sendRedirect(anyString());

            // Act
            responseWriter.write(ResponseWriterTest.this.request, ResponseWriterTest.this.response, request, response);

            // Assert
            verify(ResponseWriterTest.this.response).sendRedirect(contains("product/123"));
        }
    }
}

