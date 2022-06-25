package filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlets.Action;

import javax.persistence.Transient;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthController implements Filter {

    @Transient
    final Logger logger = LoggerFactory.getLogger(AuthController.class);

    /**
     * @param servletRequest  the <code>ServletRequest</code> object contains the client's request
     * @param servletResponse the <code>ServletResponse</code> object contains the filter's response
     * @param chain           the <code>FilterChain</code> for invoking the next filter or the resource
     * @throws ServletException if an I/O exception has occurred
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws ServletException {

        logger.info("Init controller filter");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String strAction = req.getParameter("action");

        String classname = null;
        String entityName = null;

        // fully qualified name do metodo a ser executado
        if (!strAction.contains("Log")) {
            int entityPos = req.getServletPath().lastIndexOf("/") + 1;
            entityName = req.getServletPath().substring(entityPos);
            classname = String.format("servlets.%s.%s", entityName, strAction);
        } else {
            classname = String.format("servlets.%s", strAction);
        }

        logger.info("classname: {}", classname);

        String path = null;
        try {
            Class<?> clazz = Class.forName(classname);
            Action action = (Action) clazz.getDeclaredConstructor().newInstance();
            path = action.execute(req, resp);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        logger.info("class: {} - entity: {} - action: {}", classname, entityName, strAction);

        String[] array;

        try {
            assert path != null;
            array = path.split(":");
        } catch (Exception e) {
            logger.warn("Error on parse url: {}", e.getMessage());
            throw new ServletException("Cannot parse url: " + path);
        }

        if (array[0].equals("forward")) {
            try {
                req.getRequestDispatcher("/WEB-INF/view/" + array[1]).forward(req, resp);
            } catch (IOException | ServletException e) {
                e.printStackTrace();
            }
        } else {
            try {
                resp.sendRedirect(array[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void destroy() {
        // TODO document why this method is empty
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO document why this method is empty
    }

}
