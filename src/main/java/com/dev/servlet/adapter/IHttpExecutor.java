package com.dev.servlet.adapter;

import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.domain.transfer.Request;

@FunctionalInterface
public interface IHttpExecutor<TResponse> {
    IHttpResponse<TResponse> call(Request request);
}
