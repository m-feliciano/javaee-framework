package com.dev.servlet.view.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public abstract class BaseRequest {

	protected final Logger logger;
	protected final Gson gson;

	protected static final String LOGIN = "login";
	protected static final String LOGOUT = "logout";
	protected static final String LOGIN_FORM = "loginForm";
	protected static final String INVALID = "invalid";
	protected static final String USER_OR_PASSWORD_INVALID = "User or password invalid.";
	protected static final String USER_LOGGED = "userLogged";

	protected static final String ACTION = "action";
	protected static final String CREATE = "create";
	protected static final String LIST = "list";
	protected static final String UPDATE = "update";
	protected static final String NEW = "new";
	protected static final String EDIT = "edit";
	protected static final String DELETE = "delete";
	protected static final String PARAM = "param";
	protected static final String VALUE = "value";

	protected static final String FORWARD_PAGES_NOT_FOUND = "forward:pages/not-found.jsp";
	protected static final String FORWARD_PAGES_FORM_LOGIN = "forward:pages/formLogin.jsp";

	protected BaseRequest() {
		this.logger = LoggerFactory.getLogger(BaseRequest.class.getName());
		gson = new Gson();
	}
}
