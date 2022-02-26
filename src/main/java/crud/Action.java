package crud;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Action {

	public String doService(HttpServletRequest req, HttpServletResponse resp);

}
