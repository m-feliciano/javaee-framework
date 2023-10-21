package com.dev.servlet.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.dev.servlet.controllers.UserController;
import com.dev.servlet.domain.User;
import com.dev.servlet.filter.BusinessRequest;
import com.dev.servlet.interfaces.ResquestPath;
import com.dev.servlet.utils.CacheUtil;
import com.dev.servlet.utils.PasswordUtils;
import com.dev.servlet.view.base.BaseRequest;

public class LoginView extends BaseRequest {

	private static final String REDIRECT_PRODUCT_ACTION_LIST_ALL = "redirect:productView?action=list";

	private final UserController controller = new UserController(em);

	public LoginView() {
		super();
	}

	/**
	 * Login.
	 *
	 * @param businessRequest
	 * @return the next path
	 * @throws Exception
	 */
	@ResquestPath(value = LOGIN)
	public String login(BusinessRequest businessRequest) throws Exception {
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

		User userDTO = new User();
		userDTO.setId(user.getId());
		userDTO.setToken(PasswordUtils.getNewToken());
		req.getSession().setAttribute(USER_LOGGED, userDTO);

		return REDIRECT_PRODUCT_ACTION_LIST_ALL;
	}

	/**
	 * Logout.
	 *
	 * @param businessRequest
	 * @return the next path
	 */
	@ResquestPath(value = LOGIN_FORM)
	public String redirectLogin(BusinessRequest businessRequest) {
		return FORWARD_PAGES_FORM_LOGIN;
	}

	/**
	 * Logout.
	 *
	 * @param businessRequest
	 * @return the next path
	 */
	@ResquestPath(value = LOGOUT)
	public String logout(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		HttpSession session = req.getSession();
		User userDto = (User) session.getAttribute(USER_LOGGED);
		CacheUtil.removeToken(userDto.getToken());
		session.invalidate();
		return FORWARD_PAGES_FORM_LOGIN;
	}
}
