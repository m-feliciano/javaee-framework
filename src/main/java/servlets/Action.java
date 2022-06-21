package servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Action {

	String doService(HttpServletRequest req, HttpServletResponse resp);

}
