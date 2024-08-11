package com.dev.servlet.view.base;

import com.dev.servlet.domain.User;
import com.dev.servlet.mapper.UserMapper;
import com.dev.servlet.utils.CacheUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public abstract class BaseRequest {

    protected static final String LOGIN = "login";
    protected static final String LOGOUT = "logout";
    protected static final String LOGIN_FORM = "loginForm";
    protected static final String INVALID = "invalid";
    protected static final String USER_OR_PASSWORD_INVALID = "User or password invalid.";
    protected static final String CREATE = "create";
    protected static final String LIST = "list";
    protected static final String UPDATE = "update";
    protected static final String NEW = "new";
    protected static final String EDIT = "edit";
    protected static final String DELETE = "delete";
    protected static final String PARAM = "param";
    protected static final String VALUE = "value";
    protected static final String FORWARD_PAGES_NOT_FOUND = "forward:pages/not-found.jsp";
    protected static final String FORWARD_PAGES_FORM_LOGIN = "forward:pages/formLogin.jsp";
    protected final Logger logger;
    protected final Gson gson;

    protected BaseRequest() {
        this.logger = LoggerFactory.getLogger(BaseRequest.class);
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setPrettyPrinting()
                .create();
    }

    /**
     * Return the user from cache
     *
     * @param request
     * @return {@link User}
     */
    protected Object getSessionAttribute(HttpServletRequest request, String key) {
        HttpSession session = request.getSession();
        return session.getAttribute(key);
    }

    protected void setSessionAttribute(HttpServletRequest request, String name, Object value) {
        HttpSession session = request.getSession();
        session.setAttribute(name, value);
    }

    protected String getToken(HttpServletRequest request) {
        return (String) getSessionAttribute(request, "token");
    }

    /**
     * Return the user from cache
     *
     * @param request
     * @return {@link User}
     */
    protected User getUser(HttpServletRequest request) {
        String token = getToken(request);
        return UserMapper.from(CacheUtil.getUser(token));
    }

    /**
     * Get the parameter from request
     *
     * @param request
     * @param key
     * @return
     */
    protected String getParameter(HttpServletRequest request, String key) {
        String attribute = request.getParameter(key);
        if (attribute != null && !attribute.isEmpty()) {
            return attribute.trim();
        }
        return null;
    }
}
