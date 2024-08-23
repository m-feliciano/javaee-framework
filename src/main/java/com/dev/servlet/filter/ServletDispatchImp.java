package com.dev.servlet.filter;

import com.dev.servlet.builders.BusinessRequest;
import com.dev.servlet.interfaces.IRequestProcessor;
import com.dev.servlet.interfaces.IServletDispatcher;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class is used to dispatch the request to the appropriate servlet
 *
 * @since 1.0.0
 */
//@WebFilter(urlPatterns = "/company")
@ApplicationScoped
public final class ServletDispatchImp implements IServletDispatcher {

    private static final String PACKAGE = "com.dev.servlet.business.%s";

    @Inject
    private IRequestProcessor processor;

    public ServletDispatchImp() {
    }

    public ServletDispatchImp(IRequestProcessor processor) {
        this.processor = processor;
    }

    /**
     * This method is used to dispatch the request to the appropriate servlet,
     * it will not return anything or do any business logic
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @Override
    public void dispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String token = (String) request.getSession().getAttribute("token");
        String next = (String) this.execute(token, request, response, this.getClassName(request));
        processResponse(request, response, next);
    }

    private Object execute(String token, HttpServletRequest httpRequest, HttpServletResponse htttpResponse, String classname) throws Exception {
        Class<?> clazz = Class.forName(classname);
        String action = httpRequest.getParameter("action");

        StandardRequest request = BusinessRequest.builder()
                .action(action)
                .clazz(clazz)
                .request(httpRequest)
                .response(htttpResponse)
                .token(token)
                .build();

        Object result = processor.process(request);
        return result;
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
        if (response.getStatus() == HttpServletResponse.SC_NOT_FOUND
                || response.getStatus() == HttpServletResponse.SC_FORBIDDEN
                || response.getStatus() == HttpServletResponse.SC_BAD_REQUEST) {
            // TODO: Create a error custom page
            fullpath = "forward:pages/not-found.jsp";
        }

        if (fullpath == null) {
            // We don't have a next chain, so we return the message that the service has set
            return;
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
            throw new ServletException("Error processing request: " + e.getMessage());
        }
    }

    /**
     * format the class name to be used in the classpath
     *
     * @param entityName
     * @return
     */
    private String getServletClass(String entityName) {
        return entityName.substring(0, 1).toUpperCase() + entityName.substring(1) + "Business";
    }
}
