package com.dev.servlet.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class BusinessRequest {

	private final String action;
	private final Class<?> clazz;
	private final HttpServletRequest request;
	private final HttpServletResponse response;

	public BusinessRequest(String action, Class<?> clazz, HttpServletRequest request) {
		this.action = action;
		this.clazz = clazz;
		this.request = request;
		this.response = null;
	}

	public BusinessRequest(String action, Class<?> clazz, HttpServletRequest request, HttpServletResponse response) {
		this.action = action;
		this.clazz = clazz;
		this.request = request;
		this.response = response;
	}

	public String getAction() {
		return action;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

}
