package com.dev.servlet.infrastructure.security;

import com.dev.servlet.core.util.RateLimiter;
import com.dev.servlet.core.util.PropertiesUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@NoArgsConstructor
public class RateLimitFilter implements Filter {

    private static final Map<String, EndpointRateLimit> endpointLimits = new HashMap<>();

    @Inject
    private RateLimiter rateLimiter;

    private boolean enabled;

    @PostConstruct
    public void init() {
        this.enabled = PropertiesUtil.getProperty("rate.limit.enabled", true);
        loadEndpointConfiguration();
        log.info("[RateLimitFilter] initialized [enabled={}]", enabled);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!enabled) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String endpoint = httpRequest.getServletPath();
        String method = httpRequest.getMethod();

        EndpointRateLimit limit = findMatchingLimit(endpoint, method);

        if (limit == null) {
            chain.doFilter(request, response);
            return;
        }

        String identifier = extractIdentifier(httpRequest, limit.identifierType);

        if (!rateLimiter.tryConsume(identifier)) {
            long secondsUntilRefill = rateLimiter.getSecondsUntilRefill(identifier);
            log.warn("Rate limit exceeded [endpoint={}, identifier={}, retryAfter={}s]",
                    endpoint, identifier, secondsUntilRefill);

            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.setHeader("Retry-After", String.valueOf(secondsUntilRefill));
            httpResponse.setHeader("X-RateLimit-Limit", String.valueOf(limit.maxRequests));
            httpResponse.setHeader("X-RateLimit-Remaining", "0");

            httpResponse.getWriter().write(String.format(
                    "{\"error\":\"Rate limit exceeded\",\"retryAfter\":%d}",
                    secondsUntilRefill
            ));
            return;
        }

        long availableTokens = rateLimiter.getAvailableTokens(identifier);
        httpResponse.setHeader("X-RateLimit-Limit", String.valueOf(limit.maxRequests));
        httpResponse.setHeader("X-RateLimit-Remaining", String.valueOf(availableTokens));

        chain.doFilter(request, response);
    }

    private void loadEndpointConfiguration() {
        endpointLimits.put("POST:/auth/login", new EndpointRateLimit(5, 900, 15, "user"));
        endpointLimits.put("*:/api/*", new EndpointRateLimit(100, 60, 5, "ip"));
        log.info("Loaded {} endpoint rate limit configurations", endpointLimits.size());
    }

    private EndpointRateLimit findMatchingLimit(String endpoint, String method) {
        String key = method + ":" + endpoint;
        EndpointRateLimit limit = endpointLimits.get(key);
        if (limit != null) {
            return limit;
        }

        for (Map.Entry<String, EndpointRateLimit> entry : endpointLimits.entrySet()) {
            String pattern = entry.getKey();
            if (matches(pattern, method, endpoint)) {
                return entry.getValue();
            }
        }

        return null;
    }

    private boolean matches(String pattern, String method, String endpoint) {
        String[] parts = pattern.split(":");
        if (parts.length != 2) {
            return false;
        }

        String methodPattern = parts[0];
        String endpointPattern = parts[1];

        boolean methodMatches = "*".equals(methodPattern) || method.equals(methodPattern);
        boolean endpointMatches = endpointPattern.endsWith("*")
                ? endpoint.startsWith(endpointPattern.substring(0, endpointPattern.length() - 1))
                : endpoint.equals(endpointPattern);

        return methodMatches && endpointMatches;
    }

    private String extractIdentifier(HttpServletRequest request, String type) {
        switch (type) {
            case "user":
                String login = request.getParameter("login");
                return login != null ? login : getClientIp(request);

            case "user+ip":
                String user = request.getParameter("login");
                String ip = getClientIp(request);
                return (user != null ? user : "anonymous") + ":" + ip;

            default:
                return getClientIp(request);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) {
            return ip.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private record EndpointRateLimit(int maxRequests,
                                     long windowSeconds,
                                     long blockDurationMinutes,
                                     String identifierType) {
    }
}

