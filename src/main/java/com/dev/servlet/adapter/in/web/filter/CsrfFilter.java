package com.dev.servlet.adapter.in.web.filter;

import com.dev.servlet.application.port.out.security.AuthCookiePort;
import com.dev.servlet.domain.entity.enums.RequestMethod;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RegExUtils;

import java.io.IOException;
import java.util.List;

@Slf4j
@ApplicationScoped
public class CsrfFilter implements Filter {
    public static final List<String> WHITELISTED_ENDPOINTS = List.of(
            "/health/check",
            "/health/ready",
            "/health/live",
            "/health/up",
            "/user/confirm",
            "/alert/clear",
            "/auth/login"
    );

    @Inject
    private AuthCookiePort AuthCookie;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String normalizedUri = RegExUtils.removeAll(request.getRequestURI(), "/api/v[0-9]+");

        if (normalizedUri != null && WHITELISTED_ENDPOINTS.stream().anyMatch(normalizedUri::equals)) {
            log.debug("Skipping CSRF validation for login endpoint");
            chain.doFilter(request, response);
            return;
        }

        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        if (RequestMethod.GET.getMethod().equals(method)) {
            AuthCookie.ensureCsrfToken(request, response);
            chain.doFilter(request, response);
            return;
        }

        if (isStatefulMethod(method)) {
            if (!AuthCookie.validateCsrfToken(request)) {
                log.warn("CSRF validation failed [implementation={}, uri={}]", method, requestURI);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
                return;
            }
            log.debug("CSRF validation successful [implementation={}, uri={}]", method, requestURI);
        }
        chain.doFilter(request, response);
    }

    private boolean isStatefulMethod(String method) {
        return RequestMethod.POST.name().equals(method)
               || RequestMethod.PUT.name().equals(method)
               || RequestMethod.PATCH.name().equals(method)
               || RequestMethod.DELETE.name().equals(method);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("[CsrfFilter] initialized - protecting POST/PUT/DELETE operations");
    }

    @Override
    public void destroy() {
        log.info("[CsrfFilter] destroyed");
    }
}
