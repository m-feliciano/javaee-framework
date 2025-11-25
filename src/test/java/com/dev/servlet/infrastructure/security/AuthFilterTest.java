package com.dev.servlet.infrastructure.security;

import com.dev.servlet.adapter.IServletDispatcher;
import com.dev.servlet.core.util.JwtUtil;
import com.dev.servlet.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;
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

        when(request.getServletPath()).thenReturn("/product/list");
    }
}