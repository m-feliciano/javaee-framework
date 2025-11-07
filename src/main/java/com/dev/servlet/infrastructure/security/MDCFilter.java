package com.dev.servlet.infrastructure.security;

import lombok.NoArgsConstructor;
import org.slf4j.MDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@NoArgsConstructor
public class MDCFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        MDC.put("correlationId", UUID.randomUUID().toString());
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        MDC.put("httpMethod", httpRequest.getMethod());
        MDC.put("endpoint", httpRequest.getRequestURI());
        MDC.put("ipAddress", getClientIpAddress(httpRequest));
        MDC.put("userAgent", httpRequest.getHeader("User-Agent"));
        MDC.put("requestStartTime", String.valueOf(System.currentTimeMillis()));

        try {
            chain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.clear();
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }
}