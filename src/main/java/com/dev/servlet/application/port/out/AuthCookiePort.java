package com.dev.servlet.application.port.out;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface AuthCookiePort {
    String getTokenFromCookieArray(Cookie[] cookies, String name);

    String getTokenFromCookieList(List<String> cookies, String name);

    void setAccessTokenCookie(HttpServletResponse response, String token);

    void clearCookies(HttpServletResponse response);

    void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken);

    String getAccessTokenCookieName();

    String getRefreshTokenCookieName();

    String getCsrfToken(HttpServletRequest request);

    void ensureCsrfToken(HttpServletRequest request, HttpServletResponse response);

    boolean validateCsrfToken(HttpServletRequest request);
}
