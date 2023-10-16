package com.dev.servlet.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dev.servlet.controllers.UserController;
import com.dev.servlet.domain.User;
import com.dev.servlet.domain.enums.Status;
import com.dev.servlet.utils.PasswordUtils;
import com.dev.servlet.view.base.BaseRequest;

public class UserView extends BaseRequest {

	private static final String FORWARD_PAGES_USER_FORM_CREATE_USER_JSP = "forward:pages/user/formCreateUser.jsp";
	private static final String FORWARD_PAGES_USER_FORM_LIST_USER_JSP = "forward:pages/user/formListUser.jsp";
	private static final String FORWARD_PAGES_USER_FORM_UPDATE_USER_JSP = "forward:pages/user/formUpdateUser.jsp";

	private static final String REDIRECT_PRODUCT_ACTION_CREATE_USER = "redirect:productView?action=create";
	private static final String REDIRECT_USER_ACTION_LIST_USER_BY_ID = "redirect:userView?action=list&id=";

	private final UserController controller = new UserController(em);

	public UserView() {
		super();
	}

	@Override
	public String execute(HttpServletRequest req, HttpServletResponse resp) {
		return switch (req.getParameter(ACTION)) {
		case CREATE -> doCreate(req, resp);
		case LIST -> doList(req, resp);
		case UPDATE -> doUpdate(req, resp);
		case EDIT -> doEdit(req, resp);
		case DELETE -> doDelete(req, resp);
		case NEW -> add();
		default -> FORWARD_PAGES_NOT_FOUND_JSP;
		};
	}

	/**
	 * Redirect to Create user.
	 *
	 * @return the string
	 */
	public String add() {
		return FORWARD_PAGES_USER_FORM_CREATE_USER_JSP;
	}

	/**
	 * Redirect to Edit user.
	 *
	 * @param req  the req
	 * @param resp the resp
	 * @return the string
	 */
	public String doCreate(HttpServletRequest req, HttpServletResponse resp) {

		var password = req.getParameter("password");
		var confirmPassword = req.getParameter("confirmPassword");
		
		boolean passwordValid = password != null && password.equals(confirmPassword);
		if (!passwordValid) {
			req.setAttribute("email", req.getParameter("email"));
			req.setAttribute("error", "password invalid");
			return FORWARD_PAGES_USER_FORM_CREATE_USER_JSP;
		}

		User user = new User();
		user.setLogin(req.getParameter("email").toLowerCase());
		user = controller.find(user);

		if (user != null) {
			req.setAttribute("error", "User already exists");
			return FORWARD_PAGES_USER_FORM_CREATE_USER_JSP;
		}

		user = new User(req.getParameter("email").toLowerCase(), PasswordUtils.encrypt(req.getParameter("password")));

		try {
			user.setStatus(Status.ACTIVE.getDescription());
			controller.save(user);
		} catch (IllegalArgumentException e) {
			req.setAttribute("error", e.getMessage());
			return REDIRECT_PRODUCT_ACTION_CREATE_USER;
		}

		req.setAttribute("sucess", "Sucess");
		return FORWARD_PAGES_FORM_LOGIN_JSP;
	}

	/**
	 * Update user.
	 *
	 * @param req  the req
	 * @param resp the resp
	 * @return the string
	 */
	public String doUpdate(HttpServletRequest req, HttpServletResponse resp) {
		HttpSession session = req.getSession();

		User user = (User) session.getAttribute(USER_LOGGED);
		user.setLogin(req.getParameter("email").toLowerCase());
		user.setImgUrl(req.getParameter("imgUrl"));

		boolean passwordValid = PasswordUtils.validate(user.getPassword(),
				(String) req.getAttribute("confirmPassword"));

		if (!passwordValid) {
			req.setAttribute("user", user);
			req.setAttribute(INVALID, "user or password invalid");
			return FORWARD_PAGES_USER_FORM_LIST_USER_JSP;
		}

		user.setPassword(PasswordUtils.encrypt(req.getParameter("confirmPassword")));
		controller.update(user);

		req.setAttribute("user", user);
		return REDIRECT_USER_ACTION_LIST_USER_BY_ID + user.getId();
	}

	/**
	 * List user by session.
	 *
	 * @param req  the req
	 * @param resp the resp
	 * @return the string
	 */
	public String doList(HttpServletRequest req, HttpServletResponse resp) {
		req.setAttribute("user", req.getSession().getAttribute(USER_LOGGED));
		return FORWARD_PAGES_USER_FORM_LIST_USER_JSP;
	}

	/**
	 * Edit user.
	 *
	 * @param req  the req
	 * @param resp the resp
	 * @return the string
	 */
	public String doEdit(HttpServletRequest req, HttpServletResponse resp) {
		req.setAttribute("user", controller.findById(Long.parseLong(req.getParameter("id"))));
		return FORWARD_PAGES_USER_FORM_UPDATE_USER_JSP;
	}

	public String doDelete(HttpServletRequest req, HttpServletResponse resp) {
		User user = (User) req.getSession().getAttribute(USER_LOGGED);
		controller.delete(user);
		return FORWARD_PAGES_FORM_LOGIN_JSP;
	}

}
