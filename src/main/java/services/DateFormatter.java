package services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

	public static Date From(String date) {
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = null;
		try {
			date1 = formatter1.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException("Cannot convert data from: " + date);
		}
		return date1;
	}

}
