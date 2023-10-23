package com.dev.servlet.interfaces;

import com.dev.servlet.filter.BusinessRequest;

public interface IResquestProcessor {

	/**
	 * Process
	 *
	 * @param businessRequest
	 * @return the string
	 */
	String process(BusinessRequest businessRequest) throws Exception;

}
