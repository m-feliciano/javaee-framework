package com.dev.servlet.filter;

import com.dev.servlet.builders.BusinessRequest;
import com.dev.servlet.interfaces.IRequestProcessor;
import com.dev.servlet.interfaces.IServletDispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@WebFilter(urlPatterns = "/company")
public final class ServletDispatchImp implements IServletDispatcher {

    private static final String PACKAGE = "com.dev.servlet.view.%s";
    private static final IRequestProcessor processor = new ResquestProcessImp();

    @Override
    public void dispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String token = (String) request.getSession().getAttribute("token");
        String next = this.execute(token, request, response, this.getClassName(request));
        processResponse(request, response, next);
    }

    private String execute(String token, HttpServletRequest httpRequest, HttpServletResponse htttpResponse, String classname) throws Exception {
        Class<?> clazz = Class.forName(classname);
        String action = httpRequest.getParameter("action");

        StandardRequest request = BusinessRequest.builder()
                .action(action)
                .clazz(clazz)
                .request(httpRequest)
                .response(htttpResponse)
                .token(token)
                .build();

        return processor.process(request);
    }

    /**
     * Get the class name
     *
     * @param request
     * @return String
     */
    private String getClassName(HttpServletRequest request) {
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
     */
    private void processResponse(HttpServletRequest request, HttpServletResponse response, String fullpath)
            throws ServletException {
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

        try {
            if ("forward".equals(pathAction)) {
                request.getRequestDispatcher("/WEB-INF/view/" + pathUrl).forward(request, response);
            } else {
                response.sendRedirect(pathUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
