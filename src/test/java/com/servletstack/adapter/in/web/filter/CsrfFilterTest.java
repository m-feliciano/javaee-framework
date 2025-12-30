package com.servletstack.adapter.in.web.filter;

import com.servletstack.application.port.out.security.AuthCookiePort;
import com.servletstack.domain.entity.enums.RequestMethod;
import jakarta.servlet.FilterChain;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CsrfFilter Tests")
class CsrfFilterTest {

    @Mock
    private AuthCookiePort authCookiePort;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private CsrfFilter csrfFilter;

    @BeforeEach
    void setUp() {
        lenient().when(request.getRequestURI()).thenReturn("/api/v1/product/list");
        lenient().when(request.getMethod()).thenReturn("POST");
    }

    @Nested
    @DisplayName("Whitelisted Endpoints Tests")
    class WhitelistedEndpointsTests {

        @Test
        @DisplayName("Should skip CSRF validation for health check endpoint")
        void shouldSkipCsrfForHealthCheck() throws Exception {
            // Arrange
            when(request.getRequestURI()).thenReturn("/api/v1/health/check");

            // Act
            csrfFilter.doFilter(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
            verify(authCookiePort, never()).validateCsrfToken(any());
        }

        @Test
        @DisplayName("Should skip CSRF validation for user confirmation")
        void shouldSkipCsrfForUserConfirmation() throws Exception {
            // Arrange
            when(request.getRequestURI()).thenReturn("/api/v1/user/confirm");

            // Act
            csrfFilter.doFilter(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
            verify(authCookiePort, never()).validateCsrfToken(any());
        }

        @Test
        @DisplayName("Should skip CSRF validation for alert clear")
        void shouldSkipCsrfForAlertClear() throws Exception {
            // Arrange
            when(request.getRequestURI()).thenReturn("/api/v2/alert/clear");

            // Act
            csrfFilter.doFilter(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
            verify(authCookiePort, never()).validateCsrfToken(any());
        }
    }

    @Nested
    @DisplayName("GET Request Tests")
    class GetRequestTests {

        @Test
        @DisplayName("Should ensure CSRF token for GET requests")
        void shouldEnsureCsrfTokenForGet() throws Exception {
            // Arrange
            when(request.getMethod()).thenReturn(RequestMethod.GET.getMethod());

            // Act
            csrfFilter.doFilter(request, response, filterChain);

            // Assert
            verify(authCookiePort).ensureCsrfToken(request, response);
            verify(filterChain).doFilter(request, response);
            verify(authCookiePort, never()).validateCsrfToken(any());
        }

        @Test
        @DisplayName("Should not validate CSRF for GET requests")
        void shouldNotValidateCsrfForGet() throws Exception {
            // Arrange
            when(request.getMethod()).thenReturn("GET");

            // Act
            csrfFilter.doFilter(request, response, filterChain);

            // Assert
            verify(authCookiePort, never()).validateCsrfToken(any());
        }
    }

    @Nested
    @DisplayName("POST Request Tests")
    class PostRequestTests {

        @Test
        @DisplayName("Should validate CSRF token for POST request")
        void shouldValidateCsrfForPost() throws Exception {
            // Arrange
            when(request.getMethod()).thenReturn("POST");
            when(authCookiePort.validateCsrfToken(request)).thenReturn(true);

            // Act
            csrfFilter.doFilter(request, response, filterChain);

            // Assert
            verify(authCookiePort).validateCsrfToken(request);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should reject POST request with invalid CSRF token")
        void shouldRejectPostWithInvalidToken() throws Exception {
            // Arrange
            when(request.getMethod()).thenReturn("POST");
            when(authCookiePort.validateCsrfToken(request)).thenReturn(false);

            // Act
            csrfFilter.doFilter(request, response, filterChain);

            // Assert
            verify(authCookiePort).validateCsrfToken(request);
            verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            verify(filterChain, never()).doFilter(any(), any());
        }
    }

    @Nested
    @DisplayName("PUT Request Tests")
    class PutRequestTests {

        @Test
        @DisplayName("Should validate CSRF token for PUT request")
        void shouldValidateCsrfForPut() throws Exception {
            // Arrange
            when(request.getMethod()).thenReturn("PUT");
            when(authCookiePort.validateCsrfToken(request)).thenReturn(true);

            // Act
            csrfFilter.doFilter(request, response, filterChain);

            // Assert
            verify(authCookiePort).validateCsrfToken(request);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should reject PUT request with invalid CSRF token")
        void shouldRejectPutWithInvalidToken() throws Exception {
            // Arrange
            when(request.getMethod()).thenReturn("PUT");
            when(authCookiePort.validateCsrfToken(request)).thenReturn(false);

            // Act
            csrfFilter.doFilter(request, response, filterChain);

            // Assert
            verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            verify(filterChain, never()).doFilter(any(), any());
        }
    }

    @Nested
    @DisplayName("DELETE Request Tests")
    class DeleteRequestTests {

        @Test
        @DisplayName("Should validate CSRF token for DELETE request")
        void shouldValidateCsrfForDelete() throws Exception {
            // Arrange
            when(request.getMethod()).thenReturn("DELETE");
            when(authCookiePort.validateCsrfToken(request)).thenReturn(true);

            // Act
            csrfFilter.doFilter(request, response, filterChain);

            // Assert
            verify(authCookiePort).validateCsrfToken(request);
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("PATCH Request Tests")
    class PatchRequestTests {

        @Test
        @DisplayName("Should validate CSRF token for PATCH request")
        void shouldValidateCsrfForPatch() throws Exception {
            // Arrange
            when(request.getMethod()).thenReturn("PATCH");
            when(authCookiePort.validateCsrfToken(request)).thenReturn(true);

            // Act
            csrfFilter.doFilter(request, response, filterChain);

            // Assert
            verify(authCookiePort).validateCsrfToken(request);
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("Filter Lifecycle Tests")
    class FilterLifecycleTests {

        @Test
        @DisplayName("Should initialize filter")
        void shouldInitializeFilter() throws Exception {
            // Act
            csrfFilter.init(null);

            // Assert - No exception thrown
        }

        @Test
        @DisplayName("Should destroy filter")
        void shouldDestroyFilter() {
            // Act
            csrfFilter.destroy();

            // Assert - No exception thrown
        }
    }
}

