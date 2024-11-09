package com.dev.servlet.pojo.records;

import com.dev.servlet.dto.ServiceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

// Immutable class
public record StandardRequest(HttpServletRequest servletRequest,
                              HttpServletResponse servletResponse,
                              RequestObject object) {

    public String getToken() {
        return object.token();
    }

    public String getService() {
        return object.service();
    }

    public String getAction() {
        return object.action();
    }

    public Query getQuery() {
        return object.query();
    }

    public Long getId() {
        return object.id();
    }

    //////////////////////////////////////////
    // Below are the methods from HttpServletRequest
    //////////////////////////////////////////

    public void setStatus(int status) {
        servletResponse.setStatus(status);
    }

    public void sendRedirect(String location) throws IOException {
        servletResponse.sendRedirect(location);
    }

    public void sendError(int status, String message) throws IOException {
        servletResponse.sendError(status, message);
    }

    public void sendError(int status) throws IOException {
        servletResponse.sendError(status);
    }

    public void setAttribute(String name, Object value) {
        servletRequest.setAttribute(name, value);
    }

    public void setSessionAttribute(String name, Object value) {
        HttpSession session = servletRequest.getSession();
        session.setAttribute(name, value);
    }

    public Object getSessionAttribute(String name) {
        HttpSession session = servletRequest.getSession();
        return session.getAttribute(name);
    }

    public String getParameter(String name) {
        String parameter = servletRequest.getParameter(name);
        return parameter != null ? parameter.trim() : null;
    }

    public String getRequiredParameter(String name) throws ServiceException {
        String parameter = getParameter(name);
        if (parameter == null) {
            throw new ServiceException("Parameter " + name + " is required");
        }

        return parameter.trim();
    }

    public Object getAttribute(String name) {
        return servletRequest.getAttribute(name);
    }

    public HttpSession getSession() {
        return servletRequest.getSession();
    }
}