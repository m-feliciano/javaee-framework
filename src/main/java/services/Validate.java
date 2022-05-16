package services;

import javax.servlet.http.HttpServletRequest;

public class Validate {

	public static boolean isValid(HttpServletRequest req, String obj) {
		String val = req.getParameter(obj);
		return !(val.isBlank() || val == null || val == "");
	}

}
