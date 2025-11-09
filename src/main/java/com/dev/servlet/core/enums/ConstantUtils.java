package com.dev.servlet.core.enums;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class ConstantUtils {

    public static final String ACCESS_TOKEN_COOKIE = "accessToken";
    public static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    public static final String CSRF_TOKEN_COOKIE = "XSRF-TOKEN";
    public static final String CSRF_TOKEN_HEADER = "X-XSRF-TOKEN";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String LOGIN_PAGE = "loginPage";
}
