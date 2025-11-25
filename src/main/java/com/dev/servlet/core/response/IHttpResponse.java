package com.dev.servlet.core.response;

public interface IHttpResponse<T> {
    int statusCode();
    T body();
    String error();
    String reasonText();
    String next();
    boolean json();
}
