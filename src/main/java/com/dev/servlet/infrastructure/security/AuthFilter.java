package com.dev.servlet.infrastructure.security;

import com.dev.servlet.adapter.IServletDispatcher;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.util.EndpointParser;
import com.dev.servlet.core.util.JwtUtil;
import com.dev.servlet.core.util.PropertiesUtil;
import com.dev.servlet.domain.service.AuthService;
import com.dev.servlet.domain.transfer.response.RefreshTokenResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Slf4j
@NoArgsConstructor
public class AuthFilter implements Filter {
    public static final String LOGIN_PAGE = "loginPage";
    private final Map<String, Set<String>> preAuthorized = new java.util.HashMap<>();

    private IServletDispatcher dispatcher;
    private AuthService loginService;
    private JwtUtil jwtUtil;

    @Inject
    @Named("ServletDispatcherImpl")
    public void setDispatcher(IServletDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Inject
    public void setLoginService(AuthService loginService) {
        this.loginService = loginService;
    }

    @Inject
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostConstruct
    public void init() {
        String property = PropertiesUtil.getProperty("auth.authorized");
        setupFilter(property);
        log.info("Auth filter initialized with pre-authorized paths: {}", preAuthorized);
    }

    /**
     * Sets up the filter with pre-authorized controllers and their endpoints.
     * Expected format: controller:endpoint1,endpoint2;controller2:endpoint3
     *
     * @param property the comma-separated pre-authorized paths string
     */
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

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String token = (String) httpRequest.getSession().getAttribute("token");
        if (token == null && !isAuthorizedRequest(httpRequest)) {
            log.warn("❌ Unauthorized access to the service: {}, redirecting to login page", httpRequest.getRequestURI());
            redirectToLogin(httpResponse);
            return;
        }

        if (token != null && !jwtUtil.validateToken(token)) {
            try {
                String refreshToken = (String) httpRequest.getSession().getAttribute("refreshToken");
                RefreshTokenResponse refreshTokenResponse = loginService.refreshToken(refreshToken);
                httpRequest.getSession().setAttribute("token", refreshTokenResponse.token());
                log.info("✅ Token refreshed successfully for user {}", jwtUtil.getUserId(refreshTokenResponse.token()));

            } catch (ServiceException e) {
                log.error("❌ Failed to refresh token: {}, redirecting to login page", e.getMessage());
                redirectToLogin(httpResponse);
                return;
            }
        }

        log.debug("✅ Access authorized [endpoint={}]", httpRequest.getRequestURI());
        dispatcher.dispatch(httpRequest, httpResponse);
    }

    private void redirectToLogin(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.sendRedirect(PropertiesUtil.getProperty(LOGIN_PAGE));
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