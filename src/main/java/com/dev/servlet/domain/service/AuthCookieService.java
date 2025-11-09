package com.dev.servlet.domain.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthCookieService {

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

