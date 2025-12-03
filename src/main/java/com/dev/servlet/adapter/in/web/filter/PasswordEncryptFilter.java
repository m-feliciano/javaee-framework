package com.dev.servlet.adapter.in.web.filter;

import com.dev.servlet.adapter.in.web.filter.wrapper.SecurityRequestWrapper;
import com.dev.servlet.infrastructure.utils.CryptoUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@ApplicationScoped
public class PasswordEncryptFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String passwordText = httpRequest.getParameter("password");
        String confirmationText = httpRequest.getParameter("confirmPassword");
        if (passwordText != null) {
            String password = CryptoUtils.encrypt(passwordText);
            String confirmation = confirmationText != null ? CryptoUtils.encrypt(confirmationText) : null;
            SecurityRequestWrapper wrappedRequest = new SecurityRequestWrapper(httpRequest, password, confirmation);
            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
