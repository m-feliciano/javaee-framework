package filter;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlets.Action;

import javax.persistence.Transient;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthController implements Filter {

    public static final String LOGIN_SERVLET = "servlets.LoginServlet";
    @Transient
    final Logger logger = LoggerFactory.getLogger(AuthController.class);

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * build the request and response objects and pass them to the execute method.
     *
     * @param servletRequest  the <code>ServletRequest</code> object contains the client's request
     * @param servletResponse the <code>ServletResponse</code> object contains the filter's response
     * @param chain           the <code>FilterChain</code> for invoking the next filter or the resource
     * @throws ServletException if an I/O exception has occurred
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws ServletException {

        logger.info("Initializing AuthController filter");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String strAction = req.getParameter("action");
        logger.info("Action: {}", strAction);
        String classname = getClassname(req, strAction);
        logger.info("Classname: {}", classname);
        String fullpath = executeAction(req, resp, classname);
        logger.info("Fullpath: {}", fullpath);
        processRequest(req, resp, fullpath);
    }

    @Nullable
    private String executeAction(HttpServletRequest req, HttpServletResponse resp, String classname) {
        String fullpath = null;
        try {
            Class<?> clazz = Class.forName(classname);
            Action action = (Action) clazz.getDeclaredConstructor().newInstance();
            fullpath = action.execute(req, resp);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return fullpath;
    }

    /**
     * Get the classname
     *
     * @param req
     * @param strAction
     * @return String
     */
    private String getClassname(HttpServletRequest req, String strAction) {
        String classname;
        // fully qualified name do metodo a ser executado
        if (strAction.contains("Log")) {
            classname = LOGIN_SERVLET;
        } else {
            int entityPos = req.getServletPath().lastIndexOf("/") + 1;
            String entityName = req.getServletPath().substring(entityPos);
            classname = String.format("servlets.%s.%s", entityName, getServletClass(entityName));
        }
        return classname;
    }

    /**
     * Process the request and redirect to
     *
     * @param req
     * @param resp
     * @param fullpath
     * @throws ServletException
     */
    private void processRequest(HttpServletRequest req, HttpServletResponse resp, String fullpath) throws ServletException {

        if (fullpath == null) {
            logger.error("error on AuthController filter");
            fullpath = "forward:pages/not-found.jsp";
            try {
                req.getRequestDispatcher("/WEB-INF/view/" + fullpath).forward(req, resp);
            } catch (IOException e) {
                logger.error("Error on redirect: {}", e.getMessage());
                e.printStackTrace();
            }
        }

        String[] path;
        try {
            logger.info("Path: {}", fullpath);
            path = fullpath.split(":");
        } catch (Exception e) {
            logger.error("Error on parse url: {}", e.getMessage());
            throw new ServletException("Cannot parse url: " + fullpath);
        }

        String pathAction = path[0];
        String pathUrl = path[1];

        if (pathAction.equals("forward")) {
            try {
                logger.info("Forward to: {}", pathUrl);
                req.getRequestDispatcher("/WEB-INF/view/" + pathUrl).forward(req, resp);
            } catch (IOException | ServletException e) {
                logger.error("Error on forward: {}", e.getMessage());
                e.printStackTrace();
            }
        } else {
            try {
                logger.info("Redirect to: {}", pathUrl);
                resp.sendRedirect(pathUrl);
            } catch (IOException e) {
                logger.error("Error on redirect: {}", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * format the class name to be used in the classpath
     *
     * @param entityName the entity name
     * @return the servlet class
     */
    private String getServletClass(String entityName) {
        return entityName.substring(0, 1).toUpperCase() + entityName.substring(1).concat("Servlet");
    }

}
