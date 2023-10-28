package com.dev.servlet.filter;

import static com.dev.servlet.utils.CryptoUtils.isValidToken;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dev.servlet.interfaces.IServletDispatcher;

//@WebFilter(urlPatterns = "/company")
public class Auth implements Filter {

	private static final Set<String> AUTHORIZED_ACTIONS = Set.of("login", "loginForm", "new", "create");

	/**
	 * Auth filter
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

		String action = request.getParameter("action");
		String token = (String) request.getSession().getAttribute("token");
		try {
			if (action == null || !isAuthorized(token, action)) {
				response.sendRedirect("loginView?action=loginForm");
			} else {
				IServletDispatcher dispatcher = new ServletDispatchImp();
				dispatcher.dispatch(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return true if the user token is valid or the action is authorized
	 *
	 * @param user
	 * @param action
	 * @return
	 */
	private boolean isAuthorized(String token, String action) {
		boolean anyMatch = AUTHORIZED_ACTIONS.stream().anyMatch(action::equals);
		return anyMatch || isValidToken(token);
	}
}
