package com.dev.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dev.servlet.view.interfaces.IAction;

public class AuthController implements Filter {

	public static final String PACKAGE = "com.dev.servlet.view.%s";

	public static final String FORWARD = "forward";
	public static final String WEB_INF_VIEW = "/WEB-INF/view/";
	public static final String ACTION_REQUEST = "action";

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods. build the request and response objects and pass them to the execute
	 * method.
	 *
	 * @param servletRequest  the <code>ServletRequest</code> object contains the
	 *                        client's request
	 * @param servletResponse the <code>ServletResponse</code> object contains the
	 *                        filter's response
	 * @param chain           the <code>FilterChain</code> for invoking the next
	 *                        filter or the resource
	 * @throws ServletException if an I/O exception has occurred
	 * @throws IOException 
	 */
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws ServletException, IOException {

		HttpServletRequest req = (HttpServletRequest) servletRequest;
		HttpServletResponse resp = (HttpServletResponse) servletResponse;

		String strAction = req.getParameter(ACTION_REQUEST);
		String classname = getClassname(req, strAction);
		String fullpath = executeAction(req, resp, classname);
		processRequest(req, resp, fullpath);
	}

	private String executeAction(HttpServletRequest req, HttpServletResponse resp, String classname) {
		String fullpath = null;
		try {
			Class<?> clazz = Class.forName(classname);
			IAction action = (IAction) clazz.getDeclaredConstructor().newInstance();
			fullpath = action.execute(req, resp);
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
	private String getClassname(HttpServletRequest req, String strAction) {
		String classname;
		int entityPos = req.getServletPath().lastIndexOf("/") + 1;
		// fully qualified name do metodo a ser executado
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
	private void processRequest(HttpServletRequest req, HttpServletResponse resp, String fullpath)
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
	 * @return classname
	 */
	private String getServletClass(String entityName) {
		return entityName.substring(0, 1).toUpperCase() + entityName.substring(1);
	}

}
