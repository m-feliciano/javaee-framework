package com.dev.servlet.adapter.in.web.frontcontroller;

import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.Request;
import com.dev.servlet.application.port.in.user.UserDetailsPort;
import com.dev.servlet.application.port.out.security.AuthCookiePort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.enums.RequestMethod;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ResponseWriter Tests")
class ResponseWriterTest {

    @Mock
    private AuthCookiePort authCookiePort;

    @Mock
    private UserDetailsPort userDetailsPort;

    @Mock
    private AuthenticationPort authenticationPort;

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
    @DisplayName("JSON Response Tests")
    class JsonResponseTests {

        @Test
        @DisplayName("Should write JSON response correctly")
        void shouldWriteJsonResponse() throws Exception {
            // Arrange
            IHttpResponse<String> jsonResponse = HttpResponse.ofJson("{\"status\":\"ok\"}");

            when(response.getWriter()).thenReturn(printWriter);

            Request request = Request.builder()
                    .method(RequestMethod.GET.name())
                    .endpoint("/api/v1/health")
                    .build();

            // Act
            responseWriter.write(ResponseWriterTest.this.request, response, request, jsonResponse);

            // Assert
            verify(response).setContentType("application/json");
            verify(response).setCharacterEncoding("UTF-8");
            verify(response).setHeader("X-Correlation-ID", "test-correlation-id");

            String output = stringWriter.toString();
            assertThat(output).contains("status");
        }

        @Test
        @DisplayName("Should handle null body in JSON response")
        void shouldHandleNullBodyInJsonResponse() throws Exception {
            // Arrange
            IHttpResponse jsonResponse = HttpResponse.ofJson("{\"data\":null}");

            when(response.getWriter()).thenReturn(printWriter);

            Request request = Request.builder()
                    .method(RequestMethod.GET.name())
                    .endpoint("/api/v1/test")
                    .build();

            // Act
            responseWriter.write(ResponseWriterTest.this.request, response, request, jsonResponse);

            // Assert
            verify(response).setContentType("application/json");
        }
    }

    @Nested
    @DisplayName("Cookie Handling Tests")
    class CookieHandlingTests {

        @Test
        @DisplayName("Should set auth cookies for POST login with user token")
        void shouldSetAuthCookiesForLogin() throws Exception {
            // Arrange
            UserResponse userResponse = UserResponse.builder()
                    .id("user-123")
                    .token("auth-token")
                    .refreshToken("refresh-token")
                    .build();

            IHttpResponse<UserResponse> response = HttpResponse.ok(userResponse).build();

            Request request = Request.builder()
                    .method(RequestMethod.POST.name())
                    .endpoint("/api/v1/auth/login")
                    .build();

            when(ResponseWriterTest.this.response.getWriter()).thenReturn(printWriter);
            when(ResponseWriterTest.this.request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
            doNothing().when(authCookiePort).setAuthCookies(any(), anyString(), anyString());

            // Act
            responseWriter.write(ResponseWriterTest.this.request, ResponseWriterTest.this.response, request, response);

            // Assert
            verify(authCookiePort).setAuthCookies(ResponseWriterTest.this.response, "auth-token", "refresh-token");
            verify(authCookiePort).addCdnCookies(ResponseWriterTest.this.response);
        }

        @Test
        @DisplayName("Should clear cookies on logout")
        void shouldClearCookiesOnLogout() throws Exception {
            // Arrange
            IHttpResponse<Void> response = HttpResponse.<Void>ok(null).build();

            Request request = Request.builder()
                    .method(RequestMethod.GET.name())
                    .endpoint("/api/v1/auth/logout")
                    .build();

            when(ResponseWriterTest.this.response.getWriter()).thenReturn(printWriter);
            when(ResponseWriterTest.this.request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
            doNothing().when(authCookiePort).clearCookies(any());

            // Act
            responseWriter.write(ResponseWriterTest.this.request, ResponseWriterTest.this.response, request, response);

            // Assert
            verify(authCookiePort).clearCookies(ResponseWriterTest.this.response);
            verify(authCookiePort, never()).addCdnCookies(any());
        }

        @Test
        @DisplayName("Should add CDN cookies for normal requests")
        void shouldAddCdnCookies() throws Exception {
            // Arrange
            IHttpResponse<String> response = HttpResponse.ok("data").build();

            Request request = Request.builder()
                    .method(RequestMethod.GET.name())
                    .endpoint("/api/v1/product/list")
                    .build();

            when(ResponseWriterTest.this.response.getWriter()).thenReturn(printWriter);
            when(ResponseWriterTest.this.request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

            // Act
            responseWriter.write(ResponseWriterTest.this.request, ResponseWriterTest.this.response, request, response);

            // Assert
            verify(authCookiePort).addCdnCookies(ResponseWriterTest.this.response);
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
                    .method(RequestMethod.GET.name())
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
                    .method(RequestMethod.POST.name())
                    .endpoint("/api/v1/product/register")
                    .build();

            doNothing().when(ResponseWriterTest.this.response).sendRedirect(anyString());

            // Act
            responseWriter.write(ResponseWriterTest.this.request, ResponseWriterTest.this.response, request, response);

            // Assert
            verify(ResponseWriterTest.this.response).sendRedirect(contains("product/123"));
        }
    }

    @Nested
    @DisplayName("Request Attributes Tests")
    class RequestAttributesTests {

        @Test
        @DisplayName("Should set response as request attribute")
        void shouldSetResponseAttribute() throws Exception {
            // Arrange
            IHttpResponse<String> response = HttpResponse.ok("test-data").build();

            Request request = Request.builder()
                    .method(RequestMethod.GET.name())
                    .endpoint("/api/v1/test")
                    .build();

            when(ResponseWriterTest.this.response.getWriter()).thenReturn(printWriter);
            when(ResponseWriterTest.this.request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

            // Act
            responseWriter.write(ResponseWriterTest.this.request, ResponseWriterTest.this.response, request, response);

            // Assert
            verify(ResponseWriterTest.this.request).setAttribute(eq("response"), eq(response));
        }

        @Test
        @DisplayName("Should handle query parameters")
        void shouldHandleQueryParameters() throws Exception {
            // Arrange
            when(request.getQueryString()).thenReturn("q=search&k=value");

            IHttpResponse<String> response = HttpResponse.ok("data").build();

            Request request = Request.builder()
                    .method(RequestMethod.GET.name())
                    .endpoint("/api/v1/search")
                    .build();

            when(ResponseWriterTest.this.response.getWriter()).thenReturn(printWriter);
            when(ResponseWriterTest.this.request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

            // Act
            responseWriter.write(ResponseWriterTest.this.request, ResponseWriterTest.this.response, request, response);

            // Assert
            verify(ResponseWriterTest.this.request).setAttribute(eq("response"), any());
        }

        @Test
        @DisplayName("Should set correlation ID header")
        void shouldSetCorrelationIdHeader() throws Exception {
            // Arrange
            MDC.put("correlationId", "custom-correlation-id");

            IHttpResponse<String> response = HttpResponse.ok("data").build();

            Request request = Request.builder()
                    .method(RequestMethod.GET.name())
                    .endpoint("/api/v1/test")
                    .build();

            when(ResponseWriterTest.this.response.getWriter()).thenReturn(printWriter);
            when(ResponseWriterTest.this.request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

            // Act
            responseWriter.write(ResponseWriterTest.this.request, ResponseWriterTest.this.response, request, response);

            // Assert
            verify(ResponseWriterTest.this.response).setHeader("X-Correlation-ID", "custom-correlation-id");
        }
    }
}

