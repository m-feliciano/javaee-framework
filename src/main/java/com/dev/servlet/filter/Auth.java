package com.dev.servlet.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dev.servlet.domain.User;
import com.dev.servlet.interfaces.IActionProcessor;
import com.dev.servlet.utils.PasswordUtils;

//@WebFilter(urlPatterns = "/company")
public class Auth implements Filter {

	private static final String PACKAGE = "com.dev.servlet.view.%s";

	private static final String FORWARD = "forward";
	private static final String WEB_INF_VIEW = "/WEB-INF/view/";
	private static final String ACTION_REQUEST = "action";

	private static final List<String> AUTHORIZED_ACTIONS = List.of("login", "loginForm", "new", "create");

	/**
	 * Do filter.
	 *
	 * @param servletRequest  the servlet request
	 * @param servletResponse the servlet response
	 * @param chain           the filter chain
	 * @throws IOException      Signals that an I/O exception has occurred.
	 * @throws ServletException the servlet exception
	 */
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) servletRequest;
		HttpServletResponse resp = (HttpServletResponse) servletResponse;

		String strAction = req.getParameter(ACTION_REQUEST);
		boolean authorized = AUTHORIZED_ACTIONS.stream().anyMatch(strAction::equals);

		User user = (User) req.getSession().getAttribute("userLogged");
		if (authorized || (user != null && PasswordUtils.isValidToken(user.getToken()))) {
			String classname = getClassname(req);
			String fullpath = executeAction(req, resp, classname);
			processResponse(req, resp, fullpath);
		} else {
			try {
				((HttpServletResponse) servletResponse).sendRedirect("loginView?action=loginForm");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String executeAction(HttpServletRequest req, HttpServletResponse resp, String classname) {
		String fullpath = null;
		try {
			Class<?> clazz = Class.forName(classname);
			String attribute = req.getParameter(ACTION_REQUEST);
			BusinessRequest request = new BusinessRequest(attribute, clazz, req, resp);

			IActionProcessor processor = new RequestProcessor();
			String next = processor.process(request);
			return next;

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return fullpath;
	}

	/**
	 * Get the classname
	 *
	 * @param req
	 * @param strAction
	 * @return String
	 */
	private String getClassname(HttpServletRequest req) {
		String classname;
		int entityPos = req.getServletPath().lastIndexOf("/") + 1;
		// fully qualified name
		String entityName = req.getServletPath().substring(entityPos);
		classname = String.format(PACKAGE, getServletClass(entityName));
		return classname;
	}

	/**
	 * Process the request and redirect to
	 *
	 * @param req
	 * @param resp
	 * @param fullpath
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processResponse(HttpServletRequest req, HttpServletResponse resp, String fullpath)
			throws ServletException, IOException {
		if (fullpath == null) {
			fullpath = "forward:pages/not-found.jsp";
		}

		String[] path;
		try {

			path = fullpath.split(":");
		} catch (Exception e) {
			throw new ServletException("cannot parse url: {}" + fullpath);
		}

		String pathAction = path[0];
		String pathUrl = path[1];

		if (FORWARD.equals(pathAction)) {
			try {
				req.getRequestDispatcher(WEB_INF_VIEW + pathUrl).forward(req, resp);
			} catch (IOException | ServletException e) {
				e.printStackTrace();
			}
		} else {
			try {
				resp.sendRedirect(pathUrl);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * format the class name to be used in the classpath
	 *
	 * @param entityName
	 * @return
	 */
	private String getServletClass(String entityName) {
		return entityName.substring(0, 1).toUpperCase() + entityName.substring(1);
	}

}
