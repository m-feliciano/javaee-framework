package com.dev.servlet.infrastructure.security;

import com.dev.servlet.adapter.IServletDispatcher;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.util.EndpointParser;
import com.dev.servlet.core.util.JwtUtil;
import com.dev.servlet.core.util.PropertiesUtil;
import com.dev.servlet.domain.service.AuthService;
import com.dev.servlet.domain.transfer.response.RefreshTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthFilterTest {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;
    private HttpSession session;
    private IServletDispatcher dispatcher;

    private AuthFilter authFilter;
    private JwtUtil jwtUtil;
    private AuthService loginService;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        session = mock(HttpSession.class);
        dispatcher = mock(IServletDispatcher.class);
        jwtUtil = mock(JwtUtil.class);
        loginService = mock(AuthService.class);

        authFilter = new AuthFilter();
        authFilter.setDispatcher(dispatcher);
        authFilter.setJwtUtil(jwtUtil);
        authFilter.setLoginService(loginService);

        when(request.getSession()).thenReturn(session);
        when(request.getServletPath()).thenReturn("/product/list");
    }

    @Test
    void doFilter_WithValidToken_ShouldDispatchRequest() throws IOException {
        // Arrange
        String validToken = "valid-bearerToken";
        when(session.getAttribute("token")).thenReturn(validToken);
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        // Act
        authFilter.doFilter(request, response, chain);
        // Assert
        verify(dispatcher, times(1)).dispatch(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    void doFilter_WithInvalidTokenAndAuthorizedPath_ShouldDispatchRequest() throws Exception {
        // Arrange
        String invalidToken = "invalid-bearerToken";
        when(session.getAttribute("token")).thenReturn(invalidToken);
        when(session.getAttribute("refreshToken")).thenReturn("refresh-token");
        when(jwtUtil.validateToken(anyString())).thenReturn(false);
        when(loginService.refreshToken(anyString()))
                .thenReturn(new RefreshTokenResponse("new-refresh-token"));

        try (MockedStatic<EndpointParser> endpointParser = mockStatic(EndpointParser.class);
             MockedStatic<PropertiesUtil> propertiesUtil = mockStatic(PropertiesUtil.class)) {

            EndpointParser parser = mock(EndpointParser.class);
            when(parser.controller()).thenReturn("Product");

            endpointParser.when(() -> EndpointParser.of("/product/list")).thenReturn(parser);
            propertiesUtil.when(() -> PropertiesUtil.getProperty(eq("auth.authorized"))).thenReturn("auth:*;user:create");

            // Act
            authFilter.init(); // Initialize the filter to set preAuthorizedPath
            authFilter.doFilter(request, response, chain);

            // Assert
            verify(dispatcher, times(1)).dispatch(request, response);
            verify(response, never()).sendRedirect(anyString());
        }
    }

    @Test
    void doFilter_WithInvalidTokenAndUnauthorizedPath_ShouldRedirectToLogin() throws Exception {
        // Arrange
        when(session.getAttribute("token")).thenReturn("invalid-bearerToken");
        when(session.getAttribute("refreshToken")).thenReturn("refresh-token");
        when(jwtUtil.validateToken(anyString())).thenReturn(false);
        when(loginService.refreshToken(anyString()))
                .thenThrow(new ServiceException("Refresh token invalid"));

        try (MockedStatic<EndpointParser> endpointParser = mockStatic(EndpointParser.class);
             MockedStatic<PropertiesUtil> propertiesUtil = mockStatic(PropertiesUtil.class)) {

            EndpointParser parser = mock(EndpointParser.class);
            endpointParser.when(() -> EndpointParser.of("/product/list")).thenReturn(parser);

            when(parser.controller()).thenReturn("product");
            propertiesUtil.when(() -> PropertiesUtil.getProperty(eq("auth.authorized"))).thenReturn("auth:*;user:create");
            propertiesUtil.when(() -> PropertiesUtil.getProperty("loginPage")).thenReturn("/login");

            doNothing().when(response).sendRedirect(anyString());

            // Act
            authFilter.init(); // Initialize the filter to set preAuthorizedPath
            authFilter.doFilter(request, response, chain);

            // Assert
            verify(dispatcher, never()).dispatch(request, response);
            verify(response, times(1)).sendRedirect("/login");
            verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Test
    void doFilter_WithNullEndpoint_ShouldRedirectToLogin() throws Exception {
        // Arrange
        when(session.getAttribute("token")).thenReturn(null);
        when(request.getServletPath()).thenReturn(null);
        when(jwtUtil.validateToken(anyString())).thenReturn(false);
        when(loginService.refreshToken(anyString()))
            .thenThrow(new ServiceException("Refresh token invalid"));

        try (MockedStatic<EndpointParser> endpointParser = mockStatic(EndpointParser.class);
             MockedStatic<PropertiesUtil> propertiesUtil = mockStatic(PropertiesUtil.class)) {

            EndpointParser parser = mock(EndpointParser.class);
            endpointParser.when(() -> EndpointParser.of(null)).thenReturn(parser);
            when(parser.controller()).thenReturn(null);
            when(parser.path()).thenReturn(null);

            propertiesUtil.when(() -> PropertiesUtil.getProperty("loginPage")).thenReturn("/login");

            doNothing().when(response).sendRedirect(anyString());

            // Act
            authFilter.doFilter(request, response, chain);

            // Assert
            verify(dispatcher, never()).dispatch(request, response);
            verify(response, times(1)).sendRedirect("/login");
            verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}