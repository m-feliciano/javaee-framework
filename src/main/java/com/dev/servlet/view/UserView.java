package com.dev.servlet.view;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.dev.servlet.controllers.UserController;
import com.dev.servlet.domain.User;
import com.dev.servlet.domain.enums.Perfil;
import com.dev.servlet.domain.enums.Status;
import com.dev.servlet.filter.BusinessRequest;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.utils.PasswordUtils;
import com.dev.servlet.view.base.BaseRequest;

public class UserView extends BaseRequest {

	private static final String FORWARD_PAGE_CREATE = "forward:pages/user/formCreateUser.jsp";
	private static final String FORWARD_PAGE_LIST = "forward:pages/user/formListUser.jsp";
	private static final String FORWARD_PAGE_UPDATE = "forward:pages/user/formUpdateUser.jsp";
	private static final String REDIRECT_ACTION_LIST_BY_ID = "redirect:userView?action=list&id=";

	private static final String REDIRECT_PRODUCT_ACTION_CREATE = "redirect:productView?action=create";

	private UserController controller;

	public UserView() {
		super();
	}

	public UserView(EntityManager em) {
		super();
		controller = new UserController(em);
	}

	/**
	 * Forward to create
	 * 
	 * @return
	 */
	@ResourcePath(value = NEW, forward = true)
	public String forwardRegister() {
		return FORWARD_PAGE_CREATE;
	}

	/**
	 * Redirect to Edit user.
	 *
	 * @param businessRequest
	 * @return the string
	 * @throws Exception
	 */
	@ResourcePath(value = CREATE)
	public String registerOne(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		var password = req.getParameter("password");
		var confirmPassword = req.getParameter("confirmPassword");

		if (password == null || !password.equals(confirmPassword)) {
			req.setAttribute("email", req.getParameter("email"));
			req.setAttribute("error", "password invalid");
			return FORWARD_PAGE_CREATE;
		}

		User user = new User();
		user.addPerfil(Perfil.USER);
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
	@ResourcePath(value = UPDATE)
	public String updateOne(BusinessRequest businessRequest) {
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
	@ResourcePath(value = LIST)
	public String findAll(BusinessRequest businessRequest) {
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
	@ResourcePath(value = EDIT)
	public String editOne(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		req.setAttribute("user", controller.findById(Long.parseLong(req.getParameter("id"))));
		return FORWARD_PAGE_UPDATE;
	}

	/**
	 * 
	 * @param businessRequest
	 * @return
	 */
	@ResourcePath(value = DELETE)
	public String deleteOne(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		User user = (User) req.getSession().getAttribute(USER_LOGGED);
		controller.delete(user);
		return FORWARD_PAGES_FORM_LOGIN;
	}

}
