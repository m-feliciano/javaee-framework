package filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@WebFilter(urlPatterns = "/company")
public class Auth implements Filter {

	final Logger logger = LoggerFactory.getLogger(Auth.class);

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {

		logger.info("Init filter");
		HttpServletRequest req = (HttpServletRequest) servletRequest;
		HttpServletResponse resp = (HttpServletResponse) servletResponse;

		String param = req.getParameter("action");

		HttpSession session = req.getSession();
		boolean userNotSignIn = (session.getAttribute("userLogged") == null);
		boolean filter = param.equals("Login") || param.equals("LoginForm");

		if (!filter && userNotSignIn) {
			logger.info("User redirected to login page.");
			try {
				resp.sendRedirect("product?action=LoginForm");
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		chain.doFilter(servletRequest, servletResponse);

	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

}
