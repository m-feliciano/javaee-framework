package com.dev.servlet.interfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IServletDispatcher {

	void dispatch(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception;
}
