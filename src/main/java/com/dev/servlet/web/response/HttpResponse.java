package com.dev.servlet.web.response;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;

@Builder(builderMethodName = "newBuilder")
public record HttpResponse<T>(
        int statusCode,
        T body,
        String next,
        String error,
        String reasonText,
        boolean json) implements IHttpResponse<T> {
    public static <T> HttpResponse<T> error(int status, String error) {
        return new HttpResponse<>(status, null, null, error, null, false);
    }

    public static <T> HttpResponseBuilder<T> next(String next) {
        return HttpResponse.<T>newBuilder()
                .statusCode(HttpServletResponse.SC_OK)
                .next(next);
    }

    public static <T> HttpResponseBuilder<T> ok(T body) {
        return HttpResponse.<T>newBuilder()
                .statusCode(HttpServletResponse.SC_OK)
                .body(body);
    }

    public static HttpResponse<String> ofJson(String json) {
        return ok(json).json(true).build();
    }
}
