package com.dev.servlet.adapter.out.security;

import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.security.AuthCookiePort;
import com.dev.servlet.infrastructure.config.Properties;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.dev.servlet.infrastructure.utils.CloudFrontCrypto.generateCloudFrontSignedCookies;
import static com.dev.servlet.shared.enums.ConstantUtils.ACCESS_TOKEN_COOKIE;
import static com.dev.servlet.shared.enums.ConstantUtils.CSRF_TOKEN_COOKIE;
import static com.dev.servlet.shared.enums.ConstantUtils.CSRF_TOKEN_HEADER;
import static com.dev.servlet.shared.enums.ConstantUtils.REFRESH_TOKEN_COOKIE;

@Slf4j
@ApplicationScoped
public class AuthCookieAdapter implements AuthCookiePort {

    private static final int CSRF_TOKEN_MAX_AGE = (int) TimeUnit.HOURS.toSeconds(12);
    private static final int ACCESS_TOKEN_MAX_AGE = (int) TimeUnit.MINUTES.toSeconds(15);
    private static final int REFRESH_TOKEN_MAX_AGE = (int) TimeUnit.DAYS.toSeconds(14);

    private final UUID cdnCookiesKey = UUID.randomUUID();
    private final SecureRandom secureRandom = new SecureRandom();

    private boolean isSecure;
    private String cookiePath;
    private String domain;
    private String sameSite;

    @Inject
    private CachePort cache;

    @PostConstruct
    public void init() {
        this.domain = Properties.getAppDomain();

        this.isSecure = Properties.getOrDefault("security.cookie.secure", true);
        this.cookiePath = Properties.getOrDefault("security.cookie.path", "/");
        this.sameSite = Properties.getOrDefault("security.cookie.samesite", "Lax");

        log.info("[AuthCookieService] initialized [secure={}, path={}, domain={}, sameSite={}]",
                isSecure, cookiePath, domain, sameSite);

        if (!isSecure && "production".equalsIgnoreCase(Properties.get("app.env"))) {
            log.warn("[SEVERE] SECURITY WARNING: Cookie Secure flag is disabled in production environment!");
        }
    }

    public String getCookieFromArray(Cookie[] cookies, String cookieName) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    log.debug("Token retrieved from cookie: {}", getAccessTokenCookieName());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public String getCookieFromList(List<String> cookiesList, String cookieName) {
        Cookie[] cookies = getCookies(cookiesList);
        return this.getCookieFromArray(cookies, cookieName);
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

        if (StringUtils.isNotBlank(domain)) cookie.setDomain(domain);

        String cookieHeader = buildSecureCookieHeader(name, value, maxAge, cookiePath, sameSite);
        response.addHeader("Set-Cookie", cookieHeader);
    }

    private String buildSecureCookieHeader(String name, String value, int maxAge, String path, String sameSite) {
        StringBuilder header = new StringBuilder();
        header.append(name).append("=").append(value);
        header.append("; Path=").append(path);
        header.append("; Max-Age=").append(maxAge);
        header.append("; HttpOnly");
        return buildHeader(header, domain, sameSite);
    }

    @Override
    public String getCsrfToken(HttpServletRequest request) {
        return this.getCookieFromArray(request.getCookies(), CSRF_TOKEN_COOKIE);
    }

    @Override
    public void ensureCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        String csrfToken = getCsrfToken(request);
        if (StringUtils.isBlank(csrfToken)) {
            setCsrfTokenCookie(response, generateCsrfToken());
            log.debug("Generated new CSRF token");
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
            return false;
        }

        boolean valid = cookieToken.equals(requestToken);
        if (!valid)  log.warn("CSRF token mismatch");
        else log.debug("CSRF token validated successfully");

        return valid;
    }

    @Override
    public void addCdnCookies(HttpServletResponse httpResponse) {
        if (Properties.isDevelopmentMode() || Properties.isDemoModeEnabled()) {
            log.debug("Development mode detected, skipping CDN cookies generation");
            return;
        }

        try {
            String namespace = "cdn:cookies";

            Map<String, String> cookies = cache.get(namespace, cdnCookiesKey);
            if (cookies == null || cookies.isEmpty()) {
                log.debug("No cached CDN cookies found, generating new ones");

                cookies = generateCloudFrontSignedCookies();

                Duration ttl = Duration.ofMinutes(15).minus(Duration.ofSeconds(30));
                cache.set(namespace, cdnCookiesKey, cookies, ttl);
            } else {
                log.debug("Using cached CDN cookies");
            }

            int maxAge = -1;
            String path = "/";
            cookies.forEach((name, value)
                    -> httpResponse.addHeader("Set-Cookie", buildSecureCookieHeader(name, value, maxAge, path, sameSite)));

        } catch (Exception e) {
            log.error("Error generating CloudFront signed cookies", e);
        }
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

        if (StringUtils.isNotBlank(domain)) cookie.setDomain(domain);

        String cookieHeader = buildCsrfCookieHeader(token);
        response.addHeader("Set-Cookie", cookieHeader);
    }

    private String buildCsrfCookieHeader(String token) {
        StringBuilder header = new StringBuilder();
        header.append(CSRF_TOKEN_COOKIE).append("=").append(token);
        header.append("; Path=").append(cookiePath);
        header.append("; Max-Age=").append(CSRF_TOKEN_MAX_AGE);
        return buildHeader(header, domain, sameSite);
    }

    private String buildHeader(StringBuilder header, String domain, String sameSite) {
        if (isSecure) header.append("; Secure");

        if (StringUtils.isNotBlank(domain)) header.append("; Domain=").append(domain);

        if (StringUtils.isNotBlank(sameSite)) header.append("; SameSite=").append(sameSite);

        return header.toString();
    }

    private static Cookie[] getCookies(List<String> cookies) {
        return cookies.stream()
                .flatMap(header -> Arrays.stream(header.split(";")))
                .map(String::trim)
                .map(cookie -> {
                    String[] parts = cookie.split("=", 2);
                    if (parts.length == 2) {
                        return new Cookie(parts[0], parts[1]);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(Cookie[]::new);
    }
}
