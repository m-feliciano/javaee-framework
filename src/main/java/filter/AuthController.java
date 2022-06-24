package filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlets.Action;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthController implements Filter {

	final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {

		logger.info("Init controller filter");
		HttpServletRequest req = (HttpServletRequest) servletRequest;
		HttpServletResponse resp = (HttpServletResponse) servletResponse;

		String action = req.getParameter("action");

		String classname = null;
		String entityName = null;

		// fully qualified name do metodo a ser executado
		if (!action.contains("Log")) {
			int entityPos = req.getServletPath().lastIndexOf("/") + 1;
			entityName = req.getServletPath().substring(entityPos);
			classname = String.format("servlets.%s.%s", entityName, action);
		} else {
			classname = String.format("servlets.%s", action);
		}

		logger.info("classname: {}", classname);

		String path = null;
		try {
			Class<?> clazz = Class.forName(classname);
			Action actionMethod = (Action) clazz.getDeclaredConstructor().newInstance();
			path = actionMethod.doService(req, resp);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		logger.info("{} - {} - {}", classname, entityName, action);

		String[] array;

		try {
			assert path != null;
			array = path.split(":");
		} catch (Exception e) {
			logger.warn("Error on parse url: {}", e.getMessage());
			throw new ServletException("Cannot parse url: " + path);
		}

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
