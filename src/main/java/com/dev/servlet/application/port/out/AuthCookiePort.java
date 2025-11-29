package com.dev.servlet.application.port.out;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthCookiePort {
    String getTokenFromCookie(HttpServletRequest request, String name);

    void setAccessTokenCookie(HttpServletResponse response, String token);

    void clearCookies(HttpServletResponse response);

    void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken);

    String getAccessTokenCookieName();

    String getRefreshTokenCookieName();

    String getCsrfToken(HttpServletRequest request);

    void ensureCsrfToken(HttpServletRequest request, HttpServletResponse response);

    boolean validateCsrfToken(HttpServletRequest request);
}
