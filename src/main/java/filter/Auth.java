package filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

//@WebFilter(urlPatterns = "/company")
public class Auth implements Filter {
    private final Logger logger = LoggerFactory.getLogger(Auth.class);
    private static final List<String> AUTHORIZED_ACTIONS = List.of("Login", "LoginForm", "NewUser", "CreateUser");

    /**
     * Do filter.
     *
     * @param servletRequest  the servlet request
     * @param servletResponse the servlet response
     * @param chain           the filter chain
     * @throws IOException      Signals that an I/O exception has occurred.
     * @throws ServletException the servlet exception
     */

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        logger.info("Initializing Auth filter");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String strAction = req.getParameter("action");

        HttpSession session = req.getSession();
        boolean userNotSignIn = (session.getAttribute("userLogged") == null);

        logger.info("User: {}", !userNotSignIn ? session.getAttribute("userLogged").toString() : "not logged");

        boolean filter = AUTHORIZED_ACTIONS.stream().anyMatch(strAction::equals);

        if (!filter && userNotSignIn) {
            try {
                logger.warn("Redirecting user to login page.");
                resp.sendRedirect("product?action=LoginForm");
                return;
            } catch (IOException e) {
                logger.error("Error redirecting to login page.");
                e.printStackTrace();
            }
        }
        chain.doFilter(servletRequest, servletResponse);
    }

}
