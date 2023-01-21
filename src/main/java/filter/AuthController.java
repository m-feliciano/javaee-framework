package filter;

import org.apache.commons.lang3.time.StopWatch;
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
    public static final String FORWARD = "forward";
    public static final String REDIRECT_TO = "Redirect to: {}";
    public static final String ERROR_ON_REDIRECT = "Error on redirect: {}";
    public static final String ERROR_ON_FORWARD = "Error on forward: {}";
    public static final String FORWARD_TO = "Forward to: {}";
    public static final String WEB_INF_VIEW = "/WEB-INF/view/";
    public static final String SERVLET = "Servlet";
    public static final String PATH = "Path: {}";
    public static final String ERROR_ON_PARSE_URL = "Error on parse url: {}";
    public static final String CANNOT_PARSE_URL = "Cannot parse url: ";
    public static final String FORWARD_PAGES_NOT_FOUND_JSP = "forward:pages/not-found.jsp";
    public static final String SERVLETS_S_S = "servlets.%s.%s";
    public static final String LOG = "log";
    public static final String AUTH_CONTROLLER_FILTER_AND_PROCESS_REQUEST_EXECUTION_TIME_MS = "AuthController filter and process request execution time: {}ms";
    public static final String FULLPATH = "Fullpath: {}";
    public static final String CLASSNAME = "Classname: {}";
    public static final String ACTION = "Action: {}";
    public static final String ACTION_REQUEST = "action";
    public static final String INITIALIZING_AUTH_CONTROLLER_FILTER = "Initializing AuthController filter";
    public static final String ERROR_ON_AUTH_CONTROLLER_FILTER = "error on AuthController filter";
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
        StopWatch sw = new StopWatch();
        sw.start();

        logger.info(INITIALIZING_AUTH_CONTROLLER_FILTER);
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String strAction = req.getParameter(ACTION_REQUEST);
        logger.info(ACTION, strAction);
        String classname = getClassname(req, strAction);
        logger.info(CLASSNAME, classname);
        String fullpath = executeAction(req, resp, classname);
        logger.info(FULLPATH, fullpath);
        processRequest(req, resp, fullpath);

        sw.stop();
        logger.info(AUTH_CONTROLLER_FILTER_AND_PROCESS_REQUEST_EXECUTION_TIME_MS, sw.getTime());
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
        if (strAction.contains(LOG)) {
            classname = LOGIN_SERVLET;
        } else {
            int entityPos = req.getServletPath().lastIndexOf("/") + 1;
            String entityName = req.getServletPath().substring(entityPos);
            classname = String.format(SERVLETS_S_S, entityName, getServletClass(entityName));
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
            logger.error(ERROR_ON_AUTH_CONTROLLER_FILTER);
            fullpath = FORWARD_PAGES_NOT_FOUND_JSP;
            try {
                req.getRequestDispatcher(WEB_INF_VIEW + fullpath).forward(req, resp);
            } catch (IOException e) {
                logger.error(ERROR_ON_FORWARD, e.getMessage());
                e.printStackTrace();
            }
        }

        String[] path;
        try {
            logger.info(PATH, fullpath);
            path = fullpath.split(":");
        } catch (Exception e) {
            logger.error(ERROR_ON_PARSE_URL, e.getMessage());
            throw new ServletException(CANNOT_PARSE_URL + fullpath);
        }

        String pathAction = path[0];
        String pathUrl = path[1];

        if (pathAction.equals(FORWARD)) {
            try {
                logger.info(FORWARD_TO, pathUrl);
                req.getRequestDispatcher(WEB_INF_VIEW + pathUrl).forward(req, resp);
            } catch (IOException | ServletException e) {
                logger.error(ERROR_ON_FORWARD, e.getMessage());
                e.printStackTrace();
            }
        } else {
            try {
                logger.info(REDIRECT_TO, pathUrl);
                resp.sendRedirect(pathUrl);
            } catch (IOException e) {
                logger.error(ERROR_ON_REDIRECT, e.getMessage());
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
        return entityName.substring(0, 1).toUpperCase() + entityName.substring(1).concat(SERVLET);
    }

}
