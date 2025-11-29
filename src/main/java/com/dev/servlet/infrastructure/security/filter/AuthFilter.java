package com.dev.servlet.infrastructure.security.filter;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.port.in.auth.AuthCookieUseCasePort;
import com.dev.servlet.application.port.in.auth.RefreshTokenUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.application.transfer.response.RefreshTokenResponse;
import com.dev.servlet.infrastructure.config.Properties;
import com.dev.servlet.shared.util.EndpointParser;
import com.dev.servlet.web.dispatcher.IServletDispatcher;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.dev.servlet.shared.enums.ConstantUtils.BEARER_PREFIX;
import static com.dev.servlet.shared.enums.ConstantUtils.LOGIN_PAGE;

@Setter
@Slf4j
@ApplicationScoped
public class AuthFilter implements Filter {
    private Map<String, Set<String>> preAuthorized = new java.util.HashMap<>();

    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private AuditPort auditPort;
    @Inject
    private IServletDispatcher dispatcher;
    @Inject
    private RefreshTokenUseCasePort refreshTokenUseCasePort;
    @Inject
    private AuthCookieUseCasePort authCookieUseCasePort;

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
            dispatcher.dispatch(httpRequest, httpResponse);
            return;
        }

        String token = authCookieUseCasePort.getTokenFromCookie(httpRequest, authCookieUseCasePort.getAccessTokenCookieName());
        String refreshToken = authCookieUseCasePort.getTokenFromCookie(httpRequest, authCookieUseCasePort.getRefreshTokenCookieName());
        if (token == null && refreshToken == null) {
            log.warn("No tokens found for: {}, redirecting to login page", httpRequest.getRequestURI());
            redirectToLogin(httpResponse);
            return;
        }

        boolean tokenValid = token != null && authenticationPort.validateToken(token);
        if (tokenValid) {
            log.debug("Valid token access [endpoint={}]", httpRequest.getRequestURI());
            dispatcher.dispatch(httpRequest, httpResponse);
            return;
        }

        if (refreshToken != null && authenticationPort.validateToken(refreshToken)) {
            try {
                RefreshTokenResponse refreshTokenResponse = refreshTokenUseCasePort.refreshToken(BEARER_PREFIX + refreshToken);
                authCookieUseCasePort.setAuthCookies(httpResponse, refreshTokenResponse.token(), refreshTokenResponse.refreshToken());
                httpResponse.setStatus(HttpServletResponse.SC_FOUND);
                httpResponse.sendRedirect(httpRequest.getRequestURI());
                return;
            } catch (ApplicationException e) {
                log.error("Failed to refresh token, redirecting to login page", e);
            }
        }

        log.warn("Both tokens are invalid for: {}, redirecting to login page", httpRequest.getRequestURI());
        authCookieUseCasePort.clearCookies(httpResponse);
        redirectToLogin(httpResponse);
    }

    private void redirectToLogin(HttpServletResponse response) throws IOException {
        auditPort.warning("auth_filter:redirect_login", null, null);
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
