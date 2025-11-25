package com.dev.servlet.infrastructure.security;

import com.dev.servlet.core.util.Properties;
import com.dev.servlet.core.util.RateLimiter;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

@Slf4j
@NoArgsConstructor
public class RateLimitFilter implements Filter {
    @Inject
    private RateLimiter rateLimiter;

    private boolean enabled;

    @PostConstruct
    public void init() {
        this.enabled = Properties.getOrDefault("rate_limit.enabled", true);
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
        String identifier = getClientIp(httpRequest);

        if (!rateLimiter.tryConsume(identifier, 1)) {
            long secondsUntilRefill = rateLimiter.getSecondsUntilRefill(identifier);
            log.warn("Rate limit exceeded [endpoint={}, identifier={}, retryAfter={}s]", endpoint, identifier, secondsUntilRefill);

            Long capacityLimit = Properties.getOrDefault("rate_limit.capacity", 30L);

            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.setHeader("Retry-After", String.valueOf(secondsUntilRefill));
            httpResponse.setHeader("X-RateLimit-Limit", String.valueOf(capacityLimit));
            httpResponse.setHeader("X-RateLimit-Remaining", "0");
            httpResponse.getWriter()
                    .write("{\"error\":\"Rate limit exceeded\",\"retryAfter\":%d}".formatted(secondsUntilRefill));
            return;
        }

        long availableTokens = rateLimiter.getAvailableTokens(identifier);
        httpResponse.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, availableTokens)));
        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) {
            String trim = ip.split(",")[0];
            if (StringUtils.isNotBlank(trim))
                return trim.trim();
        }

        return request.getRemoteAddr();
    }
}
