package com.servletstack.adapter.in.web.dispatcher.impl;

import com.servletstack.adapter.in.web.dispatcher.HttpExecutor;
import com.servletstack.adapter.in.web.dispatcher.IServletDispatcher;
import com.servletstack.adapter.in.web.dto.IHttpResponse;
import com.servletstack.adapter.in.web.dto.Request;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestScoped
public class ServletDispatcherImpl implements IServletDispatcher {

    @Inject
    private HttpExecutor executor;

    @Override
    @Interceptors({LogExecutionTimeInterceptor.class})
    public IHttpResponse<?> dispatch(Request request) {
        log.debug("Dispatching request to HttpExecutor: {}", executor.getClass().getSimpleName());
        IHttpResponse<?> sent = executor.send(request);
        log.debug("Request dispatched successfully.");
        return sent;
    }
}

