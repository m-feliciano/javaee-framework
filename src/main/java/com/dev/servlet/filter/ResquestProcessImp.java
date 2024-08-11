package com.dev.servlet.filter;

import com.dev.servlet.interfaces.IRequestProcessor;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.utils.JPAUtil;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

public class ResquestProcessImp implements IRequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ResquestProcessImp.class);
    private static final ServiceLocator serviceLocator = new ServiceLocator(JPAUtil.getEntityManager());

    /**
     * Process the request, if the request is a session request, it will use the Open Session In View 'pattern'
     * otherwise it will use the simple request (e.g forward, redirect etc)
     *
     * @param request
     * @return the next path
     */
    @Override
    public String process(StandardRequest request) {
        ResourcePath annotation;
        for (Method method : request.getClazz().getDeclaredMethods()) {
            annotation = method.getAnnotation(ResourcePath.class);

            if (annotation != null && annotation.value().equals(request.getAction())) {
                try {
                    if (annotation.forward())
                        return simpleRequest(request, method);
                    else
                        return sessionRequest(request, method);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    throw e;
                }
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
    private String sessionRequest(StandardRequest request, Method method) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        EntityManager em = serviceLocator.getEntityManager();
        try {
            em.getTransaction().begin();
            Object service = serviceLocator.getService(request.getClazz());
            String invoke = (String) method.invoke(service, request);
            em.flush();
            em.clear();
            em.getTransaction().commit();
//            throw new Exception("Test");
            return invoke;
        } catch (Exception e) {
            logger.error(e.getMessage());

            if (em.getTransaction().isActive())
                em.getTransaction().rollback();

            // Open Session In View, so we need to clear the request attributes
            HttpServletRequest httpRequest = request.getRequest();
            httpRequest.getAttributeNames().asIterator().forEachRemaining(httpRequest::removeAttribute);

            // TODO: Create a error enum to handle the error messages
            // TODO: Create a error layer and do not expose the error message to the user
            httpRequest.setAttribute("error", e.getMessage());
            // TODO: Create a error custom page to handle the error messages
            return "forward:pages/not-found.jsp";
        } finally {
//            if (em.isOpen())
//                em.close();
            stopWatch.stop();
            String debug = MessageFormat.format("Method: {0} of {1} took {2} ms",
                    request.getAction(),
                    request.getClazz().getName().substring(request.getClazz().getName().lastIndexOf(".") + 1),
                    stopWatch.getTime(TimeUnit.MILLISECONDS));

            logger.info(debug);
        }
    }

    /**
     * Simple (no session) request (e.g forward, redirect etc)
     *
     * @param request
     * @param method
     * @return
     */
    private String simpleRequest(StandardRequest request, Method method) {
        try {
            Object newInstance = request.getClazz()
                    .getConstructor()
                    .newInstance();

            if (method.getParameterCount() == 0)
                return (String) method.invoke(newInstance);

            return (String) method.invoke(newInstance, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
