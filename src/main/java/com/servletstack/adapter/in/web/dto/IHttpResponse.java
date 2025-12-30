package com.servletstack.adapter.in.web.dto;

public interface IHttpResponse<T> extends java.io.Serializable {
    int statusCode();
    T body();
    String error();
    String reasonText();
    String next();
}
