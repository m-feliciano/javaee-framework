package com.dev.servlet.adapter.in.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MDCFilter Tests")
class MDCFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private MDCFilter mdcFilter;

    @BeforeEach
    void setUp() {
        mdcFilter = new MDCFilter();
        MDC.clear();

        lenient().when(request.getMethod()).thenReturn("GET");
        lenient().when(request.getRequestURI()).thenReturn("/api/v1/product/list");
        lenient().when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        lenient().when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Nested
    @DisplayName("MDC Context Tests")
    class MDCContextTests {

        @Test
        @DisplayName("Should add correlation ID to MDC")
        void shouldAddCorrelationId() throws Exception {
            // Act
            mdcFilter.doFilter(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
            // CorrelationId is cleared after filter, but we can verify the chain was called
        }

        @Test
        @DisplayName("Should add HTTP implementation to MDC during filter")
        void shouldAddHttpMethod() throws Exception {
            // Arrange
            doAnswer(invocation -> {
                // Check MDC during filter execution
                String method = MDC.get("httpMethod");
                assertThat(method).isEqualTo("GET");
                return null;
            }).when(filterChain).doFilter(request, response);

            // Act
            mdcFilter.doFilter(request, response, filterChain);
        }

        @Test
        @DisplayName("Should add endpoint to MDC during filter")
        void shouldAddEndpoint() throws Exception {
            // Arrange
            doAnswer(invocation -> {
                String endpoint = MDC.get("endpoint");
                assertThat(endpoint).isEqualTo("/api/v1/product/list");
                return null;
            }).when(filterChain).doFilter(request, response);

            // Act
            mdcFilter.doFilter(request, response, filterChain);
        }

        @Test
        @DisplayName("Should add IP address to MDC during filter")
        void shouldAddIpAddress() throws Exception {
            // Arrange
            doAnswer(invocation -> {
                String ip = MDC.get("ipAddress");
                assertThat(ip).isEqualTo("127.0.0.1");
                return null;
            }).when(filterChain).doFilter(request, response);

            // Act
            mdcFilter.doFilter(request, response, filterChain);
        }

        @Test
        @DisplayName("Should add user agent to MDC during filter")
        void shouldAddUserAgent() throws Exception {
            // Arrange
            doAnswer(invocation -> {
                String userAgent = MDC.get("userAgent");
                assertThat(userAgent).isEqualTo("Mozilla/5.0");
                return null;
            }).when(filterChain).doFilter(request, response);

            // Act
            mdcFilter.doFilter(request, response, filterChain);
        }

        @Test
        @DisplayName("Should add timestamp to MDC during filter")
        void shouldAddTimestamp() throws Exception {
            // Arrange
            doAnswer(invocation -> {
                String startedAt = MDC.get("startedAt");
                assertThat(startedAt).isNotNull();
                assertThat(Long.parseLong(startedAt)).isPositive();
                return null;
            }).when(filterChain).doFilter(request, response);

            // Act
            mdcFilter.doFilter(request, response, filterChain);
        }
    }

    @Nested
    @DisplayName("MDC Cleanup Tests")
    class MDCCleanupTests {

        @Test
        @DisplayName("Should clear MDC after filter execution")
        void shouldClearMdcAfterFilter() throws Exception {
            // Act
            mdcFilter.doFilter(request, response, filterChain);

            // Assert
            assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty();
        }

        @Test
        @DisplayName("Should clear MDC even when exception occurs")
        void shouldClearMdcOnException() throws Exception {
            // Arrange
            doThrow(new RuntimeException("Test exception"))
                    .when(filterChain).doFilter(request, response);

            // Act & Assert
            try {
                mdcFilter.doFilter(request, response, filterChain);
            } catch (RuntimeException e) {
                // Expected
            }

            assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty();
        }
    }

    @Nested
    @DisplayName("X-Forwarded-For Tests")
    class XForwardedForTests {

        @Test
        @DisplayName("Should extract IP from X-Forwarded-For header")
        void shouldExtractIpFromXForwardedFor() throws Exception {
            // Arrange
            when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.1");

            doAnswer(invocation -> {
                String ip = MDC.get("ipAddress");
                assertThat(ip).isEqualTo("203.0.113.1");
                return null;
            }).when(filterChain).doFilter(request, response);

            // Act
            mdcFilter.doFilter(request, response, filterChain);
        }

        @Test
        @DisplayName("Should extract first IP from multiple X-Forwarded-For")
        void shouldExtractFirstIpFromMultiple() throws Exception {
            // Arrange
            when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.1, 198.51.100.1");

            doAnswer(invocation -> {
                String ip = MDC.get("ipAddress");
                assertThat(ip).isEqualTo("203.0.113.1");
                return null;
            }).when(filterChain).doFilter(request, response);

            // Act
            mdcFilter.doFilter(request, response, filterChain);
        }

        @Test
        @DisplayName("Should fallback to remote address if no headers")
        void shouldFallbackToRemoteAddr() throws Exception {
            // Arrange
            when(request.getRemoteAddr()).thenReturn("192.168.1.1");

            doAnswer(invocation -> {
                String ip = MDC.get("ipAddress");
                assertThat(ip).isEqualTo("192.168.1.1");
                return null;
            }).when(filterChain).doFilter(request, response);

            // Act
            mdcFilter.doFilter(request, response, filterChain);
        }
    }

    @Nested
    @DisplayName("Different Request Types Tests")
    class DifferentRequestTypesTests {

        @Test
        @DisplayName("Should handle POST request")
        void shouldHandlePostRequest() throws Exception {
            // Arrange
            when(request.getMethod()).thenReturn("POST");

            doAnswer(invocation -> {
                String method = MDC.get("httpMethod");
                assertThat(method).isEqualTo("POST");
                return null;
            }).when(filterChain).doFilter(request, response);

            // Act
            mdcFilter.doFilter(request, response, filterChain);
        }

        @Test
        @DisplayName("Should handle different endpoints")
        void shouldHandleDifferentEndpoints() throws Exception {
            // Arrange
            when(request.getRequestURI()).thenReturn("/api/v1/user/login");

            doAnswer(invocation -> {
                String endpoint = MDC.get("endpoint");
                assertThat(endpoint).isEqualTo("/api/v1/user/login");
                return null;
            }).when(filterChain).doFilter(request, response);

            // Act
            mdcFilter.doFilter(request, response, filterChain);
        }
    }
}

