package com.dev.servlet.filter;

import com.dev.servlet.interfaces.IServletDispatcher;
import com.dev.servlet.utils.PropertiesUtil;
import com.dev.servlet.utils.URIUtils;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

import static com.dev.servlet.utils.CryptoUtils.isValidToken;

//@WebFilter(urlPatterns = "/company")
public class Auth implements Filter {

    private static final Set<String> AUTHORIZED_ACTIONS = PropertiesUtil.getAuthorizedActions();

    private IServletDispatcher dispatcher;

    public Auth() {
        // Empty constructor
    }

    @Inject
    public void setDependencies(IServletDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * This method will check if the user is authorized to access the requested action,
     * otherwise it will redirect the user to the login currentPage.
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
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            if (!request.getServletPath().contains("view")) {
                chain.doFilter(request, response);
            } else {
                String token = (String) request.getSession().getAttribute("token");
                String action =  URIUtils.getAction(request);

                if (!isAuthorized(token, action)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.sendRedirect("view/login?action=loginForm");
                } else {
                    dispatcher.dispatch(request, response);
                }
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
        boolean anyMatch = AUTHORIZED_ACTIONS.stream().anyMatch(e -> e.equals(action));
        return anyMatch || isValidToken(token);
    }
}
