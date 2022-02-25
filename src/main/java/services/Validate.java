package services;

import javax.servlet.http.HttpServletRequest;

public class Validate {

	public static boolean isValid(HttpServletRequest req, String value) {
		if (value.isBlank() || value == null || value == "") {
			return false;
		}
		return true;
	}

}
