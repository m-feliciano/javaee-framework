package com.dev.servlet.filter;

import java.lang.reflect.Method;

import javax.persistence.EntityManager;

import com.dev.servlet.interfaces.IResquestProcessor;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.utils.JPAUtil;

public class ResquestProcessImp implements IResquestProcessor {

	@Override
	public String process(BusinessRequest request) throws Exception {
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
	 * @param businessRequest
	 * @param method
	 * @return
	 * @throws Exception
	 */
	private String sessionRequest(BusinessRequest businessRequest, Method method) {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			em.getTransaction().begin();

			Object newInstance = businessRequest.getClazz()
					.getDeclaredConstructor(EntityManager.class)
					.newInstance(em);
			String invoke = (String) method.invoke(newInstance, businessRequest);

			em.getTransaction().commit();
			return invoke;
		} catch (Exception e) {
			System.err.println(e);
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
	 * @throws Exception
	 */
	private String simpleRequest(BusinessRequest request, Method method) {
		try {
			Object newInstance = request.getClazz()
					.getDeclaredConstructor()
					.newInstance();

			if (method.getParameterCount() == 0)
				return (String) method.invoke(newInstance);

			return (String) method.invoke(newInstance, request);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return null;
	}
}
