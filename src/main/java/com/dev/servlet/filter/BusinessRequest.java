package com.dev.servlet.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class BusinessRequest {

	private HttpServletRequest request;
	private HttpServletResponse response;

	private String action;
	private Class<?> clazz;
	private String token;

	public BusinessRequest() {
	}

	public BusinessRequest(String token, Class<?> clazz, String action) {
		this.token = token;
		this.clazz = clazz;
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String getToken() {
		return token;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

}
