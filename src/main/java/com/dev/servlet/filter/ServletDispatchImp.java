package com.dev.servlet.filter;

import com.dev.servlet.builders.RequestBuilder;
import com.dev.servlet.interfaces.IRequestProcessor;
import com.dev.servlet.interfaces.IServletDispatcher;
import com.dev.servlet.utils.URIUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
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
@Singleton
public final class ServletDispatchImp implements IServletDispatcher {

    private IRequestProcessor processor;

    public ServletDispatchImp() {
        // Empty constructor
    }

    @Inject
    public void setDependencies(IRequestProcessor processor) {
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
        String next = (String) this.execute(token, request, response, URIUtils.getClassName(request));
        processResponse(request, response, next);
    }

    private Object execute(String token, HttpServletRequest httpRequest, HttpServletResponse htttpResponse, String classname) throws Exception {
        Class<?> clazz = Class.forName(classname);
        StandardRequest request = RequestBuilder.builder()
                .clazz(clazz)
                .request(httpRequest)
                .response(htttpResponse)
                .token(token)
                .pagination()
                .build();

        Object result = processor.process(request);
        return result;
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
            // TODO: Create a error custom currentPage
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
}
