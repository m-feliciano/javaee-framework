package com.dev.servlet.domain.service.internal;

import com.dev.servlet.core.util.PropertiesUtil;
import com.dev.servlet.domain.service.AuthCookieService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Slf4j
@Singleton
public class AuthCookieServiceImpl implements AuthCookieService {

    public static final String ACCESS_TOKEN_COOKIE = "accessToken";
    public static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    private static final int ACCESS_TOKEN_MAX_AGE = Math.toIntExact(TimeUnit.MINUTES.toSeconds(30));
    private static final int REFRESH_TOKEN_MAX_AGE = Math.toIntExact(TimeUnit.DAYS.toSeconds(30));

    private boolean isSecure;
    private String cookiePath;
    private String cookieDomain;
    private String sameSite;

    @PostConstruct
    public void init() {
        this.isSecure = PropertiesUtil.getProperty("security.cookie.secure", true);
        this.cookiePath = PropertiesUtil.getProperty("security.cookie.path", "/");
        this.cookieDomain = PropertiesUtil.getProperty("security.cookie.domain");
        this.sameSite = PropertiesUtil.getProperty("security.cookie.samesite", "Lax");

        log.info("[AuthCookieService] initialized [secure={}, path={}, domain={}, sameSite={}]",
                isSecure, cookiePath, cookieDomain, sameSite);

        if (!isSecure && "production".equalsIgnoreCase(PropertiesUtil.getProperty("app.env"))) {
            log.warn("⚠️ SECURITY WARNING: Cookie Secure flag is disabled in production environment!");
        }
    }

    @Override
    public String getTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    log.debug("Token retrieved from cookie: {}", cookieName);
                    return cookie.getValue();
                }
            }
        }
        log.debug("Cookie not found: {}", cookieName);
        return null;
    }

    @Override
    public void setAccessTokenCookie(HttpServletResponse response, String token) {
        setAuthCookies(response, token, null);
        log.debug("✅ Access token cookie set [maxAge={}s]", ACCESS_TOKEN_MAX_AGE);
    }

    @Override
    public void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        if (accessToken != null) {
            addSecureCookie(response, ACCESS_TOKEN_COOKIE, accessToken, ACCESS_TOKEN_MAX_AGE);
        }
        if (refreshToken != null) {
            addSecureCookie(response, REFRESH_TOKEN_COOKIE, refreshToken, REFRESH_TOKEN_MAX_AGE);
        }
        log.debug("✅ Auth cookies set [accessToken={}s, refreshToken={}s]", ACCESS_TOKEN_MAX_AGE, REFRESH_TOKEN_MAX_AGE);
    }

    @Override
    public void clearAuthCookies(HttpServletResponse response) {
        addSecureCookie(response, ACCESS_TOKEN_COOKIE, "", 0);
        addSecureCookie(response, REFRESH_TOKEN_COOKIE, "", 0);
        log.debug("✅ Auth cookies cleared");
    }

    @Override
    public String getAccessTokenCookieName() {
        return ACCESS_TOKEN_COOKIE;
    }

    @Override
    public String getRefreshTokenCookieName() {
        return REFRESH_TOKEN_COOKIE;
    }

    /**
     * Adds a secure cookie with all security attributes including SameSite.
     * Since Servlet 3.x doesn't support SameSite natively, we set it via Set-Cookie header.
     *
     * @param response the HTTP response
     * @param name     the cookie name
     * @param value    the cookie value
     * @param maxAge   the cookie max age in seconds
     */
    private void addSecureCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(isSecure);
        cookie.setPath(cookiePath);
        cookie.setMaxAge(maxAge);

        if (StringUtils.isNotBlank(cookieDomain)) {
            cookie.setDomain(cookieDomain);
        }

        response.addCookie(cookie);

        String cookieHeader = buildSecureCookieHeader(name, value, maxAge);
        response.addHeader("Set-Cookie", cookieHeader);

        log.trace("Cookie added: {} [maxAge={}s, secure={}, httpOnly=true, sameSite={}]",
                name, maxAge, isSecure, sameSite);
    }

    private String buildSecureCookieHeader(String name, String value, int maxAge) {
        StringBuilder header = new StringBuilder();
        header.append(name).append("=").append(value);
        header.append("; Path=").append(cookiePath);
        header.append("; Max-Age=").append(maxAge);
        header.append("; HttpOnly");

        if (isSecure) {
            header.append("; Secure");
        }

        if (StringUtils.isNotBlank(cookieDomain)) {
            header.append("; Domain=").append(cookieDomain);
        }

        if (StringUtils.isNotBlank(sameSite)) {
            header.append("; SameSite=").append(sameSite);
        }

        return header.toString();
    }

    public static String extractBearerToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                    return "Bearer " + cookie.getValue();
                }
            }
        }
        return null;
    }
}

