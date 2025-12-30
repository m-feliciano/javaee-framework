package com.servletstack.application.port.out.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface AuthCookiePort {
    String getCookieFromArray(Cookie[] cookies, String cookieName);

    String getCookieFromList(List<String> cookies, String cookieName);

    void clearCookies(HttpServletResponse response);

    void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken);

    String getAccessTokenCookieName();

    String getRefreshTokenCookieName();

    String getCsrfToken(HttpServletRequest request);

    void ensureCsrfToken(HttpServletRequest request, HttpServletResponse response);

    boolean validateCsrfToken(HttpServletRequest request);

    void addCdnCookies(HttpServletResponse httpResponse);
}
