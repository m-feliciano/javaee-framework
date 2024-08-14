package com.dev.servlet.filter;

import com.dev.servlet.interfaces.IRateLimiter;
import com.dev.servlet.interfaces.IRequestProcessor;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.providers.ServiceLocator;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class ResquestProcessImp implements IRequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ResquestProcessImp.class);

    @Inject
    private ServiceLocator serviceLocator;
    @Inject
    private Instance<IRateLimiter> ijRateLimit;
    private IRateLimiter rateLimit;

    public ResquestProcessImp() {
    }

    @PostConstruct
    public void init() {
        rateLimit = ijRateLimit.get();
    }

    public ResquestProcessImp(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    /**
     * Process the request, if the request is a session request, it will use the Open Session In View 'pattern'
     * otherwise it will use the simple request (e.g forward, redirect etc)
     *
     * @param request
     * @return the next path
     */
    @Override
    public Object process(StandardRequest request) throws Exception {
        if (rateLimit != null && request.token() != null) {
            while (!rateLimit.tryAcquire(request.token())) {
                Thread.sleep(501);
            }
        }

        ResourcePath annotation;
        for (Method method : request.clazz().getDeclaredMethods()) {
            annotation = method.getAnnotation(ResourcePath.class);

            if (annotation != null && annotation.value().equals(request.action())) {
                return processRequest(request, method);
            }
        }
        return null;
    }

    /**
     * Uses the Open Session In View 'pattern' to handle the session request
     *
     * @param request
     * @param method
     * @return the next path
     */
    private Object processRequest(StandardRequest request, Method method) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            Object service = ServiceLocator.getInstance().getService(request.clazz());
            Object invoke = method.invoke(service, request);
            return invoke;
        } catch (Exception e) {
            logger.error(e.getMessage());

            HttpServletRequest httpRequest = request.servletRequest();
            httpRequest.getAttributeNames().asIterator().forEachRemaining(httpRequest::removeAttribute);

            // TODO: Do not expose the errors to the user
            httpRequest.setAttribute("error", e.getMessage());
            // TODO: Create a error custom page
            return "forward:pages/not-found.jsp";
        } finally {
            // TODO implement a listener
            stopWatch.stop();
            String debug = MessageFormat.format("Method: {0} of {1} took {2} ms",
                    request.action(),
                    request.clazz().getName().substring(request.clazz().getName().lastIndexOf(".") + 1),
                    stopWatch.getTime(TimeUnit.MILLISECONDS));

            logger.info(debug);
        }
    }

    /**
     * Create the rate limit algorithm, uses the property rate.limit.algorithm to get the algorithm
     *
     * @return
     */
//    private synchronized IRateLimit createRateLimit() {
//        String enabled = PropertiesUtil.getProperty("rate.limit.enabled", "false");
//        if (!Boolean.parseBoolean(enabled)) {
//            return null;
//        }
//
//        String property = PropertiesUtil.getProperty("rate.limit.algorithm", "TokenRate");
//        String capacity = PropertiesUtil.getProperty("rate.limit.capacity", "10");
//        String window = PropertiesUtil.getProperty("rate.limit.window", "1000");
//
//        try {
//            Class<?> clazz = Class.forName("com.dev.servlet.providers." + property);
//            Constructor<?> constructor = clazz.getConstructor(long.class, long.class);
//            return (IRateLimit) constructor.newInstance(
//                    Long.parseLong(capacity), Long.parseLong(window));
//        } catch (Exception e) {
//            logger.error("Failed instantiating rate limit algorithm", e);
//            throw new RuntimeException(e);
//        }
//    }
}
