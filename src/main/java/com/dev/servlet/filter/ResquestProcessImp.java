package com.dev.servlet.filter;

import com.dev.servlet.interfaces.IRequestProcessor;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.utils.JPAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.lang.reflect.Method;

public class ResquestProcessImp implements IRequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ResquestProcessImp.class);
    private static final ServiceLocator serviceLocator = new ServiceLocator(JPAUtil.getEntityManager());

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
     * Open a managed session to proceed with the request
     *
     * @param standardRequest
     * @param method
     * @return the next path
     */
    private String sessionRequest(StandardRequest standardRequest, Method method) {
        EntityManager em = serviceLocator.getEntityManager();
        try {
            em.getTransaction().begin();
            Object service = serviceLocator.getService(standardRequest.getClazz());
            String invoke = (String) method.invoke(service, standardRequest);
            em.getTransaction().commit();
            return invoke;
        } catch (Exception e) {
            logger.error(e.getMessage());
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
//            if (em.isOpen())
//                em.close();
        }

        return null;
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
