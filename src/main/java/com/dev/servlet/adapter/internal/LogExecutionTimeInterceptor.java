package com.dev.servlet.adapter.internal;

import com.dev.servlet.adapter.LogExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.MDC;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Slf4j
@Interceptor
@LogExecutionTime
public class LogExecutionTimeInterceptor {

    @AroundInvoke
    public Object logMethodExecutionTime(InvocationContext context) throws Exception {
        StopWatch stopWatch = new StopWatch();
        String correlationId = MDC.get("correlationId");
        String methodName = context.getMethod().getName();
        String className = context.getTarget().getClass().getSuperclass().getName();

        stopWatch.start();
        try {
            log.debug("⬆️ Entering {}.{} [cid={}]", className, methodName, correlationId);
            return context.proceed();

        } catch (Exception e) {
            stopWatch.stop();
            long time = stopWatch.getTime();
            log.error("❌ {}.{} failed [duration={}ms, cid={}] - {}", className, methodName, time, correlationId, e.getMessage());
            throw e;

        } finally {
            stopWatch.stop();
            long time = stopWatch.getTime();
            log.info("⬇️ {}.{} completed [duration={}ms, cid={}]", className, methodName, time, correlationId);
        }
    }
}
