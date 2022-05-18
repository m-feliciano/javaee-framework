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

import crud.Action;

//@WebFilter(urlPatterns = "/company")
public class AuthController implements Filter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {

		System.out.println("AuthController Filter");
		HttpServletRequest req = (HttpServletRequest) servletRequest;
		HttpServletResponse resp = (HttpServletResponse) servletResponse;

		String param = req.getParameter("action");
		String classname = null;
		if(!param.contains("Log")) {
			int entityPos = req.getServletPath().lastIndexOf("/") + 1;
			String entityName = req.getServletPath().substring(entityPos);
			classname = "crud." + entityName + "." + param;
			
			System.out.println(classname + "crud." + entityName + "." + param);
		} else {
			classname = "crud." + param;
		}

		String path = null;
		try {
			Class<?> clazz = Class.forName(classname);
			Action action = (Action) clazz.getDeclaredConstructor().newInstance();
			path = action.doService(req, resp);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		String[] array = path.split(":");

		if (array[0].equals("forward")) {
			try {
				req.getRequestDispatcher("/WEB-INF/view/" + array[1]).forward(req, resp);
			} catch (IOException | ServletException e) {
				e.printStackTrace();
			}
		} else {
			try {
				resp.sendRedirect(array[1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

}
