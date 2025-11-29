package com.dev.servlet.web.response;

public interface IHttpResponse<T> {
    int statusCode();
    T body();
    String error();
    String reasonText();
    String next();
    boolean json();
}
