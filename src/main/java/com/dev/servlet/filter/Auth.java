package com.dev.servlet.filter;

import com.dev.servlet.interfaces.IServletDispatcher;
import com.dev.servlet.utils.PropertiesUtil;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

import static com.dev.servlet.utils.CryptoUtils.isValidToken;

//@WebFilter(urlPatterns = "/company")
public class Auth implements Filter {

    private static final Set<String> AUTHORIZED_ACTIONS = PropertiesUtil.getAuthorizedActions();

    @Inject
    private IServletDispatcher dispatcher;

    public Auth() {
    }

    public Auth(IServletDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * This method will check if the user is authorized to access the requested action,
     * otherwise it will redirect the user to the login page.
     * <p>
     * When the request has no action, it will allow the request to continue
     * because it may be a request for content like css, js, images etc.
     *
     * @param servletRequest  the servlet request
     * @param servletResponse the servlet response
     * @param chain           the filter chain
     * @since 1.0
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String action = request.getParameter("action");
        String token = (String) request.getSession().getAttribute("token");
        try {
            if (action == null || !isAuthorized(token, action)) {
                if (action == null) {
                    chain.doFilter(request, response);
                } else {
                    response.sendRedirect("view/login?action=loginForm");
                }
            } else {
                dispatcher.dispatch(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return true if the user token is valid or the action is authorized
     *
     * @param token
     * @param action
     * @return boolean
     */
    private boolean isAuthorized(String token, String action) {
        boolean anyMatch = AUTHORIZED_ACTIONS.stream().anyMatch(action::equals);
        return anyMatch || isValidToken(token);
    }
}
