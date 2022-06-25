package filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

//@WebFilter(urlPatterns = "/company")
public class Auth implements Filter {

    @Transient
    final Logger logger = LoggerFactory.getLogger(Auth.class);

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

        logger.info("Init filter");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String strAction = req.getParameter("action");

        HttpSession session = req.getSession();
        boolean userNotSignIn = (session.getAttribute("userLogged") == null);
        boolean filter = strAction.equals("Login") || strAction.equals("LoginForm");

        if (!filter && userNotSignIn) {
            logger.info("User redirected to login page.");
            try {
                resp.sendRedirect("product?action=LoginForm");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        chain.doFilter(servletRequest, servletResponse);

    }

    @Override
    public void destroy() {
        //TODO Auto-generated method stub
    }

    @Override
    public void init(FilterConfig filterConfig) {
        //TODO Auto-generated method stub
    }

}
