package com.dev.servlet.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateFormatter {

	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	public static SimpleDateFormat sdf;

	private DateFormatter() {
		sdf = new SimpleDateFormat(YYYY_MM_DD);
	}

	/**
	 * This method is used get a date from a string. The date is formatted to a
	 * string.
	 *
	 * @param strDate
	 * @return String with date format
	 */

	public static Date from(String strDate) {
		Date date;
		try {
			date = sdf.parse(strDate);
		} catch (ParseException e) {
			throw new RuntimeException("Cannot convert data from: " + strDate);
		}
		return date;
	}

}
