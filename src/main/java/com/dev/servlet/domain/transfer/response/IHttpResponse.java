package com.dev.servlet.domain.transfer.response;

public interface IHttpResponse<TResponse> {
    int statusCode();
    TResponse body();
    String error();
    String reasonText();
    String next();
}
