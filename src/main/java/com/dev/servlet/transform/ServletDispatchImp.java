package com.dev.servlet.transform;

import com.dev.servlet.builders.RequestBuilder;
import com.dev.servlet.interfaces.IRequestProcessor;
import com.dev.servlet.interfaces.IServletDispatcher;
import com.dev.servlet.pojo.records.StandardRequest;

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
        String next = (String) this.execute(token, request, response);
        processResponse(request, response, next);
    }

    private Object execute(String token, HttpServletRequest httpRequest, HttpServletResponse htttpResponse) throws Exception {
        StandardRequest request = RequestBuilder.builder()
                .request(httpRequest)
                .response(htttpResponse)
                .token(token)
                .pagination()
                .build();

        Object next = processor.process(request);
        return next;
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
            if ("forward".equalsIgnoreCase(pathAction)) {
                request.getRequestDispatcher("/WEB-INF/view/" + pathUrl).forward(request, response);
            } else {
                response.sendRedirect(pathUrl);
            }
        } catch (IOException e) {
            throw new ServletException("Error processing request: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
        }
    }
}
