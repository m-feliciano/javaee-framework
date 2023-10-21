package com.dev.servlet.interfaces;

import java.io.IOException;

import com.dev.servlet.filter.BusinessRequest;

public interface IActionProcessor {

	/**
	 * Process
	 *
	 * @param businessRequest
	 * @return the string
	 */
	String process(BusinessRequest businessRequest) throws IOException;

}
