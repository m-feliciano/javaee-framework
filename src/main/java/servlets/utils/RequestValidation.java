package servlets.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface RequestValidation {
    boolean validate(HttpServletRequest req, HttpServletResponse resp);

}
