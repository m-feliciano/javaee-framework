package com.dev.servlet.filter;

import java.lang.reflect.Method;

import com.dev.servlet.interfaces.IActionProcessor;
import com.dev.servlet.interfaces.ResquestPath;

public class RequestProcessor implements IActionProcessor {

	@Override
	public String process(BusinessRequest businessRequest) {
		for (Method method : businessRequest.getClazz().getDeclaredMethods()) {
			ResquestPath annotation = method.getAnnotation(ResquestPath.class);

			if (annotation != null && annotation.value().equals(businessRequest.getAction())) {
				try {
					Object newInstance = businessRequest.getClazz().getDeclaredConstructor().newInstance();
					Object invoke = method.invoke(newInstance, businessRequest);
					return (String) invoke;
				} catch (Exception ex) {
					System.err.println(ex.getMessage());
				}
			}
		}

		return null;
	}
}
