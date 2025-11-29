package com.dev.servlet.application.usecase.auth;

import com.dev.servlet.application.port.in.auth.AuthCookieUseCasePort;
import com.dev.servlet.application.port.out.AuthCookiePort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;

@ApplicationScoped
@NoArgsConstructor
public class AuthCookieUseCase implements AuthCookieUseCasePort {
    @Inject
    private AuthCookiePort authCookiePort;

    @Override
    public String getTokenFromCookie(HttpServletRequest request, String cookieName) {
        return authCookiePort.getTokenFromCookie(request, cookieName);
    }

    @Override
    public void setAccessTokenCookie(HttpServletResponse response, String token) {
        authCookiePort.setAccessTokenCookie(response, token);
    }

    @Override
    public void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        authCookiePort.setAuthCookies(response, accessToken, refreshToken);
    }

    @Override
    public void clearCookies(HttpServletResponse response) {
        authCookiePort.clearCookies(response);
    }

    @Override
    public String getAccessTokenCookieName() {
        return authCookiePort.getAccessTokenCookieName();
    }

    @Override
    public String getRefreshTokenCookieName() {
        return authCookiePort.getRefreshTokenCookieName();
    }

    @Override
    public String getCsrfToken(HttpServletRequest request) {
        return authCookiePort.getCsrfToken(request);
    }

    @Override
    public void ensureCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        authCookiePort.ensureCsrfToken(request, response);
    }

    @Override
    public boolean validateCsrfToken(HttpServletRequest request) {
        return authCookiePort.validateCsrfToken(request);
    }
}
