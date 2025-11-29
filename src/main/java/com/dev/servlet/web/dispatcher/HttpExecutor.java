package com.dev.servlet.web.dispatcher;

import com.dev.servlet.web.Request;
import com.dev.servlet.web.response.IHttpResponse;

@FunctionalInterface
public interface HttpExecutor<J> {
    IHttpResponse<J> send(Request request);
}
