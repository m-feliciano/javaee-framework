package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mchange.util.AssertException;

import infra.exceptions.CustomRuntimeException;

public final class DateFormatter {

	private DateFormatter() {
		throw new AssertException("This class must not be instantiated.");
	}

	public static Date From(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(strDate);
		} catch (ParseException e) {
			throw new CustomRuntimeException("Cannot convert data from: " + date);
		}
		return date;
	}

}
