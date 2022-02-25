package services;

import javax.servlet.http.HttpServletRequest;

public class Validate {

	public static boolean isValid(HttpServletRequest req, String obj) {
		String val = req.getParameter(obj);
		if (val.isBlank() || val == null || val == "") {
			return false;
		}
		return true;
	}

}
