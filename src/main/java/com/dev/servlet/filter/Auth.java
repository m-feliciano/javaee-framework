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

//@WebFilter(urlPatterns = "/company")
public class Auth implements Filter {

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
//        StopWatch sw = new StopWatch();
//        sw.start();

		HttpServletRequest req = (HttpServletRequest) servletRequest;

		String strAction = req.getParameter("action");

		boolean logged = (req.getSession().getAttribute("userLogged") != null);
		boolean authorized = AUTHORIZED_ACTIONS.stream().anyMatch(strAction::equals);

		if (authorized || logged) {
			chain.doFilter(servletRequest, servletResponse);
		} else {
			try {
				((HttpServletResponse) servletResponse).sendRedirect("loginView?action=loginForm");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

//        sw.stop();
//        logger.info("Auth filter execution time: {}ms", sw.getTime());
	}

}
