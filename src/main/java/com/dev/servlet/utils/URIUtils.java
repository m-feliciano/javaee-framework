package com.dev.servlet.utils;

import javax.servlet.http.HttpServletRequest;

public class URIUtils {

    private static final String PACKAGE = "com.dev.servlet.business.%s";

    private URIUtils() {
        // Empty constructor
    }

    /**
     * This method is used to get the action from the request
     *
     * @param request
     * @return
     */
    public static String getAction(HttpServletRequest request) {
        if (request == null) return null;

        String queryString = request.getQueryString();
        String action = null;
        if (queryString != null) {
            for (String param : queryString.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && "action".equals(keyValue[0])) {
                    action = keyValue[1];
                    break;
                }
            }
        }

        return action;
    }

    /**
     * Get the class name
     *
     * @param request
     * @return String
     */
    public static String getClassName(HttpServletRequest request) {
        String classname;
        int entityPos = request.getServletPath().lastIndexOf("/") + 1;
        // fully qualified name
        String entityName = request.getServletPath().substring(entityPos);
        classname = String.format(PACKAGE, getServletClass(entityName));
        return classname;
    }

    /**
     * format the class name to be used in the classpath
     *
     * @param entityName
     * @return
     */
    private static String getServletClass(String entityName) {
        return entityName.substring(0, 1).toUpperCase() + entityName.substring(1) + "Business";
    }

}
