package com.dev.servlet.adapter.in.web.filter;

import com.dev.servlet.adapter.in.web.util.EndpointParser;
import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.in.auth.RefreshTokenPort;
import com.dev.servlet.application.port.out.security.AuthCookiePort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.response.RefreshTokenResponse;
import com.dev.servlet.infrastructure.config.Properties;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.dev.servlet.shared.enums.ConstantUtils.BEARER_PREFIX;
import static com.dev.servlet.shared.enums.ConstantUtils.LOGIN_PAGE;

@Slf4j
@ApplicationScoped
public class AuthFilter implements Filter {

    private final Map<String, Set<String>> preAuthorized = new HashMap<>();

    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private RefreshTokenPort refreshTokenPort;
    @Inject
    private AuthCookiePort authCookiePort;

    @PostConstruct
    public void init() {
        setupFilter(Properties.get("auth.authorized"));
        log.info("Auth filter initialized with pre-authorized paths: {}", preAuthorized);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (isAuthorizedRequest(request)) {
            chain.doFilter(req, res);
            return;
        }

        Cookie[] cookies = request.getCookies();
        String token = authCookiePort.getCookieFromArray(cookies, authCookiePort.getAccessTokenCookieName());
        String refreshToken = authCookiePort.getCookieFromArray(cookies, authCookiePort.getRefreshTokenCookieName());

        if (token == null && refreshToken == null) {
            log.warn("No tokens found for: {}", request.getRequestURI());
            redirectToLogin(response);
            return;
        }

        if (token != null && authenticationPort.validateToken(token)) {
            log.debug("Valid token access [endpoint={}]", request.getRequestURI());
            chain.doFilter(req, res);
            return;
        }

        if (refreshToken != null && authenticationPort.validateToken(refreshToken)) {
            try {
                RefreshTokenResponse refresh = refreshTokenPort.refreshToken(BEARER_PREFIX + refreshToken);
                authCookiePort.setAuthCookies(response, refresh.token(), refresh.refreshToken());
                response.sendRedirect(request.getRequestURI());
                return;
            } catch (AppException e) {
                log.error("Failed to refresh token", e);
            }
        }

        log.warn("Invalid tokens for: {}", request.getRequestURI());
        authCookiePort.clearCookies(response);
        redirectToLogin(response);
    }

    private void redirectToLogin(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.sendRedirect(Properties.get(LOGIN_PAGE));
    }

    private void setupFilter(String property) {
        if (property == null) return;

        for (String entry : property.split(";")) {
            String[] parts = entry.split(":");
            String controller = parts[0].toLowerCase();
            String[] endpoints = parts[1].split(",");
            preAuthorized.computeIfAbsent(controller, k -> new HashSet<>());
            for (String ep : endpoints) {
                preAuthorized.get(controller).add(ep);
            }
        }
    }

    private boolean isAuthorizedRequest(HttpServletRequest request) {
        var parser = EndpointParser.of(request.getRequestURI());
        String controller = parser.controller();
        String path = parser.path();
        if (controller == null || path == null) return false;

        controller = controller.toLowerCase();
        var allowed = preAuthorized.get(controller);

        return allowed != null &&
               (allowed.contains(path) || allowed.contains("*"));
    }
}
