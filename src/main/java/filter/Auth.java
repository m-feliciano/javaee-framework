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

//@WebFilter(urlPatterns = "/company")
public class Auth implements Filter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {

		System.out.println("Auth Filter");
		HttpServletRequest req = (HttpServletRequest) servletRequest;
		HttpServletResponse resp = (HttpServletResponse) servletResponse;

		String param = req.getParameter("action");

		HttpSession session = req.getSession();
		boolean userNotSignIn = (session.getAttribute("userLogged") == null);
		boolean filter = param.equals("Login") || param.equals("LoginForm");

		if (!filter && userNotSignIn) {
			try {
				resp.sendRedirect("company?action=LoginForm");
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
