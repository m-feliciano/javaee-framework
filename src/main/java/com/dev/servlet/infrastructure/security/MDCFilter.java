package com.dev.servlet.infrastructure.security;

import lombok.NoArgsConstructor;
import org.slf4j.MDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.UUID;

@NoArgsConstructor
public class MDCFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        MDC.put("correlationId", UUID.randomUUID().toString());
        try {
            chain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.clear();
        }
    }
}