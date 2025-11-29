package com.dev.servlet.web.dispatcher;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

@Slf4j
@Interceptor
@LogExecutionTime
public class LogExecutionTimeInterceptor {

    @AroundInvoke
    public Object logMethodExecutionTime(InvocationContext context) throws Exception {
        StopWatch stopWatch = new StopWatch();
        String methodName = context.getMethod().getName();
        String className = context.getTarget().getClass().getSuperclass().getName();
        stopWatch.start();
        try {
            return context.proceed();
        } finally {
            stopWatch.stop();
            log.debug("{}.{} completed [duration={}ms]", className, methodName, stopWatch.getTime());
        }
    }
}
