package com.dev.servlet.filter;

import com.dev.servlet.interfaces.IServletDispatcher;
import com.dev.servlet.utils.CryptoUtils;
import com.dev.servlet.utils.PropertiesUtil;
import com.dev.servlet.utils.URIUtils;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

//@WebFilter(urlPatterns = "/company")
public class Auth implements Filter {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(Auth.class);

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
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String token = (String) request.getSession().getAttribute("token");
        String service = URIUtils.service(request);

        if (isAuthorizedAction(service) || isValidToken(token)) {
            try {
                dispatcher.dispatch(request, response);
            } catch (Exception e) {
                LOGGER.error("Error while dispatching the request", e);
            }
        } else {
            LOGGER.warn("Unauthorized access to the service: {}, redirecting to login page", service);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("/view/login/loginForm");
        }
    }

    /**
     * Check if the token is valid.
     *
     * @param token
     * @return boolean
     */
    private boolean isValidToken(String token) {
        return token != null && CryptoUtils.verifyToken(token);
    }

    /**
     * Return true if the user is authorized to access the requested action.
     *
     * @param action
     * @return boolean
     */
    private boolean isAuthorizedAction(String action) {
        return action != null && AUTHORIZED_ACTIONS.contains(action);
    }
}
