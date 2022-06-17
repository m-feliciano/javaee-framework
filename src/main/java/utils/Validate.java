package utils;

import javax.servlet.http.HttpServletRequest;

import com.mchange.util.AssertException;

public final class Validate {

	private Validate() {
		throw new AssertException("This class must not be instantiated.");
	}

	public static boolean isValid(HttpServletRequest req, String obj) {
		String val = req.getParameter(obj);
		return !(val.isBlank() || val == null || val == "");
	}

}
