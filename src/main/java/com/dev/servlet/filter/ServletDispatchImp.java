package com.dev.servlet.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dev.servlet.builders.BusinessRequestBuilder;
import com.dev.servlet.interfaces.IResquestProcessor;
import com.dev.servlet.interfaces.IServletDispatcher;

//@WebFilter(urlPatterns = "/company")
public final class ServletDispatchImp implements IServletDispatcher {

	private static final String PACKAGE = "com.dev.servlet.view.%s";

	@Override
	public void dispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String token = (String) request.getAttribute("token");
		String next = this.execute(token, request, response, this.getClassname(request));
		processResponse(request, response, next);
	}

	private String execute(String token, HttpServletRequest request, HttpServletResponse response, String classname) {
		String fullpath = null;
		try {
			Class<?> clazz = Class.forName(classname);
			BusinessRequest businessRequest = new BusinessRequestBuilder()
					.withAction(request.getParameter("action"))
					.withClazz(clazz)
					.withRequestAndResponse(request, response)
					.withToken(token)
					.build();
			IResquestProcessor processor = new ResquestProcessImp();
			return processor.process(businessRequest);
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

		if ("forward".equals(pathAction)) {
			try {
				request.getRequestDispatcher("/WEB-INF/view/" + pathUrl)
					.forward(request, response);
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
