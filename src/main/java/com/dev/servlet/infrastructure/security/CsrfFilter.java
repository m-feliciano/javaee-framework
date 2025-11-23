package com.dev.servlet.infrastructure.security;

import com.dev.servlet.domain.model.enums.RequestMethod;
import com.dev.servlet.service.AuthCookieService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RegExUtils;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
public class CsrfFilter implements Filter {

    public static final List<String> WHITELISTED_ENDPOINTS = List.of(
            "/health/check",
            "/health/ready",
            "/health/live",
            "/health/up",
            "/user/confirm"
    );

    @Inject
    private AuthCookieService authCookieService;

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

        if (RequestMethod.GET.name().equals(method)) {
            authCookieService.ensureCsrfToken(request, response);
            chain.doFilter(request, response);
            return;
        }

        if (isStatefulMethod(method)) {
            if (!authCookieService.validateCsrfToken(request)) {
                log.warn("CSRF validation failed [method={}, uri={}]", method, requestURI);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
                return;
            }
            log.debug("CSRF validation successful [method={}, uri={}]", method, requestURI);
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

