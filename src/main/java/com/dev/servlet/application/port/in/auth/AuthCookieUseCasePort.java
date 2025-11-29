package com.dev.servlet.application.port.in.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthCookieUseCasePort {
    String getTokenFromCookie(HttpServletRequest request, String cookieName);

    void setAccessTokenCookie(HttpServletResponse response, String token);

    void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken);

    void clearCookies(HttpServletResponse response);

    String getAccessTokenCookieName();

    String getRefreshTokenCookieName();

    String getCsrfToken(HttpServletRequest request);

    void ensureCsrfToken(HttpServletRequest request, HttpServletResponse response);

    boolean validateCsrfToken(HttpServletRequest request);
}

