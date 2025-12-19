package com.dev.servlet.adapter.in.web.dto;

public interface IHttpResponse<T> {
    int statusCode();
    T body();
    String error();
    String reasonText();
    String next();
}
