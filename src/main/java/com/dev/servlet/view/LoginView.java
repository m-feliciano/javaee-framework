package com.dev.servlet.view;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.dev.servlet.controllers.UserController;
import com.dev.servlet.domain.User;
import com.dev.servlet.filter.BusinessRequest;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.utils.CacheUtil;
import com.dev.servlet.utils.PasswordUtils;
import com.dev.servlet.view.base.BaseRequest;

public class LoginView extends BaseRequest {

	private static final String REDIRECT_PRODUCT_ACTION_LIST_ALL = "redirect:productView?action=list";

	private UserController controller;

	public LoginView() {
		super();
	}

	public LoginView(EntityManager em) {
		super();
		controller = new UserController(em);
	}

	/**
	 * Forward
	 *
	 * @return the next path
	 */
	@ResourcePath(value = LOGIN_FORM, forward = true)
	public String forwardLogin() {
		return FORWARD_PAGES_FORM_LOGIN;
	}

	/**
	 * Login.
	 *
	 * @param businessRequest
	 * @return the next path
	 * @throws Exception
	 */
	@ResourcePath(value = LOGIN)
	public String login(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		if (req.getParameter("sucess") != null) {
			return FORWARD_PAGES_FORM_LOGIN;
		}

		User user = new User();
		user.setLogin(req.getParameter("email"));
		user.setPassword(PasswordUtils.encrypt(req.getParameter("password")));
		user = controller.findByLogin(user);
		if (user == null) {
			req.setAttribute(INVALID, USER_OR_PASSWORD_INVALID);
			req.setAttribute("email", req.getParameter("email"));
			return FORWARD_PAGES_FORM_LOGIN;
		}

		req.getSession().setAttribute("token", PasswordUtils.generateToken(user));
		return REDIRECT_PRODUCT_ACTION_LIST_ALL;
	}

	/**
	 * Logout.
	 *
	 * @param businessRequest
	 * @return the next path
	 */
	@ResourcePath(value = LOGOUT)
	public String logout(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		HttpSession session = req.getSession();
		String token = (String) session.getAttribute("token");
		CacheUtil.clearToken(token);
		session.invalidate();
		return this.forwardLogin();
	}
}
