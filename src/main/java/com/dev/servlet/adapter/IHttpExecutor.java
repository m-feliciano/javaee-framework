package com.dev.servlet.adapter;
import com.dev.servlet.domain.transfer.request.Request;
import com.dev.servlet.domain.transfer.response.IHttpResponse;

@FunctionalInterface
public interface IHttpExecutor<TResponse> {
    IHttpResponse<TResponse> call(Request request);
}
