package com.dev.servlet.filter;

import com.dev.servlet.interfaces.IRequestProcessor;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.utils.JPAUtil;

import javax.persistence.EntityManager;
import java.lang.reflect.Method;

public class ResquestProcessImp implements IRequestProcessor {

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
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Object newInstance = standardRequest.getClazz()
                    .getDeclaredConstructor(EntityManager.class)
                    .newInstance(em);
            String invoke = (String) method.invoke(newInstance, standardRequest);

            em.getTransaction().commit();
            return invoke;
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            if (em.isOpen())
                em.close();
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
                    .getDeclaredConstructor()
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
