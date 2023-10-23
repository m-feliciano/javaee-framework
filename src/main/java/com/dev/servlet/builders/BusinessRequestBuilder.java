package com.dev.servlet.builders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dev.servlet.filter.BusinessRequest;

public class BusinessRequestBuilder {

	private BusinessRequest businessRequest;

	public BusinessRequestBuilder() {
		this.businessRequest = new BusinessRequest();
	}

	public BusinessRequestBuilder withToken(String token) {
		businessRequest.setToken(token);
		return this;
	}

	public BusinessRequestBuilder withClazz(Class<?> clazz) {
		businessRequest.setClazz(clazz);
		return this;
	}

	public BusinessRequestBuilder withAction(String action) {
		businessRequest.setAction(action);
		return this;
	}

	public BusinessRequestBuilder withRequest(HttpServletRequest request) {
		businessRequest.setRequest(request);
		return this;
	}

	public BusinessRequestBuilder withResponse(HttpServletResponse response) {
		businessRequest.setResponse(response);
		return this;
	}

	public BusinessRequestBuilder withRequestAndResponse(
			HttpServletRequest request, HttpServletResponse response) {
		this.withRequest(request);
		this.withResponse(response);
		return this;
	}

	public BusinessRequest build() {
		return this.businessRequest;
	}

}
