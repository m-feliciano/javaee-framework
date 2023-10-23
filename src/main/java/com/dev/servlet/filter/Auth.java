package com.dev.servlet.filter;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dev.servlet.domain.User;
import com.dev.servlet.interfaces.IResquestProcessor;
import com.dev.servlet.utils.PasswordUtils;

//@WebFilter(urlPatterns = "/company")
public class Auth implements Filter {

	private static final String PACKAGE = "com.dev.servlet.view.%s";

	private static final String FORWARD = "forward";
	private static final String WEB_INF_VIEW = "/WEB-INF/view/";
	private static final String ACTION_REQUEST = "action";

	private static final Set<String> AUTHORIZED_ACTIONS = Set.of("login", "loginForm", "new", "create");

	/**
	 * Do filter.
	 *
	 * @param servletRequest the servlet request
	 * @param response       the servlet response
	 * @param chain          the filter chain
	 * @throws IOException      Signals that an I/O exception has occurred.
	 * @throws ServletException the servlet exception
	 */
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		String strAction = request.getParameter(ACTION_REQUEST);
		boolean authorized = AUTHORIZED_ACTIONS.stream().anyMatch(strAction::equals);

		User user = (User) request.getSession().getAttribute("userLogged");
		if (authorized || (user != null && PasswordUtils.isValidToken(user.getToken()))) {
			String fullpath = executeAction(request, response, getClassname(request));
			processResponse(request, response, fullpath);
		} else {
			try {
				response.sendRedirect("loginView?action=loginForm");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String executeAction(HttpServletRequest request, HttpServletResponse response, String classname) {
		String fullpath = null;
		try {
			Class<?> clazz = Class.forName(classname);
			String action = request.getParameter(ACTION_REQUEST);
			var businessRequest = new BusinessRequest(action, clazz, request, response);

			IResquestProcessor processor = new RequestProcessor();
			String next = processor.process(businessRequest);
			return next;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return fullpath;
	}

	/**
	 * Get the class name
	 *
	 * @param request
	 * @param strAction
	 * @return String
	 */
	private String getClassname(HttpServletRequest request) {
		String classname;
		int entityPos = request.getServletPath().lastIndexOf("/") + 1;
		// fully qualified name
		String entityName = request.getServletPath().substring(entityPos);
		classname = String.format(PACKAGE, getServletClass(entityName));
		return classname;
	}

	/**
	 * Process the request and redirect to
	 *
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processResponse(HttpServletRequest request, HttpServletResponse response, String fullpath)
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
				request.getRequestDispatcher(WEB_INF_VIEW + pathUrl).forward(request, response);
			} catch (IOException | ServletException e) {
				e.printStackTrace();
			}
		} else {
			try {
				response.sendRedirect(pathUrl);
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
