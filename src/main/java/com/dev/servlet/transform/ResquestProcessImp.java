package com.dev.servlet.transform;

import com.dev.servlet.interfaces.IRateLimiter;
import com.dev.servlet.interfaces.IRequestProcessor;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.pojo.records.StandardRequest;
import com.dev.servlet.providers.ServiceLocator;
import com.dev.servlet.utils.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * This class is used to process the request
 *
 * @since 1.0.0
 */
@Singleton
public class ResquestProcessImp implements IRequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ResquestProcessImp.class);
    private IRateLimiter rateLimit;
    private final boolean rateLimitActive;

    public ResquestProcessImp() {
        rateLimitActive = PropertiesUtil.isRateLimitEnabled();
    }

    @Inject
    public void setDependencies(IRateLimiter ijRateLimit) {
        this.rateLimit = ijRateLimit;
    }

    /**
     * Process the request, it checks if it has to apply rate limit and then process the request
     *
     * @param request StandardRequest
     * @return the next path
     */
    @Override
    public Object process(StandardRequest request) throws Exception {
        if (rateLimitActive && !rateLimit.acquire()) {
            request.servletResponse().sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Request limit exceeded");
            return null;
        }

        return processRequest(request);
    }

    /**
     * Process the request, it uses service locator to get the service and invoke the method
     *
     * @param request
     * @return the next path
     */
    private Object processRequest(StandardRequest request) {
        try {
            Object service = ServiceLocator.getInstance().getService(request.service());
            if (service == null) {
                return "forward:pages/not-found.jsp";
            }

            Method method = null;
            for (Method methodObject : service.getClass().getDeclaredMethods()) {
                ResourcePath annotation = methodObject.getAnnotation(ResourcePath.class);

                if (annotation != null && annotation.value().equals(request.action())) {
                    method = methodObject;
                    break;
                }
            }

            if (method == null) {
                return "forward:pages/not-found.jsp";
            }

            Object invoked = method.invoke(service, request);
            return invoked;
        } catch (Exception e) {
            logger.error("ERROR: {}", (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));

            try {
                request.servletResponse().sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error. Contact support");
            } catch (Exception ignored) {
            } finally {
                HttpServletRequest httpRequest = request.servletRequest();
                httpRequest.getAttributeNames().asIterator().forEachRemaining(httpRequest::removeAttribute);
            }
        }

        return null;
    }
}
