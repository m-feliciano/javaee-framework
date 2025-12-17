package com.dev.servlet.adapter.in.web.filter;

import com.dev.servlet.adapter.in.web.filter.wrapper.XSSRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("XSSFilter Tests")
class XSSFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private XSSFilter xssFilter;

    @BeforeEach
    void setUp() {
        xssFilter = new XSSFilter();
    }

    @Nested
    @DisplayName("Request Wrapping Tests")
    class RequestWrappingTests {

        @Test
        @DisplayName("Should wrap request with XSSRequestWrapper")
        void shouldWrapRequestWithXSSRequestWrapper() throws Exception {
            // Arrange
            ArgumentCaptor<XSSRequestWrapper> captor = ArgumentCaptor.forClass(XSSRequestWrapper.class);

            // Act
            xssFilter.doFilter(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(captor.capture(), eq(response));
            assertThat(captor.getValue()).isInstanceOf(XSSRequestWrapper.class);
        }

        @Test
        @DisplayName("Should pass wrapped request to filter chain")
        void shouldPassWrappedRequestToFilterChain() throws Exception {
            // Act
            xssFilter.doFilter(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(any(XSSRequestWrapper.class), eq(response));
            verify(filterChain, never()).doFilter(eq(request), any());
        }

        @Test
        @DisplayName("Should not modify response")
        void shouldNotModifyResponse() throws Exception {
            // Act
            xssFilter.doFilter(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(any(), eq(response));
        }
    }

    @Nested
    @DisplayName("Filter Chain Tests")
    class FilterChainTests {

        @Test
        @DisplayName("Should continue filter chain")
        void shouldContinueFilterChain() throws Exception {
            // Act
            xssFilter.doFilter(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(any(), any());
        }

        @Test
        @DisplayName("Should call filter chain exactly once")
        void shouldCallFilterChainOnce() throws Exception {
            // Act
            xssFilter.doFilter(request, response, filterChain);

            // Assert
            verify(filterChain, times(1)).doFilter(any(), any());
        }
    }

    @Nested
    @DisplayName("Filter Lifecycle Tests")
    class FilterLifecycleTests {

        @Test
        @DisplayName("Should initialize filter")
        void shouldInitializeFilter() throws Exception {
            // Act
            xssFilter.init(null);

            // Assert - No exception thrown
        }
    }
}

