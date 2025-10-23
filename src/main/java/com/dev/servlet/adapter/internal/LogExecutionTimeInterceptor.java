package com.dev.servlet.adapter.internal;

import com.dev.servlet.adapter.LogExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

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
        String methodName = context.getMethod().getName();
        String className = context.getTarget().getClass().getSuperclass().getName();

        stopWatch.start();
        try {
            log.debug("⬆️ Entering {}.{}", className, methodName);
            return context.proceed();

        } catch (Exception e) {
            stopWatch.stop();
            long time = stopWatch.getTime();
            log.error("❌ {}.{} failed [duration={}ms]", className, methodName, time, e);
            throw e;

        } finally {
            stopWatch.stop();
            long time = stopWatch.getTime();
            log.info("⬇️ {}.{} completed [duration={}ms]", className, methodName, time);
        }
    }
}
