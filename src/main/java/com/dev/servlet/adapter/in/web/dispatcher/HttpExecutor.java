package com.dev.servlet.adapter.in.web.dispatcher;

import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.Request;

@FunctionalInterface
public interface HttpExecutor<J> {
    IHttpResponse<J> send(Request request);
}
