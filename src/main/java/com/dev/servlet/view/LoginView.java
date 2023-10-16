package com.dev.servlet.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dev.servlet.controllers.UserController;
import com.dev.servlet.domain.User;
import com.dev.servlet.dto.UserDTO;
import com.dev.servlet.utils.PasswordUtils;
import com.dev.servlet.view.base.BaseRequest;

public class LoginView extends BaseRequest {

	private static final String REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS = "redirect:productView?action=list";

	private final UserController controller = new UserController(em);

	public LoginView() {
		super();
	}

	/**
	 * Execute the action
	 *
	 * @param req  the req
	 * @param resp the resp
	 * @return the string
	 */

	@Override
	public String execute(HttpServletRequest req, HttpServletResponse resp) {
		String request = req.getParameter(ACTION);

		return switch (request) {
		case LOGIN -> login(req, resp);
		case LOGOUT -> logout(req, resp);
		case LOGIN_FORM -> FORWARD_PAGES_FORM_LOGIN_JSP;
		default -> FORWARD_PAGES_NOT_FOUND_JSP;
		};
	}

	/**
	 * Login.
	 *
	 * @param req  the req
	 * @param resp the resp
	 * @return the path to the next page
	 */
	public String login(HttpServletRequest req, HttpServletResponse resp) {
		if (req.getParameter("sucess") != null) {
			return FORWARD_PAGES_FORM_LOGIN_JSP;
		}

		User user = new User();
		user.setLogin(req.getParameter("email"));
		user.setPassword(PasswordUtils.encrypt(req.getParameter("password")));
		user = controller.findByLogin(user);
		if (user == null) {
			req.setAttribute(INVALID, USER_OR_PASSWORD_INVALID);
			req.setAttribute("email", req.getParameter("email"));
			return FORWARD_PAGES_FORM_LOGIN_JSP;
		}

		UserDTO userDTO = new UserDTO(user);
		userDTO.setPassword(user.getPassword());
		req.getSession().setAttribute(USER_LOGGED, userDTO);

		return REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS;
	}

	/**
	 * Logout.
	 *
	 * @param req  the req
	 * @param resp the resp
	 * @return the path to login page
	 */
	public String logout(HttpServletRequest req, HttpServletResponse resp) {
		HttpSession session = req.getSession();
		session.invalidate();
		return FORWARD_PAGES_FORM_LOGIN_JSP;
	}
}
