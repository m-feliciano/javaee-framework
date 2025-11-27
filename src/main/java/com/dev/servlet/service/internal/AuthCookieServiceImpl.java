package com.dev.servlet.service.internal;

import com.dev.servlet.core.util.Properties;
import com.dev.servlet.service.AuditService;
import com.dev.servlet.service.AuthCookieService;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static com.dev.servlet.core.enums.ConstantUtils.ACCESS_TOKEN_COOKIE;
import static com.dev.servlet.core.enums.ConstantUtils.CSRF_TOKEN_COOKIE;
import static com.dev.servlet.core.enums.ConstantUtils.CSRF_TOKEN_HEADER;
import static com.dev.servlet.core.enums.ConstantUtils.REFRESH_TOKEN_COOKIE;

@Slf4j
@Singleton
public class AuthCookieServiceImpl implements AuthCookieService {

    private static final int ACCESS_TOKEN_MAX_AGE = Math.toIntExact(TimeUnit.DAYS.toSeconds(1));
    private static final int REFRESH_TOKEN_MAX_AGE = Math.toIntExact(TimeUnit.DAYS.toSeconds(30));
    private static final int CSRF_TOKEN_MAX_AGE = Math.toIntExact(TimeUnit.DAYS.toSeconds(1));

    @Setter
    @Inject
    private AuditService auditService;

    private boolean isSecure;
    private String cookiePath;
    private String cookieDomain;
    private String sameSite;
    private final SecureRandom secureRandom = new SecureRandom();

    @PostConstruct
    public void init() {
        this.cookieDomain = Properties.getEnvOrDefault("DOMAIN", null);
        this.isSecure = Properties.getOrDefault("security.cookie.secure", true);
        this.cookiePath = Properties.getOrDefault("security.cookie.path", "/");
        this.sameSite = Properties.getOrDefault("security.cookie.samesite", "Lax");

        log.info("[AuthCookieService] initialized [secure={}, path={}, domain={}, sameSite={}]",
                isSecure, cookiePath, cookieDomain, sameSite);

        if (!isSecure && "production".equalsIgnoreCase(Properties.get("app.env"))) {
            log.warn("SECURITY WARNING: Cookie Secure flag is disabled in production environment!");
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
        return null;
    }

    @Override
    public void setAccessTokenCookie(HttpServletResponse response, String token) {
        setAuthCookies(response, token, null);
        log.debug("Access token cookie set [maxAge={}s]", ACCESS_TOKEN_MAX_AGE);
    }

    @Override
    public void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        if (accessToken != null) {
            addSecureCookie(response, ACCESS_TOKEN_COOKIE, accessToken, ACCESS_TOKEN_MAX_AGE);
        }
        if (refreshToken != null) {
            addSecureCookie(response, REFRESH_TOKEN_COOKIE, refreshToken, REFRESH_TOKEN_MAX_AGE);
        }
        log.debug("Auth cookies set [accessToken={}s, refreshToken={}s]", ACCESS_TOKEN_MAX_AGE, REFRESH_TOKEN_MAX_AGE);
    }

    @Override
    public void clearCookies(HttpServletResponse response) {
        addSecureCookie(response, ACCESS_TOKEN_COOKIE, "", 0);
        addSecureCookie(response, REFRESH_TOKEN_COOKIE, "", 0);
        setCsrfTokenCookie(response, "");
    }

    @Override
    public String getAccessTokenCookieName() {
        return ACCESS_TOKEN_COOKIE;
    }

    @Override
    public String getRefreshTokenCookieName() {
        return REFRESH_TOKEN_COOKIE;
    }

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

        CookieAuditInfo auditInfo = new CookieAuditInfo(name, maxAge);
        auditService.auditSuccess("auth_cookie:set_cookie", null, new AuditPayload<>(auditInfo, cookieHeader));
    }

    private String buildSecureCookieHeader(String name, String value, int maxAge) {
        StringBuilder header = new StringBuilder();
        header.append(name).append("=").append(value);
        header.append("; Path=").append(cookiePath);
        header.append("; Max-Age=").append(maxAge);
        header.append("; HttpOnly");

        return buildHeader(header);
    }

    @Override
    public String getCsrfToken(HttpServletRequest request) {
        return getTokenFromCookie(request, CSRF_TOKEN_COOKIE);
    }

    @Override
    public void ensureCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        String csrfToken = getCsrfToken(request);
        if (StringUtils.isBlank(csrfToken)) {
            setCsrfTokenCookie(response, generateCsrfToken());
            log.debug("Generated new CSRF token");
            auditService.auditSuccess("csrf:token_generated", null, null);
        }
    }

    @Override
    public boolean validateCsrfToken(HttpServletRequest request) {
        String cookieToken = getCsrfToken(request);
        String requestToken = request.getHeader(CSRF_TOKEN_HEADER);

        if (StringUtils.isBlank(requestToken)) {
            requestToken = request.getParameter(CSRF_TOKEN_HEADER);
        }

        if (StringUtils.isBlank(cookieToken) || StringUtils.isBlank(requestToken)) {
            log.warn("Missing CSRF token [cookie={}, request={}]", cookieToken != null, requestToken != null);
            auditService.auditFailure("csrf:token_missing", null, null);
            return false;
        }

        boolean valid = cookieToken.equals(requestToken);
        if (!valid) {
            log.warn("CSRF token mismatch");
            auditService.auditFailure("csrf:token_mismatch", null, null);
        } else {
            log.debug("CSRF token validated successfully");
        }
        return valid;
    }

    private String generateCsrfToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private void setCsrfTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(CSRF_TOKEN_COOKIE, token);
        cookie.setHttpOnly(false);
        cookie.setSecure(isSecure);
        cookie.setPath(cookiePath);
        cookie.setMaxAge(CSRF_TOKEN_MAX_AGE);

        if (StringUtils.isNotBlank(cookieDomain)) {
            cookie.setDomain(cookieDomain);
        }

        response.addCookie(cookie);

        String cookieHeader = buildCsrfCookieHeader(token);
        response.addHeader("Set-Cookie", cookieHeader);

        CookieAuditInfo auditInfo = new CookieAuditInfo(CSRF_TOKEN_COOKIE, CSRF_TOKEN_MAX_AGE);
        auditService.auditSuccess("csrf:cookie_set", null, new AuditPayload<>(auditInfo, cookieHeader));
    }

    private String buildCsrfCookieHeader(String token) {
        StringBuilder header = new StringBuilder();
        header.append(CSRF_TOKEN_COOKIE).append("=").append(token);
        header.append("; Path=").append(cookiePath);
        header.append("; Max-Age=").append(CSRF_TOKEN_MAX_AGE);
        return buildHeader(header);
    }

    @NotNull
    private String buildHeader(StringBuilder header) {
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

    record CookieAuditInfo(String cookieName, int cookieMaxAge) {}
}

