package com.dev.servlet.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.dev.servlet.controllers.UserController;
import com.dev.servlet.domain.User;
import com.dev.servlet.domain.enums.Status;
import com.dev.servlet.filter.BusinessRequest;
import com.dev.servlet.interfaces.ResquestPath;
import com.dev.servlet.utils.PasswordUtils;
import com.dev.servlet.view.base.BaseRequest;

public class UserView extends BaseRequest {

	private static final String FORWARD_PAGE_CREATE = "forward:pages/user/formCreateUser.jsp";
	private static final String FORWARD_PAGE_LIST = "forward:pages/user/formListUser.jsp";
	private static final String FORWARD_PAGE_UPDATE = "forward:pages/user/formUpdateUser.jsp";
	private static final String REDIRECT_ACTION_LIST_BY_ID = "redirect:userView?action=list&id=";

	private static final String REDIRECT_PRODUCT_ACTION_CREATE = "redirect:productView?action=create";

	private final UserController controller = new UserController(em);

	public UserView() {
		super();
	}

	/**
	 * Forward to create
	 * 
	 * @param businessRequest
	 * @return
	 */
	@ResquestPath(value = NEW)
	public String forward(BusinessRequest businessRequest) {
		return FORWARD_PAGE_CREATE;
	}

	/**
	 * Redirect to Edit user.
	 *
	 * @param businessRequest
	 * @return the string
	 * @throws Exception
	 */
	@ResquestPath(value = CREATE)
	public String doCreate(BusinessRequest businessRequest) throws Exception {

		HttpServletRequest req = businessRequest.getRequest();

		var password = req.getParameter("password");
		var confirmPassword = req.getParameter("confirmPassword");

		if (password == null || !password.equals(confirmPassword)) {
			req.setAttribute("email", req.getParameter("email"));
			req.setAttribute("error", "password invalid");
			return FORWARD_PAGE_CREATE;
		}

		User user = new User();
		String email = req.getParameter("email").toLowerCase();
		user.setLogin(email);
		user = controller.find(user);

		if (user != null) {
			req.setAttribute("error", "User already exists");
			return FORWARD_PAGE_CREATE;
		}

		user = new User(email, PasswordUtils.encrypt(password));

		try {
			user.setStatus(Status.ACTIVE.getDescription());
			controller.save(user);
		} catch (IllegalArgumentException e) {
			req.setAttribute("error", e.getMessage());
			return REDIRECT_PRODUCT_ACTION_CREATE;
		}

		req.setAttribute("sucess", "sucess");
		return FORWARD_PAGES_FORM_LOGIN;
	}

	/**
	 * Update user.
	 *
	 * @param businessRequest
	 * @return the string
	 * @throws Exception
	 */
	@ResquestPath(value = UPDATE)
	public String doUpdate(BusinessRequest businessRequest) throws Exception {
		HttpServletRequest req = businessRequest.getRequest();
		HttpSession session = req.getSession();

		User user = (User) session.getAttribute(USER_LOGGED);
		user.setLogin(req.getParameter("email").toLowerCase());
		user.setImgUrl(req.getParameter("imgUrl"));
		String password = req.getParameter("password");
		user.setPassword(PasswordUtils.encrypt(password));
		controller.update(user);

		req.setAttribute("user", user);
		return REDIRECT_ACTION_LIST_BY_ID + user.getId();
	}

	/**
	 * List user by session.
	 *
	 * @param businessRequest
	 * @return the string
	 */
	@ResquestPath(value = LIST)
	public String doList(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		User user = (User) req.getSession().getAttribute(USER_LOGGED);
		req.setAttribute("user", user);
		return FORWARD_PAGE_LIST;
	}

	/**
	 * Edit user.
	 *
	 * @param businessRequest
	 * @return the string
	 */
	@ResquestPath(value = EDIT)
	public String doEdit(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		req.setAttribute("user", controller.findById(Long.parseLong(req.getParameter("id"))));
		return FORWARD_PAGE_UPDATE;
	}

	/**
	 * 
	 * @param businessRequest
	 * @return
	 */
	@ResquestPath(value = DELETE)
	public String doDelete(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		User user = (User) req.getSession().getAttribute(USER_LOGGED);
		controller.delete(user);
		return FORWARD_PAGES_FORM_LOGIN;
	}

}
