package com.dev.servlet.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class URIUtils {

    /**
     * the pattern is used to match the URI like /{service}/{action}/{id} (optional) or /{service}/{action}?{query}
     */
    public static final Pattern P_URI = Pattern.compile("/[^/]+(?:/([^/]+))?(?:/([^/]+))?(?:/([^/]+))?");
    public static final Pattern P_ID = Pattern.compile("id=\\d+");
    public static final Pattern P_NUMBER = Pattern.compile("\\d+");

    private URIUtils() {
        // Empty constructor
    }

    /**
     * This method is used to get the service name from the request.
     *
     * @param request
     * @return
     */
    public static String service(HttpServletRequest request) {
        String uri = request.getRequestURI();
        Matcher matcher = P_URI.matcher(uri);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Get the action from the request.
     *
     * @param request
     * @return String
     */
    public static String action(HttpServletRequest request) {
        String uri = request.getRequestURI();
        Matcher matcher = P_URI.matcher(uri);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return null;
    }

    /**
     * Return the path variable from the request.
     *
     * @param httpServletRequest
     * @return
     */
    public static Long recourceId(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getParameter("id") != null) {
            return Long.parseLong(httpServletRequest.getParameter("id"));
        }

//        if (httpServletRequest.getQueryString() != null) {
//            Matcher matcher = P_ID.matcher(httpServletRequest.getQueryString());
//            if (matcher.find()) {
//                String[] split = matcher.group().split("=");
//                return Long.parseLong(split[1]);
//            }
//        }

        Matcher matcher = P_URI.matcher(httpServletRequest.getRequestURI());

        if (matcher.find()) {
            String pathVariable = matcher.group(3);

            if (pathVariable != null) {
                Matcher matcherPathVariable = P_NUMBER.matcher(pathVariable);
                if (matcherPathVariable.find()) {
                    return Long.parseLong(matcherPathVariable.group());
                }
            }
        }

        return null;
    }
}
