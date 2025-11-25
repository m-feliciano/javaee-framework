package com.dev.servlet.infrastructure.security;

import com.dev.servlet.adapter.IServletDispatcher;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.util.EndpointParser;
import com.dev.servlet.core.util.JwtUtil;
import com.dev.servlet.core.util.Properties;
import com.dev.servlet.domain.response.RefreshTokenResponse;
import com.dev.servlet.service.AuditService;
import com.dev.servlet.service.AuthCookieService;
import com.dev.servlet.service.AuthService;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.dev.servlet.core.enums.ConstantUtils.BEARER_PREFIX;
import static com.dev.servlet.core.enums.ConstantUtils.LOGIN_PAGE;

@Setter
@Slf4j
@NoArgsConstructor
public class AuthFilter implements Filter {
    private final Map<String, Set<String>> preAuthorized = new java.util.HashMap<>();

    @Inject
    private IServletDispatcher dispatcher;
    @Inject
    private AuthService loginService;
    @Inject
    private JwtUtil jwtUtil;
    @Inject
    private AuthCookieService cookieService;
    @Inject
    private AuditService auditService;

    @PostConstruct
    public void init() {
        String property = Properties.get("auth.authorized");
        setupFilter(property);
        log.info("Auth filter initialized with pre-authorized paths: {}", preAuthorized);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        boolean isAuthorized = isAuthorizedRequest(httpRequest);
        if (isAuthorized) {
            auditService.auditSuccess("auth_filter:login", null, null);
            dispatcher.dispatch(httpRequest, httpResponse);
            return;
        }

        String token = cookieService.getTokenFromCookie(httpRequest, cookieService.getAccessTokenCookieName());
        String refreshToken = cookieService.getTokenFromCookie(httpRequest, cookieService.getRefreshTokenCookieName());

        if (token == null && refreshToken == null) {
            log.warn("No tokens found for: {}, redirecting to login page", httpRequest.getRequestURI());
            redirectToLogin(httpResponse);
            return;
        }

        boolean tokenValid = token != null && jwtUtil.validateToken(token);
        if (tokenValid) {
            log.debug("Valid token access [endpoint={}]", httpRequest.getRequestURI());
            auditService.auditSuccess("auth_filter:valid_token", null, null);
            dispatcher.dispatch(httpRequest, httpResponse);
            return;
        }

        if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
            try {
                RefreshTokenResponse refreshTokenResponse = loginService.refreshToken(BEARER_PREFIX + refreshToken);
                cookieService.setAuthCookies(httpResponse, refreshTokenResponse.token(), refreshTokenResponse.refreshToken());
                auditService.auditSuccess("auth_filter:refresh_token", null, null);

                httpResponse.setStatus(HttpServletResponse.SC_FOUND);
                httpResponse.sendRedirect(httpRequest.getRequestURI());
                return;

            } catch (ServiceException e) {
                log.error("Failed to refresh token, redirecting to login page", e);
            }
        }

        log.warn("Both tokens are invalid for: {}, redirecting to login page", httpRequest.getRequestURI());
        cookieService.clearCookies(httpResponse);
        redirectToLogin(httpResponse);
    }

    private void redirectToLogin(HttpServletResponse response) throws IOException {
        auditService.auditWarning("auth_filter:redirect_login", null, null);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.sendRedirect(Properties.get(LOGIN_PAGE));
    }

    private void setupFilter(String property) {
        for (String entry : property.split(";")) {
            String[] parts = entry.split(":");
            String controller = parts[0];
            String[] endpoints = parts[1].split(",");

            preAuthorized.computeIfAbsent(controller, k -> new HashSet<>());
            for (String ep : endpoints) {
                preAuthorized.get(controller).add(ep);
            }
        }
    }

    private boolean isAuthorizedRequest(HttpServletRequest request) {
        var parser = EndpointParser.of(request.getServletPath());
        String controller = parser.controller();
        String path = parser.path();
        if (path == null || controller == null) return false;

        controller = controller.toLowerCase();
        if (!preAuthorized.containsKey(controller)) {
            return false;
        }

        var authorizations = preAuthorized.get(controller);
        return authorizations.contains(path) || authorizations.contains("*");
    }
}